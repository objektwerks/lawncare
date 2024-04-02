package lawncare

import java.util.UUID

import scala.util.Random

object Pin:
  private val specialChars = "~!@#$%^&*-+=<>?/:;".toList
  private val random = Random

  private def newSpecialChar: Char = specialChars( random.nextInt(specialChars.length) )

  def newInstance: String =
    Random.shuffle(
      Random
        .alphanumeric
        .take(5)
        .mkString
        .prepended(newSpecialChar)
        .appended(newSpecialChar)
    ).mkString

  def newPin: String = UUID.randomUUID().toString().substring(0, 7).toLowerCase()