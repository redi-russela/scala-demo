package com.redi.scalademo.business

import scala.collection.mutable.ArrayBuffer

class DefaultCalculator(
  validator: NumericStringValidator
) extends Calculator {

  override def add(augend: String, addend: String): ValidatedResult[BigDecimal] = {
    val failures = ArrayBuffer.empty[ValidationFailure]
    if (!validator.isValidNumber(augend)) {
      failures += ValidationFailure(FormControlNames.Augend, "augend must be a number")
    }
    if (!validator.isValidNumber(addend)) {
      failures += ValidationFailure(FormControlNames.Addend, "addend must be a number")
    }
    if (failures.nonEmpty) {
      Left(failures)
    } else {
      Right(BigDecimal(augend) + BigDecimal(addend))
    }
  }

}
