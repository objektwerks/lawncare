DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

CREATE TABLE account (
  id BIGSERIAL PRIMARY KEY,
  license CHAR(36) UNIQUE NOT NULL,
  email VARCHAR UNIQUE NOT NULL,
  pin CHAR(7) NOT NULL,
  activated VARCHAR(10) NOT NULL
);

CREATE TABLE property (
  id BIGSERIAL PRIMARY KEY,
  account_id BIGINT REFERENCES account(id),
  location VARCHAR NOT NULL,
  added VARCHAR(10) NOT NULL
);

CREATE TABLE session (
  id BIGSERIAL PRIMARY KEY,
  property_id BIGINT REFERENCES property(id),
  mowed BOOLEAN NOT NULL,
  edged BOOLEAN NOT NULL,
  trimmed BOOLEAN NOT NULL,
  blowed BOOLEAN NOT NULL,
  fertilized BOOLEAN NOT NULL,
  pesticided BOOLEAN NOT NULL,
  weeded BOOLEAN NOT NULL,
  watered BOOLEAN NOT NULL,
  repaired BOOLEAN NOT NULL,
  note VARCHAR,
  occured VARCHAR(10) NOT NULL
);

CREATE TABLE fault (
  cause VARCHAR NOT NULL,
  occurred VARCHAR NOT NULL
);