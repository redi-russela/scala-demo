Simple Addition Calculator: A Scala Demo
==============================================================================

Running
------------------------------------------------------------------------------

Run command: `sbt run`
Alternatively, execute the jar file after running `sbt assembly`.

Overview
------------------------------------------------------------------------------

This project uses Scala on both the client and server side, as well as having
shared Scala sources used by both sides.

Using the same language for the full stack and having shared sources allows us
in this project allows for both the server-side and client-side to:
* Use the same exact validation logic,
* Share the RMI interface.

### Key technologies

Server-side:
* [Jersey](https://jersey.java.net/) on [Jetty](http://www.eclipse.org/jetty/)

Client-side:
* [jQuery](https://jquery.com/)

Shared:
* [Autowire](https://github.com/lihaoyi/autowire) for RMI
* [ScalaTest](http://www.scalatest.org/) for testing
