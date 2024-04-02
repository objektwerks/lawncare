package lawncare

import java.time.LocalDate

sealed trait Entity:
  val id: Long

final case class Property(id: Long = 0,
                          license: String = Pin.newInstance,
                          pin: String = Pin.newInstance,
                          owner: String,
                          email: String = "",
                          location: String,
                          joined: String = LocalDate.now().toString()) extends Entity

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
                         occured: String = LocalDate.now().toString()) extends Entity