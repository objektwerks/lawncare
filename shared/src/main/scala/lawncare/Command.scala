package lawncare

sealed trait Command

final case class Register(email: String, location: String) extends Command
final case class Login(email: String, pin: String) extends Command
