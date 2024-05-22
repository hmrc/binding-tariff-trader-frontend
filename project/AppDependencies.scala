import sbt.*

object AppDependencies {

  private val bootstrapPlayVersion = "8.6.0"
  private val hmrcMongoPlayVersion = "1.9.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "commons-validator"            % "commons-validator"           % "1.8.0",
    "uk.gov.hmrc"                  %% "play-frontend-hmrc-play-30" % "8.5.0",
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"         % hmrcMongoPlayVersion,
    "commons-codec"                % "commons-codec"               % "1.17.0",
    "uk.gov.hmrc"                  %% "play-json-union-formatter"  % "1.21.0",
    "org.typelevel"                %% "cats-core"                  % "2.10.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.17.1",
    "org.apache.pekko"             %% "pekko-connectors-csv"       % "1.0.2"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30" % hmrcMongoPlayVersion,
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.scalatestplus"    %% "scalacheck-1-17"         % "3.2.18.0",
    "org.mockito"          %% "mockito-scala-scalatest" % "1.17.31",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"   % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
