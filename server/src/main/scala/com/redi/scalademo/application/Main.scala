package com.redi.scalademo.application

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.resource.Resource
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.servlet.ServletContainer

object Main extends App {

  val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
  context.setContextPath("/")

  val jettyServer = new Server(8080)
  jettyServer.setHandler(context)

  val jerseyServlet: ServletHolder =
    context.addServlet(classOf[ServletContainer], "/api/*")
  jerseyServlet.setInitOrder(0)

  val defaultServlet: ServletHolder =
    context.addServlet(classOf[DefaultServlet], "/")
  val publicPath: String =
    Resource.newResource(ClassLoader.getSystemResource("public"))
      .getURI
      .toASCIIString
  defaultServlet.setInitOrder(Int.MaxValue)
  defaultServlet.setInitParameter("resourceBase", publicPath)

  context.setWelcomeFiles(Array("/index.html"))

  jerseyServlet.setInitParameter(
    ServerProperties.PROVIDER_PACKAGES,
    "com.redi.scalademo.presentation")

  try {
    jettyServer.start()
    jettyServer.join()
  } finally {
    jettyServer.destroy()
  }

}
