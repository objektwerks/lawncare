package lawncare

import scalafx.geometry.{Insets, Orientation}
import scalafx.scene.Scene
import scalafx.scene.control.SplitPane
import scalafx.scene.layout.{Priority, VBox}

import lawncare.pane.{PropertiesPane, TabbedPane}

final class View(context: Context, model: Model):
  val menu = Menu(context)

  val vbox = new VBox:
    prefWidth = context.windowWidth
    prefHeight = context.windowHeight
    padding = Insets(6)

  val propertiesPane = PropertiesPane(context, model)
  VBox.setVgrow(propertiesPane, Priority.Always)

  val tabbedPane = TabbedPane(context, model)
  VBox.setVgrow(tabbedPane, Priority.Always)

  val splitPane = new SplitPane {
    orientation = Orientation.Horizontal
    items.addAll(propertiesPane, tabbedPane)
  }
  splitPane.setDividerPositions(0.28, 0.72)
  VBox.setVgrow(splitPane, Priority.Always)

  vbox.children = List(menu, splitPane)

  val scene = new Scene:
    root = vbox
    stylesheets = List("/style.css")