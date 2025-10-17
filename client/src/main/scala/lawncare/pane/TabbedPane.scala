package lawncare.pane

import scalafx.scene.layout.{Priority, VBox}

import lawncare.{Context, Model}

final class TabbedPane(context: Context,
                       model: Model) extends VBox:
  val sessionsPane = SessionsPane(context, model)
  val issuesPane = IssuesPane(context, model)

  children = List(sessionsPane, issuesPane)
  VBox.setVgrow(this, Priority.Always)