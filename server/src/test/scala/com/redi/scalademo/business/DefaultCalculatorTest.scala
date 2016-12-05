package com.redi.scalademo.business

import org.scalatest.{Matchers, path}

class DefaultCalculatorTest extends path.FunSpec with Matchers {
  override def newInstance: path.FunSpecLike = super.newInstance  // Suppresses false IntelliJ IDEA error.

  describe("DefaultCalculator") {

    val target: DefaultCalculator = createTarget

    describe("#add") {

      describe("when augend is not a valid number") {

        val augend = "NaN"
        val addend = "2"

        val result = target.add(augend, addend)

        it("has one ValidationFailure") {
          result.swap.forall(_.size == 1) should be (true)
        }

        it("returns a ValidationFailure on augend") {
          failureExists(result, "augend") should be (true)
        }

      }

      describe("when addend is not a valid number") {

        val augend = "1"
        val addend = "NaN"

        val result = target.add(augend, addend)

        it("has one ValidationFailure") {
          result.swap.forall(_.size == 1) should be (true)
        }

        it("returns a ValidationFailure on addend") {
          failureExists(result, "addend") should be (true)
        }

      }

      describe("when neither augend nor addend is a valid number") {

        val augend = "NaN 1"
        val addend = "NaN 2"

        val result = target.add(augend, addend)

        it("has two ValidationFailure") {
          result.swap.forall(_.size == 2) should be (true)
        }

        it("returns a ValidationFailure on augend") {
          failureExists(result, "augend") should be (true)
        }

        it("returns a ValidationFailure on addend") {
          failureExists(result, "addend") should be (true)
        }

      }

      describe("given valid numbers") {

        val augend = "1"
        val addend = "2"

        val result = target.add(augend, addend)

        it("has no ValidationFailures") {
          result.swap.forall(_.isEmpty) should be (true)
        }

        it("returns the sum") {
          result.contains(BigDecimal(3)) should be (true)
        }

      }

    }

  }

  def createTarget: DefaultCalculator = new DefaultCalculator(new NumericStringValidator)

  def failureExists(validatedResult: ValidatedResult[_], formControlName: String): Boolean = {
    validatedResult
      .swap
      .exists { (validationFailures: Iterable[ValidationFailure]) =>
        validationFailures.exists { (validationFailure: ValidationFailure) =>
          validationFailure.formControlName == formControlName
        }
      }
  }

}
