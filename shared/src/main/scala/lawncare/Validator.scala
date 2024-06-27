package lawncare

object Validator:
  extension (value: String)
    def isEmptyOrNonEmpty: Boolean = value.isEmpty || value.nonEmpty
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
      session.note.isEmptyOrNonEmpty &&
      session.occurred.nonEmpty

  extension (issue: Issue)
    def isValid: Boolean =
      issue.id >= 0 &&
      issue.propertyId > 0 &&
      issue.report.nonEmpty &&
      issue.resolution.isEmptyOrNonEmpty &&
      issue.reported.nonEmpty &&
      issue.resolved.nonEmpty

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

  extension (listIssues: ListIssues)
    def isValid: Boolean =
      listIssues.license.isLicense &&
      listIssues.propertyId > 0

  extension (saveIssue: SaveIssue)
    def isValid: Boolean =
      saveIssue.license.isLicense &&
      saveIssue.issue.isValid

  extension (addFault: AddFault)
    def isValid: Boolean =
      addFault.license.isLicense &&
      addFault.fault.cause.nonEmpty

  extension (command: Command)
    def isValid: Boolean =
      command match
        case register: Register             => register.isValid
        case login: Login                   => login.isValid
        case listProperties: ListProperties => listProperties.isValid
        case saveProperty: SaveProperty     => saveProperty.isValid
        case listSessions: ListSessions     => listSessions.isValid
        case saveSession: SaveSession       => saveSession.isValid
        case listIssues: ListIssues         => listIssues.isValid
        case saveIssue: SaveIssue           => saveIssue.isValid
        case addFault: AddFault             => addFault.isValid