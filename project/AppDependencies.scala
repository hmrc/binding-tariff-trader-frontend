import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    "commons-validator" % "commons-validator"                 % "1.7",
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "auth-client"                      % "3.2.0-play-27",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"       % "3.2.0",
    "uk.gov.hmrc"       %% "govuk-template"                   % "5.60.0-play-27",
    "uk.gov.hmrc"       %% "http-caching-client"              % "9.2.0-play-27",
    "uk.gov.hmrc"       %% "play-health"                      % "3.16.0-play-27",
    "uk.gov.hmrc"       %% "play-language"                    % "4.5.0-play-27",
    "uk.gov.hmrc"       %% "play-ui"                          % "8.18.0-play-27",
    "uk.gov.hmrc"       %% "simple-reactivemongo"             % "7.31.0-play-27",
    "uk.gov.hmrc"       %% "play-allowlist-filter"            % "0.2.0-play-27",
    "uk.gov.hmrc"       %% "play-json-union-formatter"        % "1.12.0-play-27",
    "org.typelevel"     %% "cats-core"                        % "2.2.0"
  )

  private lazy val scope: String = "test"

  val test = Seq(
    "com.github.tomakehurst"     % "wiremock-jre8"          % "2.26.1"            % scope,
    "com.typesafe.play"         %% "play-test"              % PlayVersion.current % scope,
    "org.mockito"                % "mockito-core"           % "2.26.0"            % scope,
    "org.pegdown"                % "pegdown"                % "1.6.0"             % scope,
    "org.jsoup"                  % "jsoup"                  % "1.13.1"            % scope,
    "org.scalacheck"            %% "scalacheck"             % "1.14.3"            % scope,
    "org.scalatest"             %% "scalatest"              % "3.0.9"             % scope,
    "org.scalatestplus.play"    %% "scalatestplus-play"     % "3.1.3"             % scope,
    "wolfendale"                %% "scalacheck-gen-regexp"  % "0.1.2"             % scope
  )

  def apply(): Seq[ModuleID] = (compile ++ test).map(_ withSources())
}
