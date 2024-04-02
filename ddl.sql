DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

/*
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
  property_id BIGINT REFERENCES property(id),
  mowed BOOLEAN NOT NULL,

  occured BIGINT NOT NULL
);