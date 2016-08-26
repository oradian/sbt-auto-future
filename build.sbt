version in ThisBuild := "0.0.1"
organization in ThisBuild := "com.oradian.sbtautofuture"

lazy val core = project
lazy val runner = project dependsOn core
lazy val plugin = project
