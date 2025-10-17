package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.control.{ButtonType, DatePicker, Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Entity, Issue}

final class IssueDialog(context: Context,
                        issue: Issue) extends Dialog[Issue]:
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

  val controls = List[(String, Region)](
    context.labelReport -> reportTextField,
    context.labelResolution -> resolutionTextField,
    context.labelReported -> reportedDatePicker,
    context.labelResolved -> resolvedDatePicker
  )
  dialogPane().content = ControlGridPane(controls)

  val saveButtonType = new ButtonType(context.buttonSave, ButtonData.OKDone)
  dialogPane().buttonTypes = List(saveButtonType, ButtonType.Cancel)

  resultConverter = dialogButton =>
    if dialogButton == saveButtonType then
      issue.copy(
        report = reportTextField.text.value,
        resolution = resolutionTextField.text.value,
        reported = reportedDatePicker.value.value.toString,
        resolved = resolvedDatePicker.value.value.toString
      )
    else null