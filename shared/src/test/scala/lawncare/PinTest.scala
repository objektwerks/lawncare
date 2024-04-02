package lawncare

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class PinTest extends AnyFunSuite with Matchers:
  test("pin random"):
    for i <- 1 to 1_000_000
    do Pin.newInstance.length shouldBe 7

  test("pin uuid"):
    for i <- 1 to 1_000_000
    do Pin.newPin.length shouldBe 7