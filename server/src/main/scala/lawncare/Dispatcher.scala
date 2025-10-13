package lawncare

import ox.supervised
import ox.resilience.retry
import ox.scheduling.Schedule

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

import Validator.*

final class Dispatcher(store: Store, emailer: Emailer):
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
        try
          supervised:
            retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        catch
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
      case Register(_) | Login(_, _) => Authorized

  private def sendEmail(email: String, message: String): Boolean =
    val recipients = List(email)
    emailer.send(recipients, message)

  private def register(email: String): Event =
    try
      supervised:
        val account = Account(email = email)
        val message = s"Your new pin is: ${account.pin}\n\nWelcome aboard!"
        val result = retry( Schedule.fixedInterval(600.millis).maxAttempts(1) )( sendEmail(account.email, message) )
        if result then
          Registered( store.register(account) )
        else
          throw IllegalArgumentException("Invalid email address.")
    catch
      case NonFatal(error) => Fault(s"Registration failed for: $email, because: ${error.getMessage}")

  private def login(email: String, pin: String): Event =
    Try:
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.login(email, pin) )
    .fold(
      error => Fault("Login failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn(optionalAccount.get)
        else Fault(s"Login failed for email address: $email and pin: $pin")
    )

  private def listProperties(accountId: Long): Event =
    try
      PropertiesListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.listProperties(accountId) )
      )
    catch
      case NonFatal(error) => Fault("List properties failed:", error)

  private def saveProperty(property: Property): Event =
    try
      PropertySaved(
        supervised:
          if property.id == 0 then retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.addProperty(property) )
          else retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.updateProperty(property) )
      )
    catch
      case NonFatal(error) => Fault("Save property failed:", error)

  private def listSessions(propertyId: Long): Event =
    try
      SessionsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.listSessions(propertyId) )
      )
    catch
      case NonFatal(error) => Fault("List sessions failed:", error)

  private def saveSession(session: Session): Event =
    try
      SessionSaved(
        supervised:
          if session.id == 0 then retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.addSession(session) )
          else retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.updateSession(session) )
      )
    catch
      case NonFatal(error) => Fault("Save session failed:", error)

  private def listIssues(propertyId: Long): Event =
    try
      IssuesListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxAttempts(1) )( store.listIssues(propertyId) )
      )
    catch
      case NonFatal(error) => Fault("List issues failed:", error)

  private def saveIssue(license: String, issue: Issue): Event =
    try
      IssueSaved(
        supervised:
          retry( Schedule.fixedInterval(600.millis).maxRepeats(1) ){
            if issue.id == 0 then store.addIssue(issue)
            else
              if store.isIssueResolved(issue) then
                store
                  .getAccountEmail(license)
                  .fold(())(email => sendEmail(email, s"Issue resolved: [${issue.id}] : ${issue.report}"))
              store.updateIssue(issue)
          }
      )
    catch
      case NonFatal(error) => Fault("Save issue failed:", error)

  private def addFault(fault: Fault): Event =
    try
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addFault(fault) )
        FaultAdded()
    catch
      case NonFatal(error) => Fault("Add fault failed:", error)