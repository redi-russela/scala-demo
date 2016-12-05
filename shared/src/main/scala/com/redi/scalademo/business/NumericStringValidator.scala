package com.redi.scalademo.business

import scala.util.Try

class NumericStringValidator {

  def isValidNumber(string: String): Boolean = {
    println(s"Validating if '$string' is a valid number")
    Try(BigDecimal(string)).isSuccess
  }

}
