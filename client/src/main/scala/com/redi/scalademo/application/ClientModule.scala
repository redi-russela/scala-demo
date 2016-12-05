package com.redi.scalademo.application

import com.redi.scalademo.infrastructure.{Client, SharedModule}
import com.redi.scalademo.presentation.CalculatorFormListener
import com.softwaremill.macwire.wire

object ClientModule extends SharedModule {
  lazy val client = wire[Client]
  lazy val calculationFormListener = (calculationFormId: String) => wire[CalculatorFormListener]
}
