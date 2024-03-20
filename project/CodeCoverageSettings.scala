import sbt.Setting
import scoverage.ScoverageKeys.{*, coverageHighlighting}

object CodeCoverageSettings {
  val settings: Seq[Setting[?]] = Seq(
    coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*components.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;",
    coverageMinimumStmtTotal := 96,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )
}
