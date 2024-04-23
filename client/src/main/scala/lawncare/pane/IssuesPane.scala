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