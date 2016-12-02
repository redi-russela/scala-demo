package com.redi.scalademo.presentation

import com.redi.scalademo.business.{Calculator, DefaultCalculator}
import com.redi.scalademo.infrastructure.{Server, SharedModule}
import com.softwaremill.macwire.wire

object ServerModule extends SharedModule {
  lazy val calculator: Calculator = wire[DefaultCalculator]
  lazy val server = wire[Server]
}
