package lawncare

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import java.time.LocalDate

sealed trait Entity:
  val id: Long

object Entity:
  given JsonValueCodec[Entity] = JsonCodecMaker.make[Entity]

  def now(): String = LocalDate.now().toString()
  def localDate(now: String): LocalDate = LocalDate.parse(now)

final case class Property(id: Long = 0,
                          license: String = Pin.newInstance,
                          pin: String = Pin.newInstance,
                          owner: String,
                          email: String = "",
                          location: String,
                          joined: String = Entity.now()) extends Entity

object Property:
  given JsonValueCodec[Property] = JsonCodecMaker.make[Property]

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