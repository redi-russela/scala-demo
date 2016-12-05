package com.redi.scalademo.infrastructure

import org.scalajs.dom
import upickle.Js
import upickle.default.{Reader, Writer, readJs, writeJs}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

class Client extends autowire.Client[Js.Value, Reader, Writer] {

  override def doCall(request: Request): Future[Js.Value] = {
    dom.ext.Ajax
      .post(
        url = "/api/" + request.path.mkString("/"),
        data = upickle.json.write(Js.Obj(request.args.toSeq: _*)),
        headers = Map(HttpHeaders.ContentType -> MediaType.Json)
      )
      .map((request: dom.XMLHttpRequest) => request.responseText)
      .map(upickle.json.read)
  }

  override def read[Result: Reader](pickleType: Js.Value) = {
    readJs[Result](pickleType)
  }

  override def write[Result: Writer](result: Result) = {
    writeJs(result)
  }

}
