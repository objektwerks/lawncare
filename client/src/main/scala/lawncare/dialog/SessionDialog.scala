package lawncare.dialog

import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ButtonType, Dialog}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.Region

import lawncare.{Client, Context, Session}

final class SessionDialog(context: Context, session: Session) extends Dialog[Session]:
  initOwner(Client.stage)
  title = context.windowTitle
  headerText = context.dialogSession
