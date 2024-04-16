package lawncare

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import java.time.LocalDate
import java.util.UUID

import scalafx.beans.property.ObjectProperty

sealed trait Entity:
  val id: Long

object Entity:
  given JsonValueCodec[Entity] = JsonCodecMaker.make[Entity]

  def now(): String = LocalDate.now().toString()
  def localDate(now: String): LocalDate = LocalDate.parse(now)

final case class Account(id: Long = 0,
                         license: String = UUID.randomUUID.toString,
                         email: String = "",
                         pin: String = Pin.newInstance,
                         activated: String = Entity.now()) extends Entity

object Account:
  val empty = Account(license = "", email = "", pin = "")
  given JsonValueCodec[Account] = JsonCodecMaker.make[Account]

final case class Property(id: Long = 0,
                          accountId: Long,
                          location: String,
                          added: String = Entity.now()) extends Entity:
  val locationProperty = ObjectProperty[String](this, "location", location)
  val property = this

object Property:
  given JsonValueCodec[Property] = JsonCodecMaker.make[Property]
  given Ordering[Property] = Ordering.by[Property, String](property => property.added).reverse

final case class Session(id: Long = 0,
                         propertyId: Long,
                         mowed: Boolean = true,
                         edged: Boolean = true,
                         trimmed: Boolean = false,
                         blowed: Boolean = true,
                         fertilized: Boolean = false,
                         pesticided: Boolean = false,
                         weeded: Boolean = false,
                         watered: Boolean = false,
                         repaired: Boolean = false,
                         note: String = "",
                         occurred: String = Entity.now()) extends Entity:
  val mowedProperty = ObjectProperty[Boolean](this, "mowed", mowed)
  val edgedProperty = ObjectProperty[Boolean](this, "edged", edged)
  val trimmedProperty = ObjectProperty[Boolean](this, "trimmed", trimmed)
  val blowedProperty = ObjectProperty[Boolean](this, "blowed", blowed)
  val fertilizedProperty = ObjectProperty[Boolean](this, "fertilized", fertilized)
  val pesticidedProperty = ObjectProperty[Boolean](this, "pesticided", pesticided)
  val weededProperty = ObjectProperty[Boolean](this, "weeded", weeded)
  val wateredProperty = ObjectProperty[Boolean](this, "watered", watered)
  val repairedProperty = ObjectProperty[Boolean](this, "repaired", repaired)
  val noteProperty = ObjectProperty[String](this, "occurred", occurred)
  val occurredProperty = ObjectProperty[String](this, "occurred", occurred)
  val session = this

object Session:
  given JsonValueCodec[Session] = JsonCodecMaker.make[Session]
  given Ordering[Session] = Ordering.by[Session, String](session => session.occurred).reverse

final case class Issue(id: Long: = 0,
                       propertyId: Long,
                       reported: String = Entity.now(),
                       resolved: String = "") extends Entity:
  val reportedProperty = ObjectProperty[String](this, "reported", reported)
  val resolvedProperty = ObjectProperty[String](this, "resolved", resolved)
  val issue = this

object Issue:
  given JsonValueCodec[Issue] = JsonCodecMaker.make[Issue]