package com.redi.scalademo.infrastructure

import com.redi.scalademo.business.NumericStringValidator
import com.softwaremill.macwire.wire

trait SharedModule {
  lazy val numericStringValidator = wire[NumericStringValidator]
}
