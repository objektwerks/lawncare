DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

/*
final case class Property(id: Long = 0,
                          license: String = Pin.newInstance,
                          pin: String = Pin.newInstance,
                          owner: String,
                          email: String = "",
                          location: String,
                          joined: Long) extends Entity

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
                         occured: Long = 0) extends Entity
*/

CREATE TABLE property (
  id BIGSERIAL PRIMARY KEY,
  license CHAR(7) UNIQUE NOT NULL,
  pin CHAR(7) UNIQUE NOT NULL,
  owner VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  location VARCHAR NOT NULL,
  joined BIGINT NOT NULL
);

CREATE TABLE session (
  id BIGSERIAL PRIMARY KEY,
  walker_id BIGINT REFERENCES walker(id),
  weight INT NOT NULL,
  weight_unit CHAR(2) NOT NULL,
  distance INT NOT NULL,
  distance_unit CHAR(2) NOT NULL,
  hours INT NOT NULL,
  minutes INT NOT NULL,
  calories INT NOT NULL,
  datetime BIGINT NOT NULL
);