package lawncare.dialog

import scalafx.Includes.*
import scalafx.scene.control.{ButtonType, Dialog, Label}

import lawncare.{Account, Client, Context}

final class AccountDialog(context: Context,
                          account: Account) extends Dialog[Unit]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogAccount

  val controls = List[(String, Label)](
    context.labelLicense -> Label( account.license ),
    context.labelEmail -> Label( account.email ),
    context.labelPin -> Label( account.pin )
  )

  dialogPane().content = ControlGridPane(controls)
  dialogPane().buttonTypes = List(ButtonType.Close)