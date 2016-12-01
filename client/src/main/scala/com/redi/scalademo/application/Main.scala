package com.redi.scalademo.application

import scala.scalajs.js.JSApp

object Main extends JSApp {

  override def main(): Unit = {
    import ClientModule.formListener
    formListener.attachTo("calculator-form")
  }

}
