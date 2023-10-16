import com.eed3si9n.jarjarabrams.ShadeRule
import sbtassembly.AssemblyKeys.*
import sbt.Keys.*
import sbtassembly.{MergeStrategy, PathList}

object Assembly {
  lazy val settings = Seq(
    assemblyJarName := s"${name.value.toLowerCase}-${version.value.toLowerCase}.jar",

    assembly / assemblyShadeRules := Seq(
      ShadeRule.rename("org.typelevel.cats.**" -> "repackaged.org.typelevel.cats.@1").inAll,
      ShadeRule.rename("cats.**" -> "repackaged.cats.@1").inAll,
      ShadeRule.rename("shapeless.**" -> "new_shapeless.@1").inAll,
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )
}
