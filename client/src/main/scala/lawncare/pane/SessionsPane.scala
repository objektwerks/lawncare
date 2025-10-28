package lawncare.pane

import scalafx.Includes.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, SelectionMode, Tab, TabPane, TableColumn, TableView}
import scalafx.scene.layout.{HBox, Priority, VBox}

import lawncare.{Context, Model, Session}
import lawncare.dialog.SessionDialog

final class SessionsPane(context: Context,
                         model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)

  val yesOrNo = (bool: Boolean) => if bool then context.columnYes else context.columnNo

  val tableView = new TableView[Session]():
    columns ++= List(
      new TableColumn[Session, Boolean]:
        text = context.headerMowed
        cellValueFactory = _.value.mowedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerEdged
        cellValueFactory = _.value.edgedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerTrimmed
        cellValueFactory = _.value.trimmedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerBlowed
        cellValueFactory = _.value.blowedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerFertilized
        cellValueFactory = _.value.fertilizedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerPesticided
        cellValueFactory = _.value.pesticidedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerWeeded
        cellValueFactory = _.value.weededProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerWaterd
        cellValueFactory = _.value.wateredProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, Boolean]:
        text = context.headerRepaired
        cellValueFactory = _.value.repairedProperty
        cellFactory = (cell, bool) => cell.text = yesOrNo(bool)
      ,
      new TableColumn[Session, String]:
        prefWidth = 200
        text = context.headerNote
        cellValueFactory = _.value.noteProperty
      ,
      new TableColumn[Session, String]:
        text = context.headerOccurred
        cellValueFactory = _.value.occurredProperty
    )
    items = model.observableSessions

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
  	text = context.tabSessions
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
      model.selectedSessionId.value = selectedItem.id
      editButton.disable = false
    else editButton.disable = true
  }

  def add(): Unit =
    SessionDialog(context, Session(propertyId = model.selectedPropertyId.value)).showAndWait() match
      case Some(session: Session) => model.add(session) {
        tableView.selectionModel().select(0)
      }
      case _ =>

  def update(): Unit =
    if tableView.selectionModel().getSelectedItem != null then
      val selectedIndex = tableView.selectionModel().getSelectedIndex
      val session = tableView.selectionModel().getSelectedItem.session
      SessionDialog(context, session).showAndWait() match
        case Some(session: Session) => model.update(selectedIndex, session) {
          tableView.selectionModel().select(selectedIndex)
        }
        case _ =>