package lawncare

import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.HikariDataSource

import javax.sql.DataSource

import scalikejdbc.*
import scala.concurrent.duration.FiniteDuration

object Store:
  def cache(minSize: Int,
            maxSize: Int,
            expireAfter: FiniteDuration): Cache[String, String] =
    Scaffeine()
      .initialCapacity(minSize)
      .maximumSize(maxSize)
      .expireAfterWrite(expireAfter)
      .build[String, String]()

final class Store(config: Config,
                  cache: Cache[String, String]) extends LazyLogging:
  private val dataSource: DataSource = {
    val ds = new HikariDataSource()
    ds.setDataSourceClassName(config.getString("db.driverClassName"))
    ds.addDataSourceProperty("url", config.getString("db.url"))
    ds.addDataSourceProperty("user", config.getString("db.user"))
    ds.addDataSourceProperty("password", config.getString("db.password"))
    ds
  }
  ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))

  def register(account: Account): Account = addAccount(account)

  def login(email: String, pin: String): Option[Account] =
    DB readOnly { implicit session =>
      sql"select * from account where email_address = $email and pin = $pin"
        .map(rs =>
          Account(
            rs.long("id"),
            rs.string("license"),
            rs.string("email"),
            rs.string("pin"),
            rs.string("activated")
          )
        )
        .single()
    }

  def isAuthorized(license: String): Boolean =
    cache.getIfPresent(license) match
      case Some(_) =>
        logger.debug(s"*** store cache get: $license")
        true
      case None =>
        val optionalLicense = DB readOnly { implicit session =>
          sql"select license from account where license = $license"
            .map(rs => rs.string("license"))
            .single()
        }
        if optionalLicense.isDefined then
          cache.put(license, license)
          logger.debug(s"*** store cache put: $license")
          true
        else false

  def addAccount(account: Account): Account =
    val id = DB localTx { implicit session =>
      sql"""
        insert into account(license, email, pin, activated)
        values(${account.license}, ${account.email}, ${account.pin}, ${account.activated})
      """
      .updateAndReturnGeneratedKey()
    }
    account.copy(id = id)

  def listProperties(accountId: Long): List[Property] = DB readOnly { implicit session =>
    sql"select * from property where account_id = $accountId order by added"
      .map(rs =>
        Property(
          rs.long("id"),
          rs.long("account_id"),
          rs.string("location"), 
          rs.string("added")
        )
      )
      .list()
  }

  def addProperty(property: Property): Long = DB localTx { implicit session =>
    sql"""
      insert into property(account_id, location) values(${property.accountId}, ${property.location}, ${property.added})
      """
      .updateAndReturnGeneratedKey()
  }

  def updateProperty(property: Property): Long = DB localTx { implicit session =>
    sql"""
      update property set location = ${property.location}
      where id = ${property.id}
      """
      .update()
    property.id
  }

  def listSessions(propertyId: Long): List[Session] = DB readOnly { implicit session =>
    sql"select * from session where property_id = $propertyId order by occurred desc"
      .map(rs =>
        Session(
          rs.long("id"),
          rs.long("property_id"),
          rs.boolean("mowed"),
          rs.boolean("edged"),
          rs.boolean("blowed"),
          rs.boolean("trimmed"),
          rs.boolean("fertilized"),
          rs.boolean("pesticided"),
          rs.boolean("weeded"),
          rs.boolean("watered"),
          rs.boolean("repaired"),
          rs.string("note"),
          rs.string("occurred")
        )
      )
      .list()
  }

  /* 
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
    occured: String = Entity.now()
   */

  def addSession(sess: Session): Long = DB localTx { implicit session =>
    sql"""
      insert into session(property_id, mowed, edged, trimmed, blowed, fertilized, pesticided, weeded, watered, repaired, note, occurred)
      values(${sess.propertyId}, ${sess.mowed}, ${sess.edged}, ${sess.trimmed}, ${sess.blowed}, ${sess.fertilized}, ${sess.pesticided},
      ${sess.weeded}, ${sess.watered}, ${sess.repaired}, ${sess.note}, ${sess.occured})
      """
      .updateAndReturnGeneratedKey()
  }

  def updateSession(sess: Session): Long = DB localTx { implicit session =>
    sql"""
      update session set mowed = ${sess.mowed}, edged = ${sess.edged}, blowed = ${sess.blowed}, trimmed = ${sess.trimmed},
      fertilized = ${sess.fertilized}, pesticided = ${sess.pesticided}, weeded = ${sess.weeded}, watered = ${sess.watered},
      repaired = ${sess.repaired}, note = ${sess.note}, occurred = ${sess.occured} where id = ${sess.id}
      """
      .update()
    sess.id
  }

  def listFaults(): List[Fault] = DB readOnly { implicit session =>
    sql"select * from fault order by occurred desc"
      .map(rs =>
        Fault(
          rs.string("cause"),
          rs.string("occurred")
        )
      )
      .list()
  }

  def addFault(fault: Fault): Fault = DB localTx { implicit session =>
    sql"""
      insert into fault(cause, occurred) values(${fault.cause}, ${fault.occurred})
      """
      .update()
      fault
  }