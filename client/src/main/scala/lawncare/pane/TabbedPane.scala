package lawncare.pane

import scalafx.scene.control.{Tab, TabPane}
import scalafx.scene.layout.{Priority, VBox}

import lawncare.{Context, Model}

final class TabbedPane(context: Context, model: Model) extends VBox:
  val sessionsTab = new Tab:
  	text = context.tabSessions
  	closable = false
  	content = SessionsPane(context, model)

  val issuesTab = new Tab:
  	text = context.tabIssues
  	closable = false
  	content = IssuesPane(context, model)

  val tabPane = new TabPane:
    tabs = List(sessionsTab, issuesTab)

  children = List(tabPane)
  VBox.setVgrow(tabPane, Priority.Always)