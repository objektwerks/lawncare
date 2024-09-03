package lawncare

import ox.{IO, supervised}
import ox.resilience.{retry, RetryConfig}

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

import Validator.*

final class Dispatcher(store: Store, emailer: Emailer):
  def dispatch(command: Command): Event =
    IO.unsafe:
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

  private def isAuthorized(command: Command)(using IO): Security =
    command match
      case license: License =>
        try
          supervised:
            retry( RetryConfig.delay(1, 100.millis) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        catch
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
      case Register(_) | Login(_, _) => Authorized

  private def sendEmail(email: String, message: String): Unit =
    val recipients = List(email)
    emailer.send(recipients, message)

  private def register(email: String)(using IO): Event =
    try
      supervised:
        val account = Account(email = email)
        val message = s"Your new pin is: ${account.pin}\n\nWelcome aboard!"
        retry( RetryConfig.delay(1, 600.millis) )( sendEmail(account.email, message) )
        Registered( store.register(account) )
    catch
      case NonFatal(error) => Fault(s"Registration failed for: $email, because: ${error.getMessage}")

  private def login(email: String, pin: String)(using IO): Event =
    Try:
      supervised:
        retry( RetryConfig.delay(1, 100.millis) )( store.login(email, pin) )
    .fold(
      error => Fault("Login failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn(optionalAccount.get)
        else Fault(s"Login failed for email address: $email and pin: $pin")
    )

  private def listProperties(accountId: Long)(using IO): Event =
    Try:
      PropertiesListed(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )( store.listProperties(accountId) )
      )
    .recover:
      case NonFatal(error) => Fault("List properties failed:", error)
    .get

  private def saveProperty(property: Property)(using IO): Event =
    Try:
      PropertySaved(
        supervised:
          if property.id == 0 then retry( RetryConfig.delay(1, 100.millis) )( store.addProperty(property) )
          else retry( RetryConfig.delay(1, 100.millis) )( store.updateProperty(property) )
      )
    .recover:
      case NonFatal(error) => Fault("Save property failed:", error)
    .get

  private def listSessions(propertyId: Long)(using IO): Event =
    Try:
      SessionsListed(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )( store.listSessions(propertyId) )
      )
    .recover:
      case NonFatal(error) => Fault("List sessions failed:", error)
    .get

  private def saveSession(session: Session)(using IO): Event =
    Try:
      SessionSaved(
        supervised:
          if session.id == 0 then retry( RetryConfig.delay(1, 100.millis) )( store.addSession(session) )
          else retry( RetryConfig.delay(1, 100.millis) )( store.updateSession(session) )
      )
    .recover:
      case NonFatal(error) => Fault("Save session failed:", error)
    .get

  private def listIssues(propertyId: Long)(using IO): Event =
    Try:
      IssuesListed(
        supervised:
          retry( RetryConfig.delay(1, 100.millis) )( store.listIssues(propertyId) )
      )
    .recover:
      case NonFatal(error) => Fault("List issues failed:", error)
    .get

  private def saveIssue(license: String, issue: Issue)(using IO): Event =
    Try:
      IssueSaved(
        supervised:
          retry( RetryConfig.delay(1, 600.millis) ){
            if issue.id == 0 then store.addIssue(issue)
            else
              if store.isIssueResolved(issue) then
                store
                  .getAccountEmail(license)
                  .fold(())(email => sendEmail(email, s"Issue resolved: [${issue.id}] : ${issue.report}"))
              store.updateIssue(issue)
          }
      )
    .recover:
      case NonFatal(error) => Fault("Save issue failed:", error)
    .get

  private def addFault(fault: Fault)(using IO): Event =
    Try:
      supervised:
        retry( RetryConfig.delay(1, 100.millis) )( store.addFault(fault) )
        FaultAdded()
    .recover:
      case NonFatal(error) => Fault("Add fault failed:", error)
    .get