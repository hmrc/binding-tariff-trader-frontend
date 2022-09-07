import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, integrationTestSettings, targetJvm}
import uk.gov.hmrc.gitstamp.GitStampPlugin._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "binding-tariff-trader-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(integrationTestSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(majorVersion := 0)
  .settings(
    commonSettings,
    name := appName,
    scalaVersion := "2.12.16",
    targetJvm := "jvm-1.8",
    RoutesKeys.routesImport ++= Seq("models._", "models.Languages._", "models.SortField._", "models.SortDirection._"),
    PlayKeys.playDefaultPort := 9582,
    scalacOptions ~= { opts => opts.filterNot(Set("-Xfatal-warnings", "-Ywarn-value-discard")) },
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    Test / fork := true,
    resolvers += Resolver.jcenterRepo,
    // Use the silencer plugin to suppress warnings from unused imports in compiled twirl templates
    scalacOptions += "-P:silencer:pathFilters=views;routes",
    Concat.groups := Seq(
      "javascripts/bindingtarifftraderfrontend-app.js" ->
        group(Seq("javascripts/show-hide-content.js", "javascripts/bindingtarifftraderfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat)
    // only compress files generated by concat
  )
  .settings(
    Test / resourceDirectory := baseDirectory.value / "test" / "resources",
    addTestReportOption(Test, "test-reports")
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
coverageMinimumStmtTotal := 95
coverageFailOnMinimum := true
coverageHighlighting := true

lazy val commonSettings: Seq[Setting[_]] = publishingSettings ++ gitStampSettings

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle test:scalastyle")
