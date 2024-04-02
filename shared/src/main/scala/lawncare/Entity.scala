package lawncare

/*
mowing
edging ( driveway, curbing )
trimming ( plant, tree )
fertilizing ( nitrogen, phosphorus and potassium )
pesticiding ( herbicides, insecticides, nematicides, fungicides )
watering ( sprinkler system, pipes, heads, on/off | uncapped/capped )
weeding
*/
sealed trait Entity:
  val id: Long

final case class Mow(id: Long = 0) extends Entity

final case class Trim(id: Long = 0) extends Entity

final case class Fertilize(id: Long = 0) extends Entity

final case class Pesticide(id: Long = 0) extends Entity

final case class Water(id: Long = 0) extends Entity

final case class Weeding(id: Long = 0) extends Entity