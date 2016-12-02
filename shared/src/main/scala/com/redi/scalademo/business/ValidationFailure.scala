package com.redi.scalademo.business

case class ValidationFailure(
  formControlName: String,
  message: String
)
