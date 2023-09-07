import sbt.*

object AppDependencies {

  private val bootstrapPlayVersion = "7.22.0"
  private val hmrcMongoPlayVersion = "1.3.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "commons-validator"            % "commons-validator"           % "1.7",
    "uk.gov.hmrc"                  %% "play-frontend-hmrc"         % "7.20.0-play-28",
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"         % hmrcMongoPlayVersion,
    "commons-codec"                % "commons-codec"               % "1.16.0",
    "uk.gov.hmrc"                  %% "play-json-union-formatter"  % "1.18.0-play-28",
    "org.typelevel"                %% "cats-core"                  % "2.10.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.15.2"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "org.wiremock"         % "wiremock-standalone"      % "3.1.0",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-28" % hmrcMongoPlayVersion,
    "uk.gov.hmrc"          %% "bootstrap-test-play-28"  % bootstrapPlayVersion,
    "org.scalatest"        %% "scalatest"               % "3.2.17",
    "org.scalatestplus"    %% "scalacheck-1-17"         % "3.2.17.0",
    "org.mockito"          %% "mockito-scala-scalatest" % "1.17.22",
    "com.vladsch.flexmark" % "flexmark-all"             % "0.64.8",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"   % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
