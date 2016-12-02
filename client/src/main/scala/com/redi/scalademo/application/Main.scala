package com.redi.scalademo.application

import com.redi.scalademo.presentation.FormListener

import scala.scalajs.js.JSApp

object Main extends JSApp {

  override def main(): Unit = {
    val formListener: FormListener = ClientModule.formListener
    formListener.attachTo("calculator-form")
  }

}
