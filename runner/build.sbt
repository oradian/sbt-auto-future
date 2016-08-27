name := "auto-future-runner"
version := "0.1.0"

libraryDependencies ++= Seq(
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1"
, "ch.qos.logback" % "logback-classic" % "1.1.7"
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

assemblyJarName in assembly := s"auto-future-${version}.jar"
