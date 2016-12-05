package com.redi.scalademo.presentation

import java.util.{List => JavaList}
import javax.ws.rs._
import javax.ws.rs.core.PathSegment

import com.redi.scalademo.business.Calculator
import com.redi.scalademo.infrastructure.{MediaType, Server}
import upickle.Js
import upickle.Js.Value

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

@Path("/{methodName: .*}")
class ApiEndpoint {

  import ExecutionContext.Implicits.global

  val timeout: Duration = 5.seconds

  val server: Server = ServerModule.server
  val calculator: Calculator = ServerModule.calculator

  @POST
  @Consumes(Array(MediaType.Json))
  @Produces(Array(MediaType.Json))
  def invoke(
    @PathParam("methodName") rawPathSegments: JavaList[PathSegment],
    jsonBody: String
  ): String = {
    val pathSegments: Seq[String] = rawPathSegments.asScala.map(_.getPath)
    val futureResult: Future[Value] = server.route[Calculator](calculator)(
      autowire.Core.Request(
        pathSegments,
        upickle.json.read(jsonBody).asInstanceOf[Js.Obj].value.toMap
      )
    )
    Await.result(futureResult, timeout).toString
  }

}
