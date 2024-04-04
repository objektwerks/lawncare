package lawncare

sealed trait Command

final case class Register(email: String, location: String) extends Command
