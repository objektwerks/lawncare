package lawncare

/*
mow
edge ( driveway, curbing )
trim ( plant, tree )
clean ( blower )
fertilize ( nitrogen, phosphorus and potassium )
pesticide ( herbicides, insecticides, nematicides, fungicides )
water ( sprinkler system, pipes, heads, on/off | uncapped/capped )
repair ( sprinkler system )
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
                         trimmed: Boolean = true,
                         cleaned: Boolean = true,
                         fertilized: Boolean = false,
                         pesticided: Boolean = false,
                         weeded: Boolean = false,
                         repaired: Boolean = false, // sprinkler
                         occured: Long = 0) extends Entity