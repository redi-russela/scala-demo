package com.redi.scalademo.presentation

import autowire.{clientCallable, unwrapClientProxy}
import com.redi.scalademo.business.{Calculator, NumericStringValidator, ValidatedResult, ValidationFailure}
import com.redi.scalademo.infrastructure.Client
import org.scalajs.dom
import org.scalajs.jquery.{JQuery, JQueryEventObject, jQuery => $}

import scala.collection.mutable.ArrayBuffer
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

  private val Name = "name"
  private val Value = "value"

  private val namedElement = "[name]"

  private var formElement: JQuery = _
  private var summandElement: JQuery = _
  private var augendElement: JQuery = _
  private var addendElement: JQuery = _
  private var clientSideValidationElement: JQuery = _

  def attachTo(formId: String): Unit = {

    formElement = $(s"form#$formId")
    augendElement = formElement.find(elementNamed("augend"))
    addendElement = formElement.find(elementNamed("addend"))
    summandElement = formElement.find(elementNamed("summand"))
    clientSideValidationElement = formElement.find(elementNamed("client-side-validation"))

    formElement.submit { (e: JQueryEventObject) ⇒
      resetValidationErrors()

      if (clientSideValidationEnabled) {
        val invalidElements: Iterable[JQuery] = getInvalidElements
        if (invalidElements.nonEmpty) {
          for (invalidElement <- invalidElements) {
            addValidationError(invalidElement)
          }
        } else {
          invokeAddition()
        }
      } else {
        invokeAddition()
      }

      e.preventDefault()
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

  private def resetValidationErrors(): Unit = {
    formElement.find(namedElement).removeClass(DangerClass)
  }

  private def addValidationError(element: JQuery): Unit = {
    element.addClass(DangerClass)
  }

  private def clientSideValidationEnabled: Boolean = {
    clientSideValidationElement.is(":selected")
  }

  private def getInvalidElements: Iterable[JQuery] = {
    val invalidElements = ArrayBuffer.empty[JQuery]
    for (formControlData: js.Dictionary[String] ← calculableFormData) {
      val name: String = formControlData(Name)
      val value: String = formControlData(Value)
      val isValid: Boolean = numericStringValidator.isValidNumber(value)
      if (!isValid) {
        invalidElements += formElement.find(elementNamed(name))
      }
    }
    invalidElements
  }

  private def serializeFormData(element: JQuery): js.Array[js.Dictionary[String]] = {
    element.serializeArray().asInstanceOf[js.Array[js.Dictionary[String]]]
  }

  private def calculableFormData: js.Array[js.Dictionary[String]] = {
    serializeFormData(augendElement) ++
      serializeFormData(addendElement) ++
      serializeFormData(summandElement)
  }

  private def invokeAddition(): Unit = {
    val futureResult: Future[ValidatedResult[BigDecimal]] =
      client[Calculator].add(augendValue, addendValue).call()
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

  private def augendValue: String = {
    augendElement.value().toString
  }

  private def addendValue: String = {
    addendElement.value().toString
  }

}
