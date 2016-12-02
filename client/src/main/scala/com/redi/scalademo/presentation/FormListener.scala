package com.redi.scalademo.presentation

import autowire.{clientCallable, unwrapClientProxy}
import com.redi.scalademo.business.{Calculator, NumericStringValidator, ValidatedResult, ValidationFailure}
import com.redi.scalademo.infrastructure.Client
import org.scalajs.dom
import org.scalajs.jquery.{JQuery, JQueryEventObject, jQuery ⇒ $}

import scala.concurrent.Future
import scala.math.BigDecimal
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

class FormListener(
  client: Client,
  numericStringValidator: NumericStringValidator
) {

  private val InfoClass = "is-info"
  private val DangerClass = "is-danger"

  private val Validate = "validate"
  private val Augend = "augend"
  private val Addend = "addend"
  private val Summand = "summand"

  private val Name = "name"
  private val Value = "value"

  private val namedElement = "[name]"

  private var formElement: JQuery = _
  private var summandElement: JQuery = _

  def attachTo(formId: String): Unit = {

    formElement = $(s"form#$formId")
    summandElement = formElement.find(elementNamed(Summand))

    formElement.submit { (e: JQueryEventObject) ⇒
      e.preventDefault()
      resetStyles()
      val allFormValuesAreValid = validateAndSetStyles()
      if (allFormValuesAreValid) {
        invokeAddition()
      }
    }

    formElement.find(namedElement).on("input", { (self: dom.Element, e: JQueryEventObject) ⇒
      $(self).removeClass(DangerClass)
      summandElement
        .value("")
        .removeClass(InfoClass)
    }: js.ThisFunction1[dom.Element, JQueryEventObject, js.Any])

  }

  private def elementNamed(name: String): String = {
    s"""[name="$name"]"""
  }

  private def resetStyles(): Unit = {
    formElement.find(namedElement).removeClass(DangerClass)
  }

  private def validateAndSetStyles(): Boolean = {
    var allFormValuesAreValid = true
    for (formControlData: js.Dictionary[String] ← calculableFormData) {
      val name: String = formControlData(Name)
      val value: String = formControlData(Value)
      if (shouldValidate) {
        val isValid: Boolean = numericStringValidator.isValidNumber(value)
        if (!isValid) {
          allFormValuesAreValid = false
          formElement.find(elementNamed(name)).addClass(DangerClass)
        }
      }
    }
    allFormValuesAreValid
  }

  private def serializedFormData: js.Array[js.Dictionary[String]] = {
    formElement.serializeArray().asInstanceOf[js.Array[js.Dictionary[String]]]
  }

  private def calculableFormData: js.Array[js.Dictionary[String]] = {
    var calculableFormData = js.Array[js.Dictionary[String]]()
    for (formControlData: js.Dictionary[String] ← serializedFormData) {
      if (formControlData(Name) != Validate) {
        calculableFormData += formControlData
      }
    }
    calculableFormData
  }

  private def shouldValidate: Boolean = {
    var shouldValidate = false
    for (formControlData: js.Dictionary[String] ← serializedFormData) {
      if (formControlData(Name) == Validate) {
        shouldValidate &= formControlData(Value).nonEmpty
      }
    }
    shouldValidate
  }

  private def invokeAddition(): Unit = {
    val futureResult: Future[ValidatedResult[BigDecimal]] =
      client[Calculator].add(augend, addend).call()
    for (result: ValidatedResult[BigDecimal] ← futureResult) {
      result match {
        case Left(validationFailures) ⇒
          for (validationFailure: ValidationFailure ← validationFailures) {
            formElement
              .find(elementNamed(validationFailure.formControlName))
              .addClass(DangerClass)
          }
        case Right(summand: BigDecimal) ⇒
          summandElement
            .value(summand.toString)
            .addClass(InfoClass)
      }
    }
  }

  private def augend: String = getValueOrBlank(Augend)

  private def addend: String = getValueOrBlank(Addend)

  private def getValueOrBlank(name: String): String = {
    for (formControlData: js.Dictionary[String] ← calculableFormData) {
      val currentName: String = formControlData(Name)
      val currentValue: String = formControlData(Value)
      if (currentName == name) {
        return currentValue
      }
    }
    ""
  }

}
