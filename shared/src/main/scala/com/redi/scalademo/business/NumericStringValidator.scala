package com.redi.scalademo.business

import scala.util.Try

class NumericStringValidator {

  def isValidNumber(string: String): Boolean = {
    Try(BigDecimal(string)).isSuccess
  }

}
