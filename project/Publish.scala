import sbt.Keys.*
import sbt.*

object Publish {

  lazy val ghUsername = "deepakmohanakrishnanin"
  lazy val ghRepo = "sparkify"

  lazy val settings = Seq(
    description := "sparkify ETL Utilities",
    homepage := Some(url(s"https://github.com/$ghUsername/$ghRepo")),
    pomIncludeRepository := { _ => false },

    publishTo :=  {
      val nexus = "https://s01.oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
    ),
    publishMavenStyle := true
  )
}
