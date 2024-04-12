package lawncare.pane

import scalafx.geometry.Insets
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout.VBox

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