name := "auto-future"
version := "0.1.0"

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "1.0.0"
, "org.scala-lang" % "scala-reflect" % scalaVersion.value
, "org.specs2" %% "specs2-scalacheck" % "3.8.4" % Test
)

scalaVersion := "2.11.8"
scalacOptions ++= Seq(
  "-deprecation"
, "-encoding","UTF-8"
, "-feature"
, "-language:_"
, "-unchecked"
, "-Xfuture"
, "-Xlint"
, "-Xverify"
, "-Yclosure-elim"
, "-Yconst-opt"
, "-Ydead-code"
, "-Yinline"
, "-Yinline-warnings:false"
, "-Yrepl-sync"
, "-Ywarn-adapted-args"
, "-Ywarn-dead-code"
, "-Ywarn-inaccessible"
, "-Ywarn-infer-any"
, "-Ywarn-nullary-override"
, "-Ywarn-nullary-unit"
, "-Ywarn-numeric-widen"
, "-Ywarn-unused"
)
