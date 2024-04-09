import sbt.Setting
import scoverage.ScoverageKeys._

object CodeCoverageSettings {
  val settings: Seq[Setting[?]] = Seq(
    coverageExcludedFiles := ".*components.*;.*repositories.*;.*Routes.*",
    coverageMinimumStmtTotal := 96,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )
}
