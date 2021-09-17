import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    "commons-validator" % "commons-validator" % "1.7",
    "uk.gov.hmrc"   %% "play-frontend-hmrc"         % "1.9.0-play-28",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-28" % "5.14.0",
    "uk.gov.hmrc"   %% "http-caching-client"        % "9.5.0-play-28",
    "uk.gov.hmrc"   %% "mongo-lock"                 % "7.0.0-play-28",
    "uk.gov.hmrc"   %% "play-language"              % "5.1.0-play-28",
    "uk.gov.hmrc"   %% "simple-reactivemongo"       % "8.0.0-play-28",
    "uk.gov.hmrc"   %% "play-allowlist-filter"      % "1.0.0-play-28",
    "uk.gov.hmrc"   %% "play-json-union-formatter"  % "1.15.0-play-28",
    "org.typelevel" %% "cats-core"                  % "2.6.1"
  )

  private lazy val scope: String = "test"

  val test = Seq(
    "com.github.tomakehurst" % "wiremock-jre8"             % "2.26.1"            % scope,
    "com.typesafe.play"      %% "play-test"                % PlayVersion.current % scope,
    "uk.gov.hmrc"            %% "reactivemongo-test"       % "5.0.0-play-28",
    "org.mockito"            % "mockito-core"              % "3.11.2"            % scope,
    "org.pegdown"            % "pegdown"                   % "1.6.0"             % scope,
    "org.jsoup"              % "jsoup"                     % "1.14.1"            % scope,
    "org.scalatest"          %% "scalatest"                % "3.2.9"             % scope,
    "org.scalatestplus"      %% "mockito-3-4"              % "3.2.9.0"           % scope,
    "org.scalatestplus"      %% "scalacheck-1-15"          % "3.2.9.0"           % scope,
    "org.scalatestplus.play" %% "scalatestplus-play"       % "5.1.0"             % scope,
    "wolfendale"             %% "scalacheck-gen-regexp"    % "0.1.2"             % scope,
    "com.vladsch.flexmark"   %  "flexmark-all"             % "0.36.8"            % scope
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
