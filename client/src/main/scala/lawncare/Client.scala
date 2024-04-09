package lawncare

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scalafx.application.JFXApp3

import lawncare.dialog.{Alerts}

object Client extends JFXApp3 with LazyLogging:
  val conf = ConfigFactory.load("client.conf")
  val context = Context(conf)
  val fetcher = Fetcher(context)
  val model = Model(fetcher)

  override def start(): Unit =
    val view = View(context, model)
    stage = new JFXApp3.PrimaryStage:
      scene = view.scene
      title = context.windowTitle
      minWidth = context.windowWidth
      minHeight = context.windowHeight
      icons.add(context.logo)

    stage.hide()

    model.registered.onChange { (_, _, _) =>
      Alerts.showRegisterAlert(context, stage)
      logger.error("*** register failed, client stopping ...")
      sys.exit(-1)
    }

    model.loggedin.onChange { (_, _, _) =>
      Alerts.showLoginAlert(context, stage)
      logger.error("*** login failed, client stopping ...")
      sys.exit(-1)
    }

  override def stopApp(): Unit = logger.info("*** client stopped.")