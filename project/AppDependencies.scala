import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "play-reactivemongo"               % "6.2.0",
    "uk.gov.hmrc" %% "logback-json-logger"              % "4.1.0",
    "uk.gov.hmrc" %% "govuk-template"                   % "5.3.0",
    "uk.gov.hmrc" %% "play-health"                      % "3.6.0-play-25",
    "uk.gov.hmrc" %% "play-ui"                          % "7.25.0-play-25",
    "uk.gov.hmrc" %% "http-caching-client"              % "7.2.0",
    "uk.gov.hmrc" %% "play-conditional-form-mapping"    % "0.2.0",
    "uk.gov.hmrc" %% "bootstrap-play-25"                % "3.14.0",
    "uk.gov.hmrc" %% "play-language"                    % "3.4.0",
    "uk.gov.hmrc" %% "play-whitelist-filter"            % "2.0.0"
  )

  lazy val scope: String = "test,it"

  val test = Seq(
    "com.github.tomakehurst"    %  "wiremock"           % "2.19.0"            % scope,
    "uk.gov.hmrc"               %% "hmrctest"           % "3.2.0"             % scope,
    "org.scalatest"             %% "scalatest"          % "3.0.4"             % scope,
    "org.scalatestplus.play"    %% "scalatestplus-play" % "2.0.1"             % scope,
    "org.pegdown"               % "pegdown"             % "1.6.0"             % scope,
    "org.jsoup"                 % "jsoup"               % "1.11.3"            % scope,
    "com.typesafe.play"         %% "play-test"          % PlayVersion.current % scope,
    "org.mockito"               % "mockito-all"         % "1.10.19"           % scope,
    "org.scalacheck"            %% "scalacheck"         % "1.14.0"            % scope
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
