package lawncare.pane

import scalafx.geometry.Insets
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.VBox

import lawncare.{Context, Model, Session}

final class SessionsPane(context: Context, model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)

  val yesOrNo = (bool: Boolean) => if bool then context.columnYes else context.columnNo

  val tableView = new TableView[Session]():
    columns ++= List(
      new TableColumn[Session, String]:
        text = context.headerOccurred
        cellValueFactory = _.value.occurredProperty
      ,
      new TableColumn[Session, String]:
        text = context.headerNote
        cellValueFactory = _.value.noteProperty
      ,
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
    )
    items = model.observableCleanings