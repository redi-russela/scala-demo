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

  import FormListener._

  private val namedElement = "[name]"

  private var formElement: JQuery = _
  private var summandElement: JQuery = _

  private var shouldValidate: Boolean = _
  private var allFormValuesAreValid: Boolean = _
  private var calculableFormData: js.Array[js.Dictionary[String]] = _
  private var augend: String = _
  private var addend: String = _

  def attachTo(formId: String): Unit = {

    formElement = $(s"form#$formId")
    summandElement = formElement.find(elementNamed(Summand))

    formElement.submit { (e: JQueryEventObject) ⇒
      e.preventDefault()
      init()
      processValidationFormData()
      validateAndExtractNumbers()
      invokeAddition()
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

  private def init(): Unit = {
    shouldValidate = true
    allFormValuesAreValid = true
    calculableFormData = js.Array[js.Dictionary[String]]()
    augend = ""
    addend = ""
  }

  private def processValidationFormData(): Unit = {
    formElement.find(namedElement).removeClass(DangerClass)
    val serializedFormData = formElement.serializeArray().asInstanceOf[js.Array[js.Dictionary[String]]]
    for (formControlData: js.Dictionary[String] ← serializedFormData) {
      if (formControlData(Name) == Validate) {
        shouldValidate = formControlData(Value).nonEmpty
      } else {
        calculableFormData += formControlData
      }
    }
  }

  private def validateAndExtractNumbers(): Unit = {
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
      name match {
        case Augend ⇒
          augend = value
        case Addend ⇒
          addend = value
      }
    }
  }

  private def invokeAddition(): Unit = {
    if (allFormValuesAreValid) {
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
  }

}

object FormListener {

  private val InfoClass = "is-info"
  private val DangerClass = "is-danger"

  private val Validate = "validate"
  private val Augend = "augend"
  private val Addend = "addend"
  private val Summand = "summand"

  private val Name = "name"
  private val Value = "value"

}
