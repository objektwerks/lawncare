package lawncare

object Validator:
  extension (value: String)
    def isEmptyOrNotEmpty: Boolean = value.isEmpty || value.nonEmpty
    def isLicense: Boolean = if value.nonEmpty && value.length == 36 then true else false
    def isPin: Boolean = value.length == 7
    def isEmail: Boolean = value.nonEmpty && value.length >= 3 && value.contains("@")

  extension (account: Account)
    def isValid: Boolean =
      account.id >= 0 &&
      account.license.isLicense &&
      account.email.isEmail &&
      account.pin.isPin &&
      account.activated.nonEmpty

  extension (property: Property)
    def isValid: Boolean =
      property.id >= 0 &&
      property.accountId > 0 &&
      property.location.nonEmpty &&
      property.added.nonEmpty

  extension (session: Session)
    def isValid: Boolean =
      session.id >= 0 &&
      session.propertyId > 0 &&
      session.note.isEmptyOrNotEmpty &&
      session.occurred.nonEmpty

  extension (issue: Issue)
    def isValid: Boolean =
      issue.id >= 0 &&
      issue.propertyId > 0 &&
      issue.report.nonEmpty &&
      issue.resolution.isEmptyOrNotEmpty &&
      issue.reported.nonEmpty &&
      issue.resolved.isEmptyOrNotEmpty

  extension  (license: License)
    def isLicense: Boolean =
      license.license.isLicense

  extension (register: Register)
    def isValid: Boolean =
      register.email.isEmail

  extension (login: Login)
    def isValid: Boolean =
      login.email.isEmail &&
      login.pin.isPin

  extension (listPropertiess: ListProperties)
    def isValid: Boolean =
      listPropertiess.license.isLicense &&
      listPropertiess.accountId > 0

  extension (saveProperty: SaveProperty)
    def isValid: Boolean =
      saveProperty.license.isLicense &&
      saveProperty.property.isValid

  extension (listSessions: ListSessions)
    def isValid: Boolean =
      listSessions.license.isLicense &&
      listSessions.propertyId > 0

  extension (saveSession: SaveSession)
    def isValid: Boolean =
      saveSession.license.isLicense &&
      saveSession.session.isValid

  extension (addFault: AddFault)
    def isValid: Boolean =
      addFault.license.isLicense &&
      addFault.fault.cause.nonEmpty

  extension (command: Command)
    def isValid: Boolean =
      command match
        case register @ Register(_)                => register.isValid
        case login @ Login(_, _)                   => login.isValid
        case listProperties @ ListProperties(_, _) => listProperties.isValid
        case saveProperty @ SaveProperty(_, _)     => saveProperty.isValid
        case listSessions @ ListSessions(_, _)     => listSessions.isValid
        case saveSession @ SaveSession(_, _)       => saveSession.isValid
        case addFault @ AddFault(_, _)             => addFault.isValid