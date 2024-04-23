package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.control.{Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Issue}

final class IssueDialog(context: Context, issue: Issue) extends Dialog[Issue]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogSession