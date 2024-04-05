package lawncare

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import java.time.LocalDate
import java.util.UUID

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
                          joined: String = Entity.now()) extends Entity

object Property:
  given JsonValueCodec[Property] = JsonCodecMaker.make[Property]
  given Ordering[Property] = Ordering.by[Property, String](property => property.joined).reverse

final case class Session(id: Long = 0,
                         propertyId: Long,
                         mowed: Boolean = true,
                         edged: Boolean = true,
                         cleaned: Boolean = true,
                         trimmed: Boolean = false,
                         fertilized: Boolean = false,
                         pesticided: Boolean = false,
                         weeded: Boolean = false,
                         watered: Boolean = false,
                         repaired: Boolean = false,
                         note: String = "",
                         occured: String = Entity.now()) extends Entity

object Session:
  given JsonValueCodec[Session] = JsonCodecMaker.make[Session]
  given Ordering[Session] = Ordering.by[Session, String](session => session.occured).reverse