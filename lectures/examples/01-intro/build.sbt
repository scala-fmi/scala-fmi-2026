name := "scala-examples"
version := "0.1"

scalaVersion := "3.8.1"

fork := true

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "cats-effect" % "3.6.3",
  "co.fs2" %% "fs2-core" % "3.12.2",
  "org.http4s" %% "http4s-dsl" % "0.23.33",
  "org.http4s" %% "http4s-ember-client" % "0.23.33",
  "org.http4s" %% "http4s-ember-server" % "0.23.33",
  "ch.qos.logback" % "logback-classic" % "1.5.28",
  "org.fusesource.jansi" % "jansi" % "1.18"
)
