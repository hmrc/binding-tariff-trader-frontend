import sbt.*

object AppDependencies {

  private val hmrcMongoPlayVersion = "2.6.0"
  private val bootstrapPlayVersion = "9.11.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "commons-validator"      % "commons-validator"          % "1.9.0",
    "uk.gov.hmrc"           %% "play-frontend-hmrc-play-30" % "12.0.0",
    "uk.gov.hmrc"           %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-play-30"         % hmrcMongoPlayVersion,
    "commons-codec"          % "commons-codec"              % "1.18.0",
    "org.typelevel"         %% "cats-core"                  % "2.13.0",
    "commons-io"             % "commons-io"                 % "2.19.0",
    "org.apache.xmlgraphics" % "fop"                        % "2.10",
    "net.sf.saxon"           % "Saxon-HE"                   % "12.5"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30" % hmrcMongoPlayVersion,
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.scalatestplus"    %% "scalacheck-1-18"         % "3.2.19.0",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"   % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
