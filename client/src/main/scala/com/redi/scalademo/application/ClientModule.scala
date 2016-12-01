package com.redi.scalademo.application

import com.redi.scalademo.business.NumericStringValidator
import com.redi.scalademo.infrastructure.Client
import com.redi.scalademo.presentation.FormListener

private[application] object ClientModule {

  lazy val numericStringValidator = new NumericStringValidator
  lazy val client = new Client
  lazy val formListener = new FormListener(client, numericStringValidator)

}
