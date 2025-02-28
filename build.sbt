import uk.gov.hmrc.gitstamp.GitStampPlugin.*

lazy val appName: String = "binding-tariff-trader-frontend"

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / majorVersion := 0

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    gitStampSettings,
    routesImport ++= Seq(
      "models._",
      "models.Languages._",
      "models.SortField._",
      "models.SortDirection._"
    ),
    PlayKeys.playDefaultPort := 9582,
    libraryDependencies ++= AppDependencies(),
    scalacOptions ++= Seq(
      "-feature",
      "-source:3.4-migration",
      "-rewrite"
//      "-Wconf:src=routes/.*:s",
//      "-Wconf:cat=unused-imports&src=views/.*:s"
    ),
    Test / scalacOptions ++= Seq(
      "-feature",
      "-source:3.4-migration",
      "-rewrite"
    ),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "app" / "views" / "components" / "fop",
    Test / unmanagedResourceDirectories += baseDirectory.value / "app" / "views" / "components" / "fop",
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
  .settings(CodeCoverageSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
