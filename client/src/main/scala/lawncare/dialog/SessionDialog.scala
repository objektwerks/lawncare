package lawncare.dialog

import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ButtonType, Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Entity, Session}

final class SessionDialog(context: Context, session: Session) extends Dialog[Session]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogSession

  val noteTextField = new TextField:
    text = session.note

  val controls = List[(String, Region)](
    context.labelNote   -> noteTextField
  )
  dialogPane().content = ControlGridPane(controls)

  val saveButtonType = new ButtonType(context.buttonSave, ButtonData.OKDone)
  dialogPane().buttonTypes = List(saveButtonType, ButtonType.Cancel)

  resultConverter = dialogButton =>
    if dialogButton == saveButtonType then
      session.copy(
        note = noteTextField.text.toString
      )
    else null