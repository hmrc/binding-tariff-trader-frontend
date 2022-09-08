resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

addSbtPlugin("com.typesafe.play"         % "sbt-plugin"             % "2.8.16")
addSbtPlugin("com.typesafe.sbt"          % "sbt-digest"             % "1.1.4")
addSbtPlugin("net.ground5hark.sbt"       % "sbt-concat"             % "0.2.0")
addSbtPlugin("io.github.irundaia"        % "sbt-sassify"            % "1.5.2")
addSbtPlugin("org.scalastyle"            %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"          % "2.0.0")
addSbtPlugin("uk.gov.hmrc"               % "sbt-auto-build"         % "3.7.0")
addSbtPlugin("uk.gov.hmrc"               % "sbt-distributables"     % "2.1.0")
addSbtPlugin("com.timushev.sbt"          % "sbt-updates"            % "0.6.3")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"           % "2.4.6")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"           % "0.3.3")
addDependencyTreePlugin
