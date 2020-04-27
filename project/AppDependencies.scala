import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    "commons-validator" % "commons-validator"                 % "1.6",
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "auth-client"                      % "2.35.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-play-26"                % "1.7.0",
    "uk.gov.hmrc"       %% "govuk-template"                   % "5.54.0-play-26",
    "uk.gov.hmrc"       %% "http-caching-client"              % "9.0.0-play-26",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % "1.2.0-play-26",
    "uk.gov.hmrc"       %% "play-health"                      % "3.15.0-play-26",
    "uk.gov.hmrc"       %% "play-language"                    % "4.2.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"                          % "8.8.0-play-26",
    "uk.gov.hmrc"       %% "simple-reactivemongo"             % "7.26.0-play-26",
    "uk.gov.hmrc"       %% "play-whitelist-filter"            % "3.1.0-play-26",
    "uk.gov.hmrc"       %% "play-json-union-formatter"        % "1.10.0-play-26",
    "org.webjars.npm"   %  "accessible-autocomplete"          % "2.0.2"

  )

  private lazy val scope: String = "test"

  val test = Seq(
    "com.github.tomakehurst"     % "wiremock-jre8"          % "2.26.1"            % scope,
    "com.typesafe.play"         %% "play-test"              % PlayVersion.current % scope,
    "org.mockito"                % "mockito-core"           % "2.26.0"            % scope,
    "org.pegdown"                % "pegdown"                % "1.6.0"             % scope,
    "org.jsoup"                  % "jsoup"                  % "1.13.1"            % scope,
    "org.scalacheck"            %% "scalacheck"             % "1.14.3"            % scope,
    "org.scalatest"             %% "scalatest"              % "3.0.8"             % scope,
    "org.scalatestplus.play"    %% "scalatestplus-play"     % "3.1.3"             % scope,
    "uk.gov.hmrc"               %% "hmrctest"               % "3.9.0-play-26"     % scope
  )

  def apply(): Seq[ModuleID] = (compile ++ test).map(_ withSources())
}
