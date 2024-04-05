package lawncare

import scala.util.Try
import scala.util.control.NonFatal

import Validator.*

final class Dispatcher(store: Store, emailer: Emailer):
  def dispatch[E <: Event](command: Command): Event =
    if !command.isValid then store.addFault( Fault(s"Command is invalid: $command") )
    
    isAuthorized(command) match
      case Authorized(isAuthorized) => if !isAuthorized then store.addFault( Fault(s"License is unauthorized: $command") )
      case fault @ Fault(_, _) => store.addFault(fault)
      case _ =>
        
    val event = command match
      case Register(emailAddress)       => register(emailAddress)
      case Login(emailAddress, pin)     => login(emailAddress, pin)
      case ListProperties(_, accountId) => listProperties(accountId)
      case SaveProperty(_, property)    => saveProperty(property)
      case ListSessions(_, walkerId)    => listSessions(walkerId)
      case SaveSession(_, session)      => saveSession(session)
      case AddFault(_, fault)           => addFault(fault)

    event match
      case fault @ Fault(_, _) => store.addFault(fault)
      case _ => event

  private def isAuthorized(command: Command): Event =
    command match
      case license: License =>
        Try {
          Authorized( store.isAuthorized(license.license) )
        }.recover { case NonFatal(error) => Fault(s"Authorization failed: $error") }
         .get
      case Register(_) | Login(_, _) => Authorized(true)

  private def register(email: String): Event =
    Try {
      val account = Account(email = email)
      send(account.email, account.pin)
      Registered( store.register(account) )
    }.recover { case NonFatal(error) => Fault(s"Registration failed for: $email, because: ${error.getMessage}") }
     .get

  private def send(email: String, pin: String): Unit =
    val recipients = List(email)
    val message = s"<p>This is your new pin: <b>${pin}</b> Welcome aboard!</p>"
    emailer.send(recipients, message)

  private def login(email: String, pin: String): Event =
    Try { store.login(email, pin) }.fold(
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

  private def listSessions(swimmerId: Long): Event =
    Try {
      SessionsListed( store.listSessions(swimmerId) )
    }.recover { case NonFatal(error) => Fault("List sessions failed:", error) }
     .get

  private def saveSession(session: Session): Event =
    Try {
      SessionSaved(
        if session.id == 0 then store.addSession(session)
        else store.updateSession(session)
      )
    }.recover { case NonFatal(error) => Fault("Save cleaning failed:", error) }
     .get

  private def addFault(fault: Fault): Event =
    Try {
      store.addFault(fault)
      FaultAdded()
    }.recover { case NonFatal(error) => Fault("Add fault failed:", error) }
     .get