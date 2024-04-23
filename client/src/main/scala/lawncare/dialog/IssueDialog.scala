package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.control.{DatePicker, Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Entity, Issue}

final class IssueDialog(context: Context, issue: Issue) extends Dialog[Issue]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogSession

  val reportTextField = new TextField:
    text = issue.report

  val resolutionTextField = new TextField:
    text = issue.resolution

  val reportedDatePicker = new DatePicker:
      value = Entity.localDate(issue.reported)

  val resolvedDatePicker = new DatePicker:
      value = Entity.localDate(issue.resolved)