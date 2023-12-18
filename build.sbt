import uk.gov.hmrc.gitstamp.GitStampPlugin.*

lazy val appName: String = "binding-tariff-trader-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(majorVersion := 0)
  .settings(
    gitStampSettings,
    name := appName,
    scalaVersion := "2.13.12",
    play.sbt.routes.RoutesKeys.routesImport ++= Seq(
      "models._",
      "models.Languages._",
      "models.SortField._",
      "models.SortDirection._"
    ),
    PlayKeys.playDefaultPort := 9582,
    scalacOptions ~= { opts => opts.filterNot(Set("-Xfatal-warnings", "-Ywarn-value-discard")) },
    libraryDependencies ++= AppDependencies(),
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always),
    // To resolve dependency clash between flexmark v0.64.4+ and play-language to run accessibility tests, remove when versions align
    dependencyOverrides += "com.ibm.icu" % "icu4j" % "69.1",
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    ),
    Concat.groups := Seq(
      "javascripts/bindingtarifftraderfrontend-app.js" ->
        group(Seq("javascripts/show-hide-content.js", "javascripts/bindingtarifftraderfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat)
  )
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
    )
  )

// Coverage configuration
coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*components.*;.*repositories.*;" +
  ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;"
coverageMinimumStmtTotal := 96
coverageFailOnMinimum := true
coverageHighlighting := true

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt A11y/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle A11y/scalastyle")
