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

final case class Mowing(id: Long = 0) extends Entity

final case class Trimming(id: Long = 0) extends Entity

final case class Fertilizing(id: Long = 0) extends Entity

final case class Pesticiding(id: Long = 0) extends Entity

final case class Watering(id: Long = 0) extends Entity