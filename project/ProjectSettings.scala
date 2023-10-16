import sbt.Keys.*
import sbt.{Def, *}

object ProjectSettings {

  private lazy val general = Seq(
    version := version.value,
    scalaVersion := Dependencies.scala,
    organization := "com.sparkify",
    organizationName := "sparkify",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfuture", "-Xlint"),
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    cancelable in Global := true //allow to use Ctrl + C in sbt prompt
  )

  private lazy val commonSettings = general ++ Testing.settings ++ Publish.settings ++ Assembly.settings

  lazy val root = commonSettings

  lazy val common: Seq[Def.Setting[?]] = commonSettings ++ Dependencies.commonLibraryDependencies
}
