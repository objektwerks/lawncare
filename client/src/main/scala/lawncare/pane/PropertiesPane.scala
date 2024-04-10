package lawncare.pane

import scalafx.geometry.Insets
import scalafx.scene.layout.VBox

import lawncare.{Context, Model}

final class PropertiesPane(context: Context, model: Model) extends VBox:
  spacing = 6
  padding = Insets(6)