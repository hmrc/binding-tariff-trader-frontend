import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    "commons-validator" % "commons-validator" % "1.7",
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"   %% "auth-client"                % "5.6.0-play-27",
    "uk.gov.hmrc"   %% "play-frontend-hmrc"         % "0.79.0-play-27",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-27" % "5.3.0",
    "uk.gov.hmrc"   %% "govuk-template"             % "5.68.0-play-27",
    "uk.gov.hmrc"   %% "http-caching-client"        % "9.5.0-play-27",
    "uk.gov.hmrc"   %% "mongo-lock"                 % "7.0.0-play-27",
    "uk.gov.hmrc"   %% "play-health"                % "3.16.0-play-27",
    "uk.gov.hmrc"   %% "play-language"              % "5.1.0-play-27",
    //"uk.gov.hmrc"   %% "play-ui"                    % "9.5.0-play-27",
    "uk.gov.hmrc"   %% "simple-reactivemongo"       % "8.0.0-play-27",
    "uk.gov.hmrc"   %% "play-allowlist-filter"      % "1.0.0-play-27",
    "uk.gov.hmrc"   %% "play-json-union-formatter"  % "1.13.0-play-27",
    "org.typelevel" %% "cats-core"                  % "2.2.0"
  )

  private lazy val scope: String = "test"

  val test = Seq(
    "com.github.tomakehurst" % "wiremock-jre8"          % "2.26.1" % scope,
    "com.typesafe.play"      %% "play-test"             % PlayVersion.current % scope,
    "uk.gov.hmrc"            %% "reactivemongo-test"    % "5.0.0-play-27",
    "org.mockito"            % "mockito-core"           % "3.11.0" % scope,
    "org.pegdown"            % "pegdown"                % "1.6.0" % scope,
    "org.jsoup"              % "jsoup"                  % "1.13.1" % scope,
    "org.scalacheck"         %% "scalacheck"            % "1.15.4" % scope,
    "org.scalatest"          %% "scalatest"             % "3.0.9" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play"    % "4.0.3" % scope,
    "wolfendale"             %% "scalacheck-gen-regexp" % "0.1.2" % scope
  )

  def apply(): Seq[ModuleID] = (compile ++ test).map(_ withSources ())
}
