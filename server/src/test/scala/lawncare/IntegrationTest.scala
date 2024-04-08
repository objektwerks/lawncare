package lawncare

import com.typesafe.config.ConfigFactory

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.*
import scala.sys.process.Process

import Validator.*

final class IntegrationTest extends AnyFunSuite with Matchers:
  val exitCode = Process("psql -d lawncare -f ddl.sql").run().exitValue()
  exitCode shouldBe 0

  val config = ConfigFactory.load("test.conf")

  val store = Store(config, Store.cache(minSize = 1, maxSize = 1, expireAfter = 1.hour))
  val emailer = Emailer(config)
  val dispatcher = Dispatcher(store, emailer)

  var testAccount = Account()
  var testProperty = Property(accountId = 0, location = "a")
  var testSession = Session(propertyId = 0)

  test("integration"):
    register
    login

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

  def fault: Unit =
    val fault = Fault("error message")
    store.addFault(fault)
    store.listFaults().length shouldBe 1