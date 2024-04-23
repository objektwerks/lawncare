package lawncare

import com.typesafe.scalalogging.LazyLogging

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty

import Fault.given

final class Model(fetcher: Fetcher) extends LazyLogging:
  val shouldBeInFxThread = (message: String) => require(Platform.isFxApplicationThread, message)

  val registered = ObjectProperty[Boolean](true)
  val loggedin = ObjectProperty[Boolean](true)

  val selectedPropertyId = ObjectProperty[Long](0)
  val selectedSessionId = ObjectProperty[Long](0)
  val selectedIssueId = ObjectProperty[Long](0)

  selectedPropertyId.onChange { (_, oldPropertyId, newPropertyId) =>
    logger.info("*** selected property id onchange event: {} -> {}", oldPropertyId, newPropertyId)
    shouldBeInFxThread("*** selected property id onchange should be in fx thread.")
    sessions(newPropertyId)
  }

  val objectAccount = ObjectProperty[Account](Account.empty)
  val observableProperties = ObservableBuffer[Property]()
  val observableSessions = ObservableBuffer[Session]()
  val observableIssues = ObservableBuffer[Issue]()
  val observableFaults = ObservableBuffer[Fault]()

  objectAccount.onChange { (_, oldAccount, newAccount) =>
    logger.info("*** object account onchange event: {} -> {}", oldAccount, newAccount)
  }

  observableProperties.onChange { (_, changes) =>
    logger.info("*** observable properties onchange event: {}", changes)
  }

  observableSessions.onChange { (_, changes) =>
    logger.info("*** observable sessions onchange event: {}", changes)
  }

  observableIssues.onChange { (_, changes) =>
    logger.info("*** observable issues onchange event: {}", changes)
  }

  def onFetchFault(source: String, fault: Fault): Unit =
    val cause = s"$source - $fault"
    logger.error("*** cause: {}", cause)
    observableFaults += fault.copy(cause = cause)

  def onFetchFault(source: String, entity: Entity, fault: Fault): Unit =
    val cause = s"$source - $entity - $fault"
    logger.error("*** cause: {}", cause)
    observableFaults += fault.copy(cause = cause)

  def add(fault: Fault): Unit =
    fetcher.fetch(
      AddFault(objectAccount.get.license, fault),
      (event: Event) => event match
        case fault @ Fault(cause, _) => onFetchFault("Model.add fault", fault)
        case FaultAdded() =>
          observableFaults += fault
          observableFaults.sort()
        case _ => ()
    )

  def register(register: Register): Unit =
    fetcher.fetch(
      register,
      (event: Event) => event match
        case fault @ Fault(_, _) => registered.set(false)
        case Registered(account) => objectAccount.set(account)
        case _ => ()
    )

  def login(login: Login): Unit =
    fetcher.fetch(
      login,
      (event: Event) => event match
        case fault @ Fault(_, _) => loggedin.set(false)
        case LoggedIn(account) =>
          objectAccount.set(account)
          properties()
        case _ => ()
    )

  def properties(): Unit =
    fetcher.fetch(
      ListProperties(objectAccount.get.license, objectAccount.get.id),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.properties", fault)
        case PropertiesListed(properties) =>
          observableProperties.clear()
          observableProperties ++= properties
        case _ => ()
    )

  def add(selectedIndex: Int, property: Property)(runLast: => Unit): Unit =
    fetcher.fetch(
      SaveProperty(objectAccount.get.license, property),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.save property", property, fault)
        case PropertySaved(id) =>
          observableProperties += property.copy(id = id)
          observableProperties.sort()
          selectedPropertyId.set(id)
          runLast
        case _ => ()
    )

  def update(selectedIndex: Int, property: Property)(runLast: => Unit): Unit =
    fetcher.fetch(
      SaveProperty(objectAccount.get.license, property),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.save property", property, fault)
        case PropertySaved(id) =>
          observableProperties.update(selectedIndex, property)
          runLast
        case _ => ()
    )

  def sessions(propertyId: Long): Unit =
    fetcher.fetch(
      ListSessions(objectAccount.get.license, propertyId),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.sessions", fault)
        case SessionsListed(sessions) =>
          observableSessions.clear()
          observableSessions ++= sessions
        case _ => ()
    )

  def add(selectedIndex: Int, session: Session)(runLast: => Unit): Unit =
    fetcher.fetch(
      SaveSession(objectAccount.get.license, session),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.save session", session, fault)
        case SessionSaved(id) =>
          observableSessions += session.copy(id = id)
          observableSessions.sort()
          selectedSessionId.set(id)
          runLast
        case _ => ()
    )

  def update(selectedIndex: Int, session: Session)(runLast: => Unit): Unit =
    fetcher.fetch(
      SaveSession(objectAccount.get.license, session),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.save session", session, fault)
        case SessionSaved(id) =>
          observableSessions.update(selectedIndex, session)
          runLast
        case _ => ()
    )

  def issues(propertyId: Long): Unit =
    fetcher.fetch(
      ListIssues(objectAccount.get.license, propertyId),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.issues", fault)
        case IssuesListed(issues) =>
          observableIssues.clear()
          observableIssues ++= issues
        case _ => ()
    )

  def add(selectedIndex: Int, issue: Issue)(runLast: => Unit): Unit =
    fetcher.fetch(
      SaveIssue(objectAccount.get.license, issue),
      (event: Event) => event match
        case fault @ Fault(_, _) => onFetchFault("Model.save issue", issue, fault)
        case IssueSaved(id) =>
          observableIssues += issue.copy(id = id)
          observableIssues.sort()
          selectedIssueId.set(id)
          runLast
        case _ => ()
    )