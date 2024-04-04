package lawncare

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import java.time.{Instant, LocalDate}

import scalafx.beans.property.ObjectProperty

sealed trait Event

object Event:
  given JsonValueCodec[Event] = JsonCodecMaker.make[Event]

final case class Authorized(isAuthorized: Boolean) extends Event

final case class Registered(property: Property) extends Event


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