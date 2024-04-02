package lawncare

import java.util.UUID

object Pin:
  def newPin: String = UUID.randomUUID().toString().substring(0, 7).toLowerCase()