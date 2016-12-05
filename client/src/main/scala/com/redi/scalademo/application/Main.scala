package com.redi.scalademo.application

import com.redi.scalademo.presentation.CalculatorFormListener
import org.scalajs.jquery.{jQuery => $}

import scala.scalajs.js.JSApp

object Main extends JSApp {

  override def main(): Unit = {
    val calculatorFormListener: CalculatorFormListener = ClientModule.calculationFormListener("calculator-form")
    calculatorFormListener.startListening()
  }

}
