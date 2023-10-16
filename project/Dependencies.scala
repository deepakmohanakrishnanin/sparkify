
import sbt.Keys.*
import sbt.*

object Dependencies {

  lazy val classDependencyCompile = "compile->compile"
  lazy val classDependencyTest = "test->test"
  lazy val classDependencyCompileTest = "test->test;compile->compile"

  lazy val scala = "2.12.17"
  lazy val scalaTest = "3.2.15"
  lazy val sparkV = "3.4.1"
  lazy val scopt = "4.1.0"
  lazy val pureConfig = "0.17.4"
  lazy val scalaLogging = "3.9.3"
  lazy val mockito = "5.2.0"
  lazy val hadoopVersion = "3.3.2"

  lazy val sparkLibraries: Seq[ModuleID] = Seq(
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" %% "spark-sql" % sparkV,
  ).map(_ % Provided)


  lazy val testingLibraries: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % scalaTest,
    "org.scalatest" %% "scalatest-flatspec" % scalaTest,
    "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0",
    "org.mockito" % "mockito-core" % mockito,

    "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
    "org.apache.hadoop" % "hadoop-aws" % hadoopVersion,
  ).map(_ % Test)

  lazy val miscLibraries: Seq[ModuleID] = Seq(
    "com.github.scopt" %% "scopt" % scopt,
    "io.github.spark-redshift-community" % "spark-redshift_2.11" % "4.1.1",
    "software.amazon.awssdk" % "aws-sdk-java" % "2.21.0",
    "mysql" % "mysql-connector-java" % "8.0.33",
    "com.github.pureconfig" %% "pureconfig" % pureConfig,
  )

  lazy val commonLibraryDependencies = libraryDependencies ++= sparkLibraries ++ testingLibraries ++ miscLibraries

}
