package lawncare

import com.typesafe.config.Config

import scala.jdk.CollectionConverters.*
import scalafx.scene.image.{Image, ImageView}

final class Context(config: Config):
  val windowTitle = config.getString("window.title")
  val windowWidth = config.getDouble("window.width")
  val windowHeight = config.getDouble("window.height")

  val url = config.getString("url")
  val endpoint = config.getString("endpoint")

  val buttonAdd = config.getString("button.add")
  val buttonEdit = config.getString("button.edit")
  val buttonSave = config.getString("button.save")
  val buttonFaults = config.getString("button.faults")
  val buttonRegister = config.getString("button.register")
  val buttonLogin = config.getString("button.login")
  val buttonAccount = config.getString("button.account")

  val dialogRegisterLogin = config.getString("dialog.registerLogin")
  val dialogAccount = config.getString("dialog.account")
  val dialogProperty = config.getString("dialog.property")
  val dialogSession = config.getString("dialog.session")
  val dialogFaults = config.getString("dialog.faults")

  val errorServer = config.getString("error.server")
  val errorRegister = config.getString("error.register")
  val errorLogin = config.getString("error.login")

  val headerFault = config.getString("header.fault")

  val labelLicense = config.getString("label.license")
  val labelEmail = config.getString("label.email")
  val labelPin = config.getString("label.pin")
  val labelProperties = config.getString("label.properties")
  val labelSessions = config.getString("label.sessions")

  val tabWalkers = config.getString("tab.walkers")
  val tabSessions = config.getString("tab.sessions")
  val tabCalories = config.getString("tab.calories")
  val tabWeight = config.getString("tab.weight")
  val tabDistance = config.getString("tab.distance")

  val dateTimeSelectorEllipsis = config.getString("dateTimeSelector.ellipsis")
  val dateTimeSelectorYear = config.getString("dateTimeSelector.year")
  val dateTimeSelectorMonth = config.getString("dateTimeSelector.month")
  val dateTimeSelectorDay = config.getString("dateTimeSelector.day")
  val dateTimeSelectorHour = config.getString("dateTimeSelector.hour")
  val dateTimeSelectorMinute = config.getString("dateTimeSelector.minute")
  val dateTimeSelectorSecond = config.getString("dateTimeSelector.second")
  val dateTimeSelectorClose = config.getString("dateTimeSelector.close")

  def logoImage = loadImageView("/image/logo.png")
  def addImage = loadImageView("/image/add.png")
  def editImage = loadImageView("/image/edit.png")
  def chartImage = loadImageView("/image/chart.png")
  def faultsImage = loadImageView("/image/faults.png")
  def accountImage = loadImageView("/image/account.png")

  def logo = new Image(Image.getClass.getResourceAsStream("/image/logo.png"))

  private def loadImageView(path: String): ImageView = new ImageView:
    image = new Image(Image.getClass.getResourceAsStream(path))
    fitHeight = 25
    fitWidth = 25
    preserveRatio = true
    smooth = true