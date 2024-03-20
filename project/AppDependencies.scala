import sbt.*

object AppDependencies {

  private val bootstrapPlayVersion = "8.5.0"
  private val hmrcMongoPlayVersion = "1.8.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "commons-validator"            % "commons-validator"           % "1.8.0",
    "uk.gov.hmrc"                  %% "play-frontend-hmrc-play-30" % "8.5.0",
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"         % hmrcMongoPlayVersion,
    "commons-codec"                % "commons-codec"               % "1.16.0",
    "uk.gov.hmrc"                  %% "play-json-union-formatter"  % "1.21.0",
    "org.typelevel"                %% "cats-core"                  % "2.10.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.17.0",
    "org.apache.pekko"             %% "pekko-connectors-csv"       % "1.0.2"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "org.wiremock"         % "wiremock-standalone"      % "3.4.2",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30" % hmrcMongoPlayVersion,
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.scalatest"        %% "scalatest"               % "3.2.18",
    "org.scalatestplus"    %% "scalacheck-1-17"         % "3.2.18.0",
    "org.mockito"          %% "mockito-scala-scalatest" % "1.17.30",
    "com.vladsch.flexmark" % "flexmark-all"             % "0.64.8",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"   % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
