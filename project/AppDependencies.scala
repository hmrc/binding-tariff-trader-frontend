import sbt._

object AppDependencies {

  import play.core.PlayVersion

  private val silencerVersion      = "1.7.11"
  private val bootstrapPlayVersion = "7.3.0"
  private val hmrcMongoPlayVersion = "0.73.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "commons-validator"            % "commons-validator"              % "1.7",
    "uk.gov.hmrc"                  %% "play-frontend-hmrc"            % "3.27.0-play-28",
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-28"    % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "http-caching-client"           % "9.6.0-play-28",
    "uk.gov.hmrc"                  %% "play-language"                 % "5.3.0-play-28",
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"            % hmrcMongoPlayVersion,
    "commons-codec"                % "commons-codec"                  % "1.15",
    "uk.gov.hmrc"                  %% "play-allowlist-filter-play-28" % "1.1.0",
    "uk.gov.hmrc"                  %% "play-json-union-formatter"     % "1.15.0-play-28",
    "org.typelevel"                %% "cats-core"                     % "2.8.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"          % "2.13.4",
    compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "com.github.tomakehurst" % "wiremock-jre8"            % "2.33.2",
    "com.typesafe.play"      %% "play-test"               % PlayVersion.current,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % hmrcMongoPlayVersion,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % bootstrapPlayVersion,
    "org.scalatest"          %% "scalatest"               % "3.2.13",
    "org.scalatestplus"      %% "mockito-4-6"             % "3.2.13.0",
    "org.scalatestplus"      %% "scalacheck-1-16"         % "3.2.13.0",
    "com.vladsch.flexmark"   % "flexmark-all"             % "0.62.2",
    "io.github.wolfendale"   %% "scalacheck-gen-regexp"   % "1.0.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
