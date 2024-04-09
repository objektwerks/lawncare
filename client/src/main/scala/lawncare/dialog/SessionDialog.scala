package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.control.{ButtonType, CheckBox, Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Session}

/* 
  mowed: Boolean = true,
  edged: Boolean = true,
  trimmed: Boolean = false,
  blowed: Boolean = true,
  fertilized: Boolean = false,
  pesticided: Boolean = false,
  weeded: Boolean = false,
  watered: Boolean = false,
  repaired: Boolean = false,
  note: String = "",
  occurred: String
 */

final class SessionDialog(context: Context, session: Session) extends Dialog[Session]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogSession

  val mowedCheckBox = new CheckBox:
    selected = session.mowed

  val edgedCheckBox = new CheckBox:
    selected = session.edged

  val noteTextField = new TextField:
    text = session.note

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
        note = noteTextField.text.toString
      )
    else null