package com.redi.scalademo.business

trait Calculator {
  def add(augend: String, addend: String): ValidatedResult[BigDecimal]
}
