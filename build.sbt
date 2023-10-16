ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "sparkify",
  )
  .settings(ProjectSettings.root)
  .aggregate(common)

lazy val common = (project in file("common"))
  .settings(
    name := "sparkify-common",
  )
  .settings(ProjectSettings.common)

