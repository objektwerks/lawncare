package lawncare

/*
mow
edge ( driveway, curbing )
trim ( plant, tree )
clean ( blower )
fertilize ( nitrogen, phosphorus and potassium )
pesticide ( herbicides, insecticides, nematicides, fungicides )
water
repair ( sprinkler system, pipes, heads, on/off | uncapped/capped )
weeding
*/
sealed trait Entity:
  val id: Long

final case class Property(id: Long = 0,
                          owner: String,
                          location: String) extends Entity

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
                         occured: Long = 0) extends Entity