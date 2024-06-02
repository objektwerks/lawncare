package lawncare.dialog

import java.awt.Toolkit

import scalafx.Includes.*
import scalafx.scene.layout.VBox
import scalafx.scene.control.{ButtonType, Dialog, TextField, TitledPane}
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.stage.Stage

import lawncare.{Context, Register, Login}
import lawncare.Validator.*

final case class RegisterLogin(register: Option[Register] = None,
                               login: Option[Login] = None)

final class RegisterLoginDialog(primaryStage: Stage,
                                context: Context) extends Dialog[RegisterLogin]:
  initOwner(primaryStage)
  title = context.windowTitle
  headerText = context.dialogRegisterLogin
  graphic = context.logoImage
  x = Toolkit.getDefaultToolkit.getScreenSize.width / 2.4
  y = Toolkit.getDefaultToolkit.getScreenSize.height / 6

  val registerEmailTextField = new TextField
  val registerControls = List[(String, TextField)](
    context.labelEmail -> registerEmailTextField
  )
  val registerTitledPane = new TitledPane:
    text = context.buttonRegister
    collapsible = false
    maxWidth = Double.MaxValue
    maxHeight = Double.MaxValue
    content = ControlGridPane(registerControls)

  val loginEmailTextField = new TextField
  val loginPinTextField = new TextField:
    prefColumnCount = 7
  val loginControls = List[(String, TextField)](
    context.labelEmail -> loginEmailTextField,
    context.labelPin -> loginPinTextField
  )
  val loginTitledPane = new TitledPane:
    text = context.buttonLogin
    collapsible = false
    maxWidth = Double.MaxValue
    maxHeight = Double.MaxValue
    content = ControlGridPane(loginControls)

  val registerLoginPane = new VBox:
    spacing = 6
    children = List(registerTitledPane, loginTitledPane)

  dialogPane().content = registerLoginPane

  val registerButtonType = new ButtonType(context.buttonRegister, ButtonData.Left)
  val loginButtonType = new ButtonType(context.buttonLogin, ButtonData.Right)
  dialogPane().buttonTypes = List(registerButtonType, loginButtonType)

  val registerButton = dialogPane().lookupButton(registerButtonType)
  registerButton.disable = true

  val loginButton = dialogPane().lookupButton(loginButtonType)
  loginButton.disable = true

  registerEmailTextField.text.onChange { (_, _, newValue) =>
    registerButton.disable = !newValue.isEmail
  }

  loginEmailTextField.text.onChange { (_, _, newValue) =>
    loginButton.disable = !newValue.isEmail || !loginPinTextField.text.value.isPin
  }

  loginPinTextField.text.onChange { (_, _, newValue) =>
    loginButton.disable = !newValue.isPin || !loginEmailTextField.text.value.isEmail
  }

  resultConverter = dialogButton => {
    if dialogButton == registerButtonType then
      RegisterLogin(register = Some( Register( registerEmailTextField.text.value ) ) )
    else if dialogButton == loginButtonType then
      RegisterLogin(login = Some( Login( loginEmailTextField.text.value, loginPinTextField.text.value ) ) )
    else null
  }