package lawncare.pane

import scalafx.Includes.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, SelectionMode, Tab, TabPane, TableColumn, TableView}
import scalafx.scene.layout.{HBox, Priority, VBox}

import lawncare.{Context, Model, Issue}
import lawncare.dialog.IssueDialog

final class IssuesPane(context: Context, model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)

  val tableView = new TableView[Issue]():
    columns ++= List(
      new TableColumn[Issue, String]:
        text = context.headerReport
        cellValueFactory = _.value.reportProperty
      ,
      new TableColumn[Issue, String]:
        text = context.headerResolution
        cellValueFactory = _.value.resolutionProperty
      ,
      new TableColumn[Issue, String]:
        text = context.headerReported
        cellValueFactory = _.value.reportedProperty
      ,
      new TableColumn[Issue, String]:
        text = context.headerResolved
        cellValueFactory = _.value.resolvedProperty
    )
    items = model.observableIssues

  val addButton = new Button:
    graphic = context.addImage
    text = context.buttonAdd
    disable = true
    onAction = { _ => add() }