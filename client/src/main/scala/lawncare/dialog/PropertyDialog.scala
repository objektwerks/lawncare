package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.layout.Region
import scalafx.scene.control.{ButtonType, Dialog, TextField}
import scalafx.scene.control.ButtonBar.ButtonData

import lawncare.{Client, Context, Property}

final class PropertyDialog(context: Context,
                           property: Property) extends Dialog[Property]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogProperty

  val locationTextField = new TextField:
    text = property.location

  val controls = List[(String, Region)](
    context.labelLocation -> locationTextField
  )
  dialogPane().content = ControlGridPane(controls)

  val saveButtonType = new ButtonType(context.buttonSave, ButtonData.OKDone)
  dialogPane().buttonTypes = List(saveButtonType, ButtonType.Cancel)

  resultConverter = dialogButton =>
    if dialogButton == saveButtonType then
      property.copy(
        location = locationTextField.text.value
      )
    else null