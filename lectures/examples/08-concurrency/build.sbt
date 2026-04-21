name := "concurrency"
version := "0.1"

scalaVersion := "3.8.2"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Werror",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "cats-effect" % "3.7.0",
  "org.asynchttpclient" % "async-http-client" % "2.12.3",
  "org.http4s" %% "http4s-dsl" % "0.23.26",
  "org.http4s" %% "http4s-ember-server" % "0.23.26",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
