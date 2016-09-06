publishTo := Some(
  if (version.value endsWith "-SNAPSHOT")
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

licenses += (("MIT License", url("https://opensource.org/licenses/MIT")))
startYear := Some(2016)

scmInfo := Some(ScmInfo(
  url("https://github.com/oradian/sbt-auto-future")
, "scm:git:https://github.com/oradian/sbt-auto-future.git"
, Some("scm:git:git@github.com:oradian/sbt-auto-future.git")
))

pomExtra :=
<developers>
  <developer>
    <id>melezov</id>
    <name>Marko Elezovi&#263;</name>
    <url>https://github.com/melezov</url>
  </developer>
</developers>

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

homepage := Some(url("https://github.com/oradian/sbt-auto-future"))
