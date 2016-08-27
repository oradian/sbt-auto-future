organization in ThisBuild := "com.oradian.sbtautofuture"

lazy val core = project
lazy val runner = project dependsOn core
lazy val plugin = project
