import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, targetJvm}
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "binding-tariff-trader-frontend"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin, SbtArtifactory)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(majorVersion := 0)
  .settings(
    name := appName,
    scalaVersion := "2.11.11",
    targetJvm := "jvm-1.8",
    RoutesKeys.routesImport += "models._",
    PlayKeys.playDefaultPort := 9000,
    scalacOptions ++= Seq("-Xfatal-warnings", "-feature"),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(true),
    parallelExecution in Test := false,
    fork in Test := false,
    retrieveManaged := true,
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/bindingtarifftraderfrontend-app.js" ->
        group(Seq("javascripts/show-hide-content.js", "javascripts/bindingtarifftraderfrontend.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    UglifyKeys.compressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("bindingtarifftraderfrontend-*.js")
  )
  .settings(inConfig(TemplateTest)(Defaults.testSettings): _*)
  .settings(
    unmanagedSourceDirectories in Test := Seq((baseDirectory in Test).value / "test"),
    addTestReportOption(Test, "test-reports")
  )
  .configs(IntegrationTest)
  .settings(inConfig(TemplateItTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := Seq((baseDirectory in IntegrationTest).value / "it"),
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)

lazy val allPhases = "tt->test;test->test;test->compile;compile->compile"
lazy val allItPhases = "tit->it;it->it;it->compile;compile->compile"

lazy val TemplateTest = config("tt") extend Test
lazy val TemplateItTest = config("tit") extend IntegrationTest

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = {
  tests map {
    test => Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }
}

// Coverage configuration
coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*repositories.*;" +
  ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;" +
  ".*ControllerConfiguration;.*LanguageSwitchController"
coverageMinimum := 80
coverageFailOnMinimum := true
coverageHighlighting := true
