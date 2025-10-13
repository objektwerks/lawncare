Lawncare
--------
Lawn care app using ScalaFx, ScalikeJdbc, Jsoniter, JoddMail, Helidon, Ox, PostgreSql, HikariCP and Scala 3.

Model
-----
* Account 1 --> * Property
* Property 1 --> * Session
* Property 1 --> * Issue
* Fault

Session
-------
>Session properties:
* mowed ( yard )
* edged ( driveway, curbing )
* trimmed ( plant, tree )
* blowed ( blower, driveway, plant beds )
* fertilized ( nitrogen, phosphorus and potassium )
* pesticided ( herbicides, insecticides, nematicides, fungicides )
* weeded ( plant beds )e
* watered ( sprinkler system, sprinkler heads and pipes )
* repaired ( sprinkler system, pipes, heads, on/off | uncapped/capped )
* note ( anyting pertaining to a yard session )

Location
--------
>The Property.location property can contain the following:
1. [Address to Lat/Lon Converter](https://www.latlong.net/convert-address-to-lat-long.html)
2. [Google Maps](https://support.google.com/maps/answer/18539?hl=en&co=GENIE.Platform=Android)
3. Short Address
4. Long Address
5. Or any special encoded address. :)

Build
-----
1. sbt clean compile

Test
----
1. sbt clean test

Server Run
----------
1. sbt server/run

Client Run
----------
1. sbt client/run

Package Server
--------------
1. sbt server/universal:packageBin
2. see server/target/universal

Client Assembly
---------------
1. sbt clean test assembly copyAssemblyJar

Execute Client
--------------
1. java -jar .assembly/lawncare-$version.jar ( or double-click executable jar )

Postgresql
----------
1. config:
    1. on osx intel: /usr/local/var/postgres/postgresql.config : listen_addresses = ‘localhost’, port = 5432
    2. on osx m1: /opt/homebrew/var/postgres/postgresql.config : listen_addresses = ‘localhost’, port = 5432
2. run:
    1. brew services start postgresql@14
3. logs:
    1. on osx intel: /usr/local/var/log/postgres.log
    2. on m1: /opt/homebrew/var/log/postgres.log

Database
--------
>Example database url: postgresql://localhost:5432/lawncare?user=mycomputername&password=lawncare"
1. psql postgres
2. CREATE DATABASE lawncare OWNER [your computer name];
3. GRANT ALL PRIVILEGES ON DATABASE lawncare TO [your computer name];
4. \l
5. \q
6. psql lawncare
7. \i ddl.sql
8. \q

DDL
---
>Alternatively run: psql -d lawncare -f ddl.sql
1. psql lawncare
2. \i ddl.sql
3. \q

Drop
----
1. psql postgres
2. drop database lawncare;
3. \q

Environment
-----------
>The following environment variables must be defined:
```
export LAWNCARE_HOST="127.0.0.1"
export LAWNCARE_PORT=7070
export LAWNCARE_ENDPOINT="/command"

export LAWNCARE_CACHE_INITIAL_SIZE=4
export LAWNCARE_CACHE_MAX_SIZE=10
export LAWNCARE_CACHE_EXPIRE_AFTER=24

export LAWNCARE_POSTGRESQL_DRIVER="org.postgresql.ds.PGSimpleDataSource"
export LAWNCARE_POSTGRESQL_URL="jdbc:postgresql://localhost:5432/lawncare"
export LAWNCARE_POSTGRESQL_USER="yourusername"
export LAWNCARE_POSTGRESQL_PASSWORD="lawncare"

export LAWNCARE_EMAIL_HOST="your-email.provider.com"
export LAWNCARE_EMAIL_ADDRESS="your-email@provider.com"
export LAWNCARE_EMAIL_PASSWORD="your-email-password"
```

Resources
---------
* [JavaFX](https://openjfx.io/index.html)
* [JavaFX Tutorial](https://jenkov.com/tutorials/javafx/index.html)
* [ScalaFX](http://www.scalafx.org/)


License
-------
>Copyright (c) [2024 - 2025] [Objektwerks]

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    * http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
