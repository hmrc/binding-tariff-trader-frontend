resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
// Try to remove when sbt 1.8.0+ and scoverage is 2.0.7+
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

addSbtPlugin("com.typesafe.play"   % "sbt-plugin"               % "2.8.21")
addSbtPlugin("com.typesafe.sbt"    % "sbt-digest"               % "1.1.4")
addSbtPlugin("net.ground5hark.sbt" % "sbt-concat"               % "0.2.0")
addSbtPlugin("io.github.irundaia"  % "sbt-sassify"              % "1.5.2")
addSbtPlugin("org.scalastyle"      % "scalastyle-sbt-plugin"    % "1.0.0")
addSbtPlugin("org.scoverage"       % "sbt-scoverage"            % "2.0.9")
addSbtPlugin("uk.gov.hmrc"         % "sbt-auto-build"           % "3.15.0")
addSbtPlugin("uk.gov.hmrc"         % "sbt-distributables"       % "2.4.0")
addSbtPlugin("com.timushev.sbt"    % "sbt-updates"              % "0.6.3")
addSbtPlugin("org.scalameta"       % "sbt-scalafmt"             % "2.5.2")
addSbtPlugin("uk.gov.hmrc"         % "sbt-accessibility-linter" % "0.38.0")
