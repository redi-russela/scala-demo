scalaVersion in ThisBuild := "2.12.0"

val AutowireVersion = "0.2.6"
val MacWireVersion = "2.2.5"
val UPickleVersion = "0.4.4"
val ScalaTestVersion = "3.0.1"

val JerseyVersion = "2.24.1"

val JQueryVersion = "2.2.4"

val commonSettings = Seq(
  name := "scala-demo",
  organization := "com.redi",
  version := "0.1.0-SNAPSHOT",
  ivyScala := ivyScala.value.map {
    _.copy(overrideScalaVersion = true)
  }
)

lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared"))
    .disablePlugins(AssemblyPlugin)
    .settings(commonSettings: _*)
    .settings(
      name += "-shared",
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "autowire" % AutowireVersion,
        "com.lihaoyi" %%% "upickle" % UPickleVersion,
        "com.softwaremill.macwire" %% "macros" % MacWireVersion % "provided",
        "org.scalatest" %%% "scalatest" % ScalaTestVersion % "test"
      )
    )
    .jvmSettings(
      libraryDependencies ++= Seq(
        "com.softwaremill.macwire" %% "proxy" % MacWireVersion,
        "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
      )
    )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm

lazy val client = (project in file("client"))
    .enablePlugins(ScalaJSPlugin)
    .disablePlugins(AssemblyPlugin)
    .settings(commonSettings: _*)
    .settings(
      persistLauncher := true,  // only without bundler
      name += "-client",
      artifactPath in (Compile, fastOptJS) := file(crossTarget.value / moduleName.value + ".js"),
      artifactPath in (Compile, fullOptJS) := (artifactPath in (Compile, fastOptJS)).value,
      libraryDependencies ++= Seq(
        "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
        "org.scala-js" %%% "scalajs-dom" % "0.9.1",
        "com.softwaremill.macwire" %% "macros" % MacWireVersion % "provided",
        "org.scalatest" %%% "scalatest" % ScalaTestVersion % "test"
      ),
      jsDependencies ++= Seq(
        "org.webjars" % "jquery" % JQueryVersion
          / s"$JQueryVersion/jquery.js"
          minified s"$JQueryVersion/jquery.min.js"
      )
    )
    .dependsOn(sharedJs)

lazy val server = (project in file("server"))
    .enablePlugins(SbtWeb)
    .settings(commonSettings: _*)
    .settings(
      name += "-server",
      scalaJSProjects := Seq(client),
      pipelineStages in Assets := Seq(scalaJSPipeline),
      unmanagedResourceDirectories in Assets ++= Seq(
        (baseDirectory in client).value / "src" / "main" / "public"
      ),
      WebKeys.packagePrefix in Assets := "public/",
      managedClasspath in Runtime += (packageBin in Assets).value,
      libraryDependencies ++= Seq(
        "org.glassfish.jersey.containers" % "jersey-container-jetty-servlet" % JerseyVersion,
        "com.softwaremill.macwire" %% "macros" % MacWireVersion % "provided",
        "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
      ),
      run := {
        val log: Logger = streams.value.log
        assembly.result.value match {
          case Inc(_) =>
            log.error("Unable to start due to assembly failure.")
          case Value(_) =>
            log.info("Starting...")
            val jarLocation: String = (assemblyOutputPath in assembly).value.getCanonicalPath
            s"java -jar $jarLocation".!
        }
      }
    )
    .dependsOn(sharedJvm)

cancelable in Global := true

run := (run in server).evaluated
