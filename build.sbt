ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "sparkify",
  )
  .settings(ProjectSettings.root)
  .aggregate(common)

lazy val startMotoSubProcess = taskKey[Unit]("start moto server")

// This prepends the String you would type into the shell
lazy val startupTransition: State => State = { s: State =>
  "startMotoSubProcess" :: s
}

lazy val common = (project in file("common"))
  .settings(
    name := "sparkify-common",

    startMotoSubProcess := {
      sys.process.Process("moto_server -p 9999").run()
    },

    // onLoad is scoped to Global because there's only one.
    Global / onLoad := {
      val old = (Global / onLoad).value
      // compose the new transition on top of the existing one
      // in case your plugins are using this hook.
      startupTransition compose old
    }
  )
  .settings(ProjectSettings.common)

