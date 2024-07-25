package lawncare

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import io.helidon.webserver.WebServer
import io.helidon.webserver.http.HttpRouting

import scala.concurrent.duration.DurationInt

object Server extends LazyLogging:
  @main def main(): Unit =
    val config = ConfigFactory.load("server.conf")
    val host = config.getString("server.host")
    val port = config.getInt("server.port")
    val endpoint = config.getString("server.endpoint")

    val cache = Store.cache(config)
    val dataSource = Store.dataSource(config)
    val store = Store(cache, dataSource)
    val emailer = Emailer(config)
    val dispatcher = Dispatcher(store, emailer)

    val handler = Handler(dispatcher)

    val builder = HttpRouting
      .builder
      .post(endpoint, handler)

    WebServer
      .builder
      .port(port)
      .routing(builder)
      .build
      .start

    println(s"*** Press Control-C to shutdown Lawncare Http Server at: $host:$port$endpoint")
    logger.info(s"*** Lawncare Http Server started at: $host:$port$endpoint")

    Thread.currentThread().join()