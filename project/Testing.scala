import sbt.Keys.*
import sbt.*
import scoverage.ScoverageKeys.*

object Testing {

  private lazy val testSettings = Seq(
    Test / fork := true,
    Test / parallelExecution := false
  )
  private lazy val coverageSettings = Seq(
    coverageFailOnMinimum := false, //FIXME: set it to true to fail builds on minimum coverage
    coverageHighlighting := true,
    coverageMinimumStmtTotal := 70,
    coverageMinimumBranchTotal := 70,
    coverageMinimumStmtPerPackage := 70,
    coverageMinimumBranchPerPackage := 70,
    coverageMinimumStmtPerFile := 70,
    coverageMinimumBranchPerFile := 70,
  )

  lazy val settings = testSettings ++ coverageSettings
}
