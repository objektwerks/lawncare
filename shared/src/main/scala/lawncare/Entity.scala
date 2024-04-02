package lawncare

/*
mow
edge ( driveway, curbing )
trim ( plant, tree )
fertilize ( nitrogen, phosphorus and potassium )
pesticide ( herbicides, insecticides, nematicides, fungicides )
water ( sprinkler system, pipes, heads, on/off | uncapped/capped )
*/
sealed trait Entity:
  val id: Long

final case class Mow(id: Long = 0) extends Entity

final case class Trim(id: Long = 0) extends Entity

final case class Fertilize(id: Long = 0) extends Entity

final case class Pesticide(id: Long = 0) extends Entity

final case class Water(id: Long = 0) extends Entity