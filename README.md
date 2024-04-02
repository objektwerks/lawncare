Lawncare
--------
Lawn care app using Scala 3.

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
>To build for a "mac", "m1', "win" or "linux" os target:
1. sbt -Dtarget="mac" clean test assembly copyAssemblyJar
2. sbt -Dtarget="m1" clean test assembly copyAssemblyJar
3. sbt -Dtarget="win" clean test assembly copyAssemblyJar
4. sbt -Dtarget="linux" clean test assembly copyAssemblyJar

Execute Client
--------------
>To execute an assembled jar locally:
1. java -jar .assembly/lawncare-mac-0.11.jar
2. java -jar .assembly/lawncare-m1-0.11.jar
3. java -jar .assembly/lawncare-win-0.11.jar
4. java -jar .assembly/lawncare-linux-0.11.jar

Deploy
------
>Consider these options:
1. [jDeploy](https://www.npmjs.com/package/jdeploy)
2. [Conveyor](https://hydraulic.software/index.html)

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

export LAWNCARE_POSTGRESQL_URL="jdbc:postgresql://localhost:5432/lawncare"
export LAWNCARE_POSTGRESQL_USER="yourusername"
export LAWNCARE_POSTGRESQL_PASSWORD="lawncare"
export LAWNCARE_POSTGRESQL_DRIVER="org.postgresql.Driver"
export LAWNCARE_POSTGRESQL_DB_NAME="lawncare"
export LAWNCARE_POSTGRESQL_HOST="127.0.0.1"
export LAWNCARE_POSTGRESQL_PORT=5432
export LAWNCARE_POSTGRESQL_POOL_INITIAL_SIZE=9
export LAWNCARE_POSTGRESQL_POOL_MAX_SIZE=32
export LAWNCARE_POSTGRESQL_POOL_CONNECTION_TIMEOUT_MILLIS=30000

export LAWNCARE_EMAIL_HOST="your-email.provider.com"
export LAWNCARE_EMAIL_ADDRESS="your-email@provider.com"
export LAWNCARE_EMAIL_PASSWORD="your-email-password"
```