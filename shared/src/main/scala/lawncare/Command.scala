package lawncare

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

sealed trait Command

object Command:
  given JsonValueCodec[Command] = JsonCodecMaker.make[Command]
  given JsonValueCodec[License] = JsonCodecMaker.make[License]

  given JsonValueCodec[Register] = JsonCodecMaker.make[Register]


sealed trait License:
  val license: String

final case class Register(email: String, location: String) extends Command
final case class Login(email: String, pin: String) extends Command

final case class ListProperties(license: String) extends Command with License
final case class SaveProperty(license: String, property: Property) extends Command with License
