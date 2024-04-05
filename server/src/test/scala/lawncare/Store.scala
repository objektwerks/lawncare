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