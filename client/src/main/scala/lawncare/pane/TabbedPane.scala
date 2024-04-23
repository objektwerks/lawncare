package lawncare.pane

import scalafx.geometry.Insets
import scalafx.scene.control.{Tab, TabPane}
import scalafx.scene.layout.{Priority, VBox}

import pool.{Context, Model}

final class TabbedPane(context: Context, model: Model) extends VBox:
  padding = Insets(6)