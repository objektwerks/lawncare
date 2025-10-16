package lawncare

import com.typesafe.config.Config

import scalafx.scene.image.{Image, ImageView}

final class Context(config: Config):
  val windowTitle = config.getString("window.title")
  val windowWidth = config.getDouble("window.width")
  val windowHeight = config.getDouble("window.height")

  val url = config.getString("url")
  val endpoint = config.getString("endpoint")

  val aboutAlertHeaderText = config.getString("about.alert.headerText")
  val aboutAlertContentText = config.getString("about.alert.contentText")

  val menuMenu = config.getString("menu.menu")
  val menuAbout = config.getString("menu.about")
  val menuExit = config.getString("menu.exit")

  val buttonAdd = config.getString("button.add")
  val buttonEdit = config.getString("button.edit")
  val buttonSave = config.getString("button.save")
  val buttonFaults = config.getString("button.faults")
  val buttonRegister = config.getString("button.register")
  val buttonLogin = config.getString("button.login")
  val buttonAccount = config.getString("button.account")

  val columnYes = config.getString("column.yes")
  val columnNo = config.getString("column.no")

  val dialogRegisterLogin = config.getString("dialog.registerLogin")
  val dialogAccount = config.getString("dialog.account")
  val dialogProperty = config.getString("dialog.property")
  val dialogSession = config.getString("dialog.session")
  val dialogFaults = config.getString("dialog.faults")

  val errorServer = config.getString("error.server")
  val errorRegister = config.getString("error.register")
  val errorLogin = config.getString("error.login")

  val headerFault = config.getString("header.fault")
  val headerLocation = config.getString("header.location")
  val headerOccurred = config.getString("header.occurred")
  val headerNote = config.getString("header.note")
  val headerMowed = config.getString("header.mowed")
  val headerEdged = config.getString("header.edged")
  val headerTrimmed = config.getString("header.trimmed")
  val headerBlowed = config.getString("header.blowed")
  val headerFertilized = config.getString("header.fertilized")
  val headerPesticided = config.getString("header.pesticided")
  val headerWeeded = config.getString("header.weeded")
  val headerWaterd = config.getString("header.watered")
  val headerRepaired = config.getString("header.repaired")
  val headerReport = config.getString("header.report")
  val headerResolution = config.getString("header.resolution")
  val headerReported = config.getString("header.reported")
  val headerResolved = config.getString("header.resolved")

  val labelLicense = config.getString("label.license")
  val labelEmail = config.getString("label.email")
  val labelPin = config.getString("label.pin")
  val labelLocation = config.getString("label.location")
  val labelMowed = config.getString("label.mowed")
  val labelEdged = config.getString("label.edged")
  val labelTimmed = config.getString("label.trimmed")
  val labelBlowed = config.getString("label.blowed")
  val labelFertilized = config.getString("label.fertilized")
  val labelPesticided = config.getString("label.pesticided")
  val labelWeeded = config.getString("label.weeded")
  val labelWatered = config.getString("label.watered")
  val labelRepaired = config.getString("label.repaired")
  val labelNote = config.getString("label.note")
  val labelProperties = config.getString("label.properties")
  val labelSessions = config.getString("label.sessions")
  val labelReport = config.getString("label.report")
  val labelResolution = config.getString("label.resolution")
  val labelReported = config.getString("label.reported")
  val labelResolved = config.getString("label.resolved")

  val tabProperties = config.getString("tab.properties")
  val tabSessions = config.getString("tab.sessions")
  val tabIssues = config.getString("tab.issues")

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