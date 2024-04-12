package lawncare.pane

import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Tab, TabPane, TableColumn, TableView}
import scalafx.scene.layout.{HBox, Priority, VBox}

import lawncare.{Context, Model, Property}

final class PropertiesPane(context: Context, model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)

  val tableView = new TableView[Property]():
    columns ++= List(
      new TableColumn[Property, String]:
        text = context.headerLocation
        cellValueFactory = _.value.locationProperty
      ,
    )
    items = model.observableProperties

  val addButton = new Button:
    graphic = context.addImage
    text = context.buttonAdd
    disable = false
    onAction = { _ => add() }

  val editButton = new Button:
    graphic = context.editImage
    text = context.buttonEdit
    disable = true
    onAction = { _ => update() }

  val faultsButton = new Button:
    graphic = context.faultsImage
    text = context.buttonFaults
    disable = true
    onAction = { _ => faults() }

  val accountButton = new Button:
    graphic = context.accountImage
    text = context.buttonAccount
    disable = false
    onAction = { _ => account() }

  val buttonBar = new HBox:
    spacing = 6
    children = List(addButton, editButton, faultsButton, accountButton)

  val tab = new Tab:
  	text = context.tabProperties
  	closable = false
  	content = new VBox {
      spacing = 6
      padding = Insets(6)
      children = List(tableView, buttonBar)
    }

  val tabPane = new TabPane:
    tabs = List(tab)

  children = List(tabPane)
  VBox.setVgrow(tableView, Priority.Always)
  VBox.setVgrow(tabPane, Priority.Always)