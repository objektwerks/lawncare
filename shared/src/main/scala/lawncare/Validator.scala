package lawncare

object Validator:
  extension (value: String)
    def isLicense: Boolean = if value.nonEmpty && value.length == 36 then true else false
    def isPin: Boolean = value.length == 7
    def isEmail: Boolean = value.nonEmpty && value.length >= 3 && value.contains("@")

  extension (register: Register)
    def isValid: Boolean = register.email.isEmail && register.location.nonEmpty

  extension (login: Login)
    def isValid: Boolean = login.email.isEmail && login.pin.isPin

  extension (listPropertiess: ListProperties)
    def isValid: Boolean = listPropertiess.license.isLicense

  extension (saveProperty: SaveProperty)
    def isValid: Boolean = saveProperty.license.isLicense && saveProperty.property.isValid

  extension (addFault: AddFault)
    def isValid: Boolean = addFault.license.isLicense && addFault.fault.cause.nonEmpty