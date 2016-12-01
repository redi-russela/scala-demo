package com.redi.scalademo.infrastructure

import upickle.Js
import upickle.default.{Reader, Writer}

class Server extends autowire.Server[Js.Value, Reader, Writer] {

  override def read[Result: Reader](pickleType: Js.Value) = {
    upickle.default.readJs[Result](pickleType)
  }

  override def write[Result: Writer](result: Result) = {
    upickle.default.writeJs(result)
  }

}
