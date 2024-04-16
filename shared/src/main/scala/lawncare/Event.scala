package lawncare

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import java.time.{Instant, LocalDate}

import scalafx.beans.property.ObjectProperty

sealed trait Event

object Event:
  given JsonValueCodec[Event] = JsonCodecMaker.make[Event]

  given JsonValueCodec[Registered] = JsonCodecMaker.make[Registered]
  given JsonValueCodec[LoggedIn] = JsonCodecMaker.make[LoggedIn]

  given JsonValueCodec[PropertiesListed] = JsonCodecMaker.make[PropertiesListed]
  given JsonValueCodec[PropertySaved] = JsonCodecMaker.make[PropertySaved]

  given JsonValueCodec[SessionsListed] = JsonCodecMaker.make[SessionsListed]
  given JsonValueCodec[SessionSaved] = JsonCodecMaker.make[SessionSaved]

  given JsonValueCodec[Fault] = JsonCodecMaker.make[Fault]

final case class Authorized(isAuthorized: Boolean) extends Event

final case class Registered(account: Account) extends Event
final case class LoggedIn(account: Account) extends Event

final case class PropertiesListed(properties: List[Property]) extends Event
final case class PropertySaved(id: Long) extends Event

final case class SessionsListed(sessions: List[Session]) extends Event
final case class SessionSaved(id: Long) extends Event

final case class IssuesListed(issues: List[Issue]) extends Event
final case class IssueSaved(id: Long) extends Event

object Fault:
  def apply(throwable: Throwable, defaultMessage: String): Fault =
    val message = throwable.getMessage
    Fault(
      if message == null then defaultMessage
      else message
    )

  def apply(prefixMessage: String, throwable: Throwable): Fault =
    val message = throwable.getMessage
    Fault(
      if message == null then prefixMessage
      else s"$prefixMessage $message"
    )

  given faultOrdering: Ordering[Fault] = Ordering.by[Fault, Long](f => LocalDate.parse(f.occurred).toEpochDay()).reverse

final case class Fault (cause: String, occurred: String = Instant.now.toString) extends Event:
  val causeProperty = ObjectProperty[String](this, "cause", cause)
  val occurredProperty = ObjectProperty[String](this, "occurred", occurred)

final case class FaultAdded() extends Event