package com.redi.scalademo.presentation

import com.redi.scalademo.business.{Calculator, DefaultCalculator}
import com.redi.scalademo.infrastructure.Server

private[presentation] object ServerModule {
  lazy val calculator: Calculator = new DefaultCalculator
  lazy val server = new Server
}
