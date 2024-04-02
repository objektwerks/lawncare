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
  val occured: Long

final case class Mowing(id: Long = 0,
                        occured: Long = 0) extends Entity

final case class Trimming(id: Long = 0,
                          occured: Long = 0) extends Entity

final case class Fertilizing(id: Long = 0,
                             occured: Long = 0) extends Entity

final case class Pesticiding(id: Long = 0) extends Entity

final case class Watering(id: Long = 0) extends Entity

final case class Weeding(id: Long = 0) extends Entity