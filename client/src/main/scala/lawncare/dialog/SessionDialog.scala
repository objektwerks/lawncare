package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.control.{ButtonType, CheckBox, Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Session}

final class SessionDialog(context: Context,
                          session: Session) extends Dialog[Session]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogSession

  val mowedCheckBox = new CheckBox:
    selected = session.mowed

  val edgedCheckBox = new CheckBox:
    selected = session.edged

  val trimmedCheckBox = new CheckBox:
    selected = session.trimmed

  val blowedCheckBox = new CheckBox:
    selected = session.blowed

  val fertilizedCheckBox = new CheckBox:
    selected = session.fertilized

  val pesticidedCheckBox = new CheckBox:
    selected = session.pesticided

  val weededCheckBox = new CheckBox:
    selected = session.weeded

  val wateredCheckBox = new CheckBox:
    selected = session.watered

  val repairedCheckBox = new CheckBox:
    selected = session.repaired

  val noteTextField = new TextField:
    text = session.note

  val occurredTextField = new TextField:
    text = session.occurred

  val controls = List[(String, Region)](
    context.labelMowed -> mowedCheckBox,
    context.labelNote -> noteTextField
  )
  dialogPane().content = ControlGridPane(controls)

  val saveButtonType = new ButtonType(context.buttonSave, ButtonData.OKDone)
  dialogPane().buttonTypes = List(saveButtonType, ButtonType.Cancel)

  resultConverter = dialogButton =>
    if dialogButton == saveButtonType then
      session.copy(
        mowed = mowedCheckBox.selected.value,
        edged = edgedCheckBox.selected.value,
        trimmed = trimmedCheckBox.selected.value,
        blowed = blowedCheckBox.selected.value,
        fertilized = fertilizedCheckBox.selected.value,
        pesticided = pesticidedCheckBox.selected.value,
        weeded = weededCheckBox.selected.value,
        watered = wateredCheckBox.selected.value,
        repaired = repairedCheckBox.selected.value,
        note = noteTextField.text.value,
        occurred = occurredTextField.text.value
      )
    else null