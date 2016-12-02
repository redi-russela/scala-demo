package com.redi.scalademo.application

import com.redi.scalademo.infrastructure.{Client, SharedModule}
import com.redi.scalademo.presentation.FormListener
import com.softwaremill.macwire.wire

object ClientModule extends SharedModule {
  lazy val client = wire[Client]
  lazy val formListener = wire[FormListener]
}
