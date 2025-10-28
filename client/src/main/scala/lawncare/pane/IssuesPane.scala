package lawncare.pane

import scalafx.Includes.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, SelectionMode, Tab, TabPane, TableColumn, TableView}
import scalafx.scene.layout.{HBox, Priority, VBox}

import lawncare.{Context, Model, Issue}
import lawncare.dialog.IssueDialog

final class IssuesPane(context: Context,
                       model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)

  val tableView = new TableView[Issue]():
    columns ++= List(
      new TableColumn[Issue, String]:
        prefWidth = 400
        text = context.headerReport
        cellValueFactory = _.value.reportProperty
      ,
      new TableColumn[Issue, String]:
        prefWidth = 400
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

  val editButton = new Button:
    graphic = context.editImage
    text = context.buttonEdit
    disable = true
    onAction = { _ => update() }

  val buttonBar = new HBox:
    spacing = 6
    children = List(addButton, editButton)

  val tab = new Tab:
  	text = context.tabIssues
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

  model.selectedPropertyId.onChange { (_, _, _) =>
    addButton.disable = false
  }

  tableView.onMouseClicked = { event =>
    if (event.getClickCount == 2 && tableView.selectionModel().getSelectedItem != null) update()
  }

  tableView.selectionModel().selectionModeProperty().value = SelectionMode.Single

  tableView.selectionModel().selectedItemProperty().addListener { (_, _, selectedItem) =>
    // model.update executes a remove and add on items. the remove passes a null selectedItem!
    if selectedItem != null then
      model.selectedIssueId.value = selectedItem.id
      editButton.disable = false
    else editButton.disable = true
  }

  def add(): Unit =
    IssueDialog(context, Issue(propertyId = model.selectedPropertyId.value)).showAndWait() match
      case Some(issue: Issue) => model.add(issue) {
        tableView.selectionModel().select(0)
      }
      case _ =>

  def update(): Unit =
    if tableView.selectionModel().getSelectedItem != null then
      val selectedIndex = tableView.selectionModel().getSelectedIndex
      val issue = tableView.selectionModel().getSelectedItem.issue
      IssueDialog(context, issue).showAndWait() match
        case Some(issue: Issue) => model.update(selectedIndex, issue) {
          tableView.selectionModel().select(selectedIndex)
        }
        case _ =>