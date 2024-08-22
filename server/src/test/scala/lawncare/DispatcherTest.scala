package lawncare

import com.typesafe.config.ConfigFactory

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ox.supervised
import ox.IO.globalForTesting.given

import scala.sys.process.Process

import Validator.*

final class DispatcherTest extends AnyFunSuite with Matchers:
  val exitCode = Process("psql -d lawncare -f ddl.sql").run().exitValue()
  exitCode shouldBe 0

  val config = ConfigFactory.load("test.conf")

  val store = Store(config)
  val emailer = Emailer(config)
  val dispatcher = Dispatcher(store, emailer)

  var testAccount = Account()
  var testProperty = Property(accountId = 0, location = "a")
  var testSession = Session(propertyId = 0)
  var testIssue = Issue(propertyId = 0, report = "sprinkler broken")

  test("dispatcher"):
    supervised:
      register
      login

      addProperty
      updateProperty
      listProperties

      addSession
      updateSession
      listSessions

      fault

  def register: Unit =
    val register = Register(config.getString("email.sender"))
    dispatcher.dispatch(register) match
      case Registered(account) =>
        assert( account.isValid )
        testAccount = account
      case fault => fail(s"Invalid registered event: $fault")

  def login: Unit =
    val login = Login(testAccount.email, testAccount.pin)
    dispatcher.dispatch(login) match
      case LoggedIn(account) => account shouldBe testAccount
      case fault => fail(s"Invalid loggedin event: $fault")

  def addProperty: Unit =
    testProperty = testProperty.copy(accountId = testAccount.id, location = testProperty.location)
    val saveProperty = SaveProperty(testAccount.license, testProperty)
    dispatcher.dispatch(saveProperty) match
      case PropertySaved(id) =>
        id should not be 0
        testProperty = testProperty.copy(id = id)
        testSession = testSession.copy(propertyId = id)
      case fault => fail(s"Invalid property saved event: $fault")

  def updateProperty: Unit =
    testProperty = testProperty.copy(location = "z")
    val saveProperty = SaveProperty(testAccount.license, testProperty)
    dispatcher.dispatch(saveProperty) match
      case PropertySaved(id) => id shouldBe testProperty.id
      case fault => fail(s"Invalid property saved event: $fault")

  def listProperties: Unit =
    val listProperties = ListProperties(testAccount.license, testProperty.accountId)
    dispatcher.dispatch(listProperties) match
      case PropertiesListed(properties) =>
        properties.length shouldBe 1
        properties.head shouldBe testProperty
      case fault => fail(s"Invalid properties listed event: $fault")

  def addSession: Unit =
    val saveSession = SaveSession(testAccount.license, testSession)
    dispatcher.dispatch(saveSession) match
      case SessionSaved(id) =>
        id should not be 0
        testSession = testSession.copy(id = id)
      case fault => fail(s"Invalid session saved event: $fault")

  def updateSession: Unit =
    testSession = testSession.copy(note = "trimmer broke")
    val saveSession = SaveSession(testAccount.license, testSession)
    dispatcher.dispatch(saveSession) match
      case SessionSaved(id) => id shouldBe testSession.id
      case fault => fail(s"Invalid session saved event: $fault")

  def listSessions: Unit =
    val listSessions = ListSessions(testAccount.license, testProperty.id)
    dispatcher.dispatch(listSessions) match
      case SessionsListed(sessions) =>
        sessions.length shouldBe 1
        sessions.head shouldBe testSession
      case fault => fail(s"Invalid sessions listed event: $fault")

  def addIssue: Unit =
    val saveIssue = SaveIssue(testAccount.license, testIssue)
    dispatcher.dispatch(saveIssue) match
      case IssueSaved(id) =>
        id should not be 0
        testIssue = testIssue.copy(id = id)
      case fault => fail(s"Invalid issue saved event: $fault")

  def updateIssue: Unit =
    testIssue = testIssue.copy(resolution = "fixed sprinkler", resolved = Entity.now)
    val saveIssue = SaveIssue(testAccount.license, testIssue)
    dispatcher.dispatch(saveIssue) match
      case IssueSaved(id) => id shouldBe testIssue.id
      case fault => fail(s"Invalid issue saved event: $fault")

  def listIssues: Unit =
    val listIssues = ListIssues(testAccount.license, testProperty.id)
    dispatcher.dispatch(listIssues) match
      case IssuesListed(issues) =>
        issues.length shouldBe 1
        issues.head shouldBe testIssue
      case fault => fail(s"Invalid issues listed event: $fault")

  def fault: Unit =
    val fault = Fault("error message")
    store.addFault(fault)
    store.listFaults().length shouldBe 1