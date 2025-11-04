package lawncare

import com.typesafe.scalalogging.LazyLogging

import ox.supervised

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty

import Fault.given

final class Model(fetcher: Fetcher) extends LazyLogging:
  def assertInFxThread(message: String, suffix: String = " should be in fx thread!"): Unit =
    require(Platform.isFxApplicationThread, message + suffix)
  def assertNotInFxThread(message: String, suffix: String = " should not be in fx thread!"): Unit =
    require(!Platform.isFxApplicationThread, message + suffix)

  val registered = ObjectProperty[Boolean](true)
  val loggedin = ObjectProperty[Boolean](true)

  val selectedPropertyId = ObjectProperty[Long](0)
  val selectedSessionId = ObjectProperty[Long](0)
  val selectedIssueId = ObjectProperty[Long](0)

  selectedPropertyId.onChange { (_, _, newPropertyId) =>
    sessions(newPropertyId)
    issues(newPropertyId)
  }

  val objectAccount = ObjectProperty[Account](Account.empty)
  val observableProperties = ObservableBuffer[Property]()
  val observableSessions = ObservableBuffer[Session]()
  val observableIssues = ObservableBuffer[Issue]()
  val observableFaults = ObservableBuffer[Fault]()

  def onFetchFault(source: String, fault: Fault): Unit =
    val cause = s"$source - $fault"
    logger.error("*** cause: {}", cause)
    observableFaults += fault.copy(cause = cause)

  def onFetchFault(source: String, entity: Entity, fault: Fault): Unit =
    val cause = s"$source - $entity - $fault"
    logger.error("*** cause: {}", cause)
    observableFaults += fault.copy(cause = cause)

  def add(fault: Fault): Unit =
    supervised:
      assertNotInFxThread(s"add fault: $fault")
      fetcher.fetch(
        AddFault(objectAccount.get.license, fault),
        (event: Event) => event match
          case fault @ Fault(cause, _) => onFetchFault("add fault", fault)
          case FaultAdded(_) =>
            observableFaults += fault
            observableFaults.sort()
          case _ => ()
      )

  def register(register: Register): Unit =
    supervised:
      assertNotInFxThread(s"register: $register")
      fetcher.fetch(
        register,
        (event: Event) => event match
          case _ @ Fault(_, _) => registered.set(false)
          case Registered(account) => objectAccount.set(account)
          case _ => ()
      )

  def login(login: Login): Unit =
    supervised:
      assertNotInFxThread(s"login: $login")
      fetcher.fetch(
        login,
        (event: Event) => event match
          case _ @ Fault(_, _) => loggedin.set(false)
          case LoggedIn(account) =>
            objectAccount.set(account)
            properties()
          case _ => ()
      )

  def properties(): Unit =
    supervised:
      assertNotInFxThread("list properties")
      fetcher.fetch(
        ListProperties(objectAccount.get.license, objectAccount.get.id),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("properties", fault)
          case PropertiesListed(properties) =>
            observableProperties.clear()
            observableProperties ++= properties
          case _ => ()
      )

  def add(property: Property)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"add property: $property")
      fetcher.fetch(
        SaveProperty(objectAccount.get.license, property),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("add property", property, fault)
          case PropertySaved(id) =>
            observableProperties.insert(0, property.copy(id = id))
            observableProperties.sort()
            selectedPropertyId.set(id)
            logger.info(s"Added property: $property")
            runLast
          case _ => ()
      )

  def update(selectedIndex: Int, property: Property)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"update property from: $selectedIndex to: $property")
      fetcher.fetch(
        SaveProperty(objectAccount.get.license, property),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("update property", property, fault)
          case PropertySaved(id) =>
            if selectedIndex > -1 then
              observableProperties.update(selectedIndex, property)
              logger.info(s"Updated property from: $selectedIndex to: $property")
              runLast
            else
              logger.error(s"Update of property: $property \nfailed due to invalid index: $selectedIndex")
          case _ => ()
      )

  def sessions(propertyId: Long): Unit =
    supervised:
      assertNotInFxThread(s"list sessions, property id: $propertyId")
      fetcher.fetch(
        ListSessions(objectAccount.get.license, propertyId),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("sessions", fault)
          case SessionsListed(sessions) =>
            observableSessions.clear()
            observableSessions ++= sessions
          case _ => ()
      )

  def add(session: Session)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"add session: $session")
      fetcher.fetch(
        SaveSession(objectAccount.get.license, session),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("add session", session, fault)
          case SessionSaved(id) =>
            observableSessions.insert(0, session.copy(id = id))
            observableSessions.sort()
            selectedSessionId.set(id)
            logger.info(s"Added session: $session")
            runLast
          case _ => ()
      )

  def update(selectedIndex: Int, session: Session)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"update session from: $selectedIndex to: $session")
      fetcher.fetch(
        SaveSession(objectAccount.get.license, session),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("update session", session, fault)
          case SessionSaved(id) =>
            if selectedIndex > -1 then
              observableSessions.update(selectedIndex, session)      
              logger.info(s"Updated session from: $selectedIndex to: $session")
              runLast
            else
              logger.error(s"Update of session: $session \nfailed due to invalid index: $selectedIndex")
          case _ => ()
      )

  def issues(propertyId: Long): Unit =
    supervised:
      assertNotInFxThread(s"list issues, property id: $propertyId")
      fetcher.fetch(
        ListIssues(objectAccount.get.license, propertyId),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("issues", fault)
          case IssuesListed(issues) =>
            observableIssues.clear()
            observableIssues ++= issues
          case _ => ()
      )

  def add(issue: Issue)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"add issue: $issue")
      fetcher.fetch(
        SaveIssue(objectAccount.get.license, issue),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("add issue", issue, fault)
          case IssueSaved(id) =>
            observableIssues.insert(0, issue.copy(id = id))
            observableIssues.sort()
            selectedIssueId.set(id)
            logger.info(s"Added issue: $issue")
            runLast
          case _ => ()
      )

  def update(selectedIndex: Int, issue: Issue)(runLast: => Unit): Unit =
    supervised:
      assertNotInFxThread(s"update issue from: $selectedIndex to: $issue")
      fetcher.fetch(
        SaveIssue(objectAccount.get.license, issue),
        (event: Event) => event match
          case fault @ Fault(_, _) => onFetchFault("update issue", issue, fault)
          case IssueSaved(id) =>
            if selectedIndex > -1 then
              observableIssues.update(selectedIndex, issue)      
              logger.info(s"Updated issue from: $selectedIndex to: $issue")
              runLast
            else
              logger.error(s"Update of issue: $issue \nfailed due to invalid index: $selectedIndex")
          case _ => ()
      )