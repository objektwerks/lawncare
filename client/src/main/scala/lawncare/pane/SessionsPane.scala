package lawncare.pane

import scalafx.geometry.Insets
import scalafx.scene.layout.VBox

import lawncare.{Context, Model}

final class SessionsPane(context: Context, model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)