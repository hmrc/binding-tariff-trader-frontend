resolvers += "HMRC-open-artefacts-maven2" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
// Try to remove when sbt 1.8.0+ and scoverage is 2.0.7+
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

addSbtPlugin("com.typesafe.play"         % "sbt-plugin"         % "2.8.19")
addSbtPlugin("com.typesafe.sbt"          % "sbt-digest"         % "1.1.4")
addSbtPlugin("net.ground5hark.sbt"       % "sbt-concat"         % "0.2.0")
addSbtPlugin("io.github.irundaia"        % "sbt-sassify"        % "1.5.2")
addSbtPlugin("com.beautiful-scala"       % "sbt-scalastyle"     % "1.5.1")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"      % "2.0.7")
addSbtPlugin("uk.gov.hmrc"               % "sbt-auto-build"     % "3.9.0")
addSbtPlugin("uk.gov.hmrc"               % "sbt-distributables" % "2.2.0")
addSbtPlugin("com.timushev.sbt"          % "sbt-updates"        % "0.6.3")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"       % "2.4.6")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"       % "0.3.3")
addDependencyTreePlugin
