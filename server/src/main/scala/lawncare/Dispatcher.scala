package lawncare

import ox.{IO, supervised}
import ox.resilience.{retry, RetryConfig}

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

import Validator.*

final class Dispatcher(store: Store, emailer: Emailer)(using IO):
  def dispatch(command: Command): Event =
    command.isValid match
      case false => addFault( Fault(s"Invalid command: $command") )
      case true =>
        isAuthorized(command) match
          case Unauthorized(cause) => addFault( Fault(cause) )
          case Authorized =>
            command match
              case Register(emailAddress)       => register(emailAddress)
              case Login(emailAddress, pin)     => login(emailAddress, pin)
              case ListProperties(_, accountId) => listProperties(accountId)
              case SaveProperty(_, property)    => saveProperty(property)
              case ListSessions(_, propertyId)  => listSessions(propertyId)
              case SaveSession(_, session)      => saveSession(session)
              case ListIssues(_, propertyId)    => listIssues(propertyId)
              case SaveIssue(license, issue)    => saveIssue(license, issue)
              case AddFault(_, fault)           => addFault(fault)

  private def isAuthorized(command: Command): Security =
    command match
      case license: License =>
        Try:
          supervised:
            retry( RetryConfig.delay(1, 100.millis) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        .recover:
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
        .get
      case Register(_) | Login(_, _) => Authorized

  private def sendEmail(email: String, message: String): Unit =
    val recipients = List(email)
    emailer.send(recipients, message)

  private def register(email: String): Event =
    Try:
      supervised:
        val account = Account(email = email)
        val message = s"Your new pin is: ${account.pin}\n\nWelcome aboard!"
        retry( RetryConfig.delay(1, 600.millis) )( sendEmail(account.email, message) )
        Registered( store.register(account) )
    .recover:
      case NonFatal(error) => Fault(s"Registration failed for: $email, because: ${error.getMessage}")
    .get

  private def login(email: String, pin: String): Event =
    Try:
      supervised:
        retry( RetryConfig.delay(1, 100.millis) )( store.login(email, pin) )
    .fold(
      error => Fault("Login failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn(optionalAccount.get)
        else Fault(s"Login failed for email address: $email and pin: $pin")
    )

  private def listProperties(accountId: Long): Event =
    Try {
      PropertiesListed(store.listProperties(accountId))
    }.recover { case NonFatal(error) => Fault("List properties failed:", error) }
     .get

  private def saveProperty(property: Property): Event =
    Try {
      PropertySaved(
        if property.id == 0 then store.addProperty(property)
        else store.updateProperty(property)
      )
    }.recover { case NonFatal(error) => Fault("Save property failed:", error) }
     .get

  private def listSessions(propertyId: Long): Event =
    Try {
      SessionsListed( store.listSessions(propertyId) )
    }.recover { case NonFatal(error) => Fault("List sessions failed:", error) }
     .get

  private def saveSession(session: Session): Event =
    Try {
      SessionSaved(
        if session.id == 0 then store.addSession(session)
        else store.updateSession(session)
      )
    }.recover { case NonFatal(error) => Fault("Save session failed:", error) }
     .get

  private def listIssues(propertyId: Long): Event =
    Try {
      IssuesListed( store.listIssues(propertyId) )
    }.recover { case NonFatal(error) => Fault("List issues failed:", error) }
     .get

  private def saveIssue(license: String,
                        issue: Issue): Event =
    Try {
      IssueSaved(
        if issue.id == 0 then store.addIssue(issue)
        else
          if store.isIssueResolved(issue) then
            store
              .getAccountEmail(license)
              .fold(())(email => sendEmail(email, s"Issue resolved: [${issue.id}] : ${issue.report}"))
          store.updateIssue(issue)
      )
    }.recover { case NonFatal(error) => Fault("Save issue failed:", error) }
     .get

  private def addFault(fault: Fault): Event =
    Try {
      store.addFault(fault)
      FaultAdded()
    }.recover { case NonFatal(error) => Fault("Add fault failed:", error) }
     .get