package com.redi.scalademo.business

import org.scalatest.{Matchers, path}

class NumericStringValidatorTest extends path.FunSpec with Matchers {
  override def newInstance: path.FunSpecLike = new NumericStringValidatorTest

  describe("NumericStringValidator") {

    val target: NumericStringValidator = createTarget

    describe("#isValidNumber") {

      describe("given an integer") {

        val input: String = "7"

        it("returns true") {
          target.isValidNumber(input) should be(true)
        }

      }

      it("given a decimal, returns true") {
        target.isValidNumber("1.5") should be(true)
      }

      it("given an alphabetic character, returns false") {
        target.isValidNumber("a") should be(false)
      }

    }

  }

  def createTarget: NumericStringValidator = new NumericStringValidator

}
