package com.redi.scalademo.presentation

import autowire.{clientCallable, unwrapClientProxy}
import com.redi.scalademo.business.{Calculator, NumericStringValidator, ValidatedResult}
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

  def attachTo(formId: String): Unit = {

    val InfoClass = "is-info"
    val DangerClass = "is-danger"

    val Validate = "validate"
    val Augend = "augend"
    val Addend = "addend"
    val Summand = "summand"

    val Name = "name"
    val Value = "value"

    val formElement: JQuery = $(s"form#$formId")
    val summandElement: JQuery = formElement.find(elementNamed(Summand))

    formElement.submit { (e: JQueryEventObject) ⇒

      e.preventDefault()

      formElement.find(namedElement).removeClass(DangerClass)
      val serializedFormData = formElement.serializeArray().asInstanceOf[js.Array[js.Dictionary[String]]]
      val (validationFormData: js.Array[js.Dictionary[String]], calculableFormData: js.Array[js.Dictionary[String]]) =
        serializedFormData.partition(_(Name) == Validate)

      val shouldValidate: Boolean = validationFormData.forall(_(Value).nonEmpty)

      var allFormValuesAreValid = true
      var augend: String = ""
      var addend: String = ""

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

      if (allFormValuesAreValid) {
        val futureResult: Future[ValidatedResult[BigDecimal]] =
          client[Calculator].add(augend, addend).call()
        for (result: ValidatedResult[BigDecimal] ← futureResult) {
          result match {
            case Left(validationFailures) ⇒
              for (validationFailure ← validationFailures) {
                formElement.find(elementNamed(validationFailure.formControlName)).addClass(DangerClass)
              }
            case Right(summand: BigDecimal) ⇒
              summandElement
                .value(summand.toString)
                .addClass(InfoClass)
          }
        }
      }

      ()

    }

    formElement.find(namedElement).on("input", { (self: dom.Element, e: JQueryEventObject) ⇒
      $(self).removeClass(DangerClass)
      summandElement
        .value("")
        .removeClass(InfoClass)
      ()
    }: js.ThisFunction1[dom.Element, JQueryEventObject, js.Any])

  }

  private val namedElement = "[name]"

  private def elementNamed(name: String): String = {
    s"""[name="$name"]"""
  }

}
