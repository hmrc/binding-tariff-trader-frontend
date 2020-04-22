import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    "commons-validator" % "commons-validator"                 % "1.6",
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "auth-client"                      % "2.22.0-play-25",
    "uk.gov.hmrc"       %% "bootstrap-play-25"                % "4.12.0",
    "uk.gov.hmrc"       %% "govuk-template"                   % "5.31.0-play-25",
    "uk.gov.hmrc"       %% "http-caching-client"              % "8.4.0-play-25",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"    % "0.2.0",
    "uk.gov.hmrc"       %% "play-health"                      % "3.14.0-play-25",
    "uk.gov.hmrc"       %% "play-language"                    % "3.4.0",
    "uk.gov.hmrc"       %% "play-ui"                          % "7.40.0-play-25",
    "uk.gov.hmrc"       %% "simple-reactivemongo"             % "7.20.0-play-25",
    "uk.gov.hmrc"       %% "play-whitelist-filter"            % "2.0.0",
    "uk.gov.hmrc"       %% "play-json-union-formatter"        % "1.5.0",
    "org.webjars.npm"   %  "accessible-autocomplete"          % "2.0.2"

  )

  private lazy val scope: String = "test,it"

  val test = Seq(
    "com.github.tomakehurst"     % "wiremock"               % "2.22.0"            % scope,
    "com.typesafe.play"         %% "play-test"              % PlayVersion.current % scope,
    "org.mockito"                % "mockito-core"           % "2.26.0"            % scope,
    "org.pegdown"                % "pegdown"                % "1.6.0"             % scope,
    "org.jsoup"                  % "jsoup"                  % "1.11.3"            % scope,
    "org.scalacheck"            %% "scalacheck"             % "1.14.0"            % scope,
    "org.scalatest"             %% "scalatest"              % "3.0.4"             % scope,
    "org.scalatestplus.play"    %% "scalatestplus-play"     % "2.0.1"             % scope,
    "uk.gov.hmrc"               %% "hmrctest"               % "3.8.0-play-25"     % scope
  )

  def apply(): Seq[ModuleID] = (compile ++ test).map(_ withSources())
}
