name := "building-a-scala-app"
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

  "org.tpolecat" %% "doobie-core" % "1.0.0-RC12",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC12",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC12",

  "com.typesafe" % "config" % "1.4.2",

  "co.fs2" %% "fs2-core" % "3.13.0",
  "co.fs2" %% "fs2-io" % "3.13.0",
  "co.fs2" %% "fs2-reactive-streams" % "3.13.0",

  "io.circe" %% "circe-core" % "0.14.15",
  "io.circe" %% "circe-generic" % "0.14.15",
  "io.circe" %% "circe-parser" % "0.14.15",

  "org.http4s" %% "http4s-dsl" % "0.23.32",
  "org.http4s" %% "http4s-ember-server" % "0.23.32",
  "org.http4s" %% "http4s-ember-client" % "0.23.32",
  "org.http4s" %% "http4s-circe" % "0.23.32",

  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.13.19",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.13.19",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client4" % "1.13.19",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % "1.13.19",
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.13.19",
  "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % "1.13.19",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub4-server" % "1.13.19" % Test,

  "com.softwaremill.sttp.client4" %% "core" % "4.0.23",
  "com.softwaremill.sttp.client4" %% "fs2" % "4.0.23",
  "com.softwaremill.sttp.client4" %% "circe" % "4.0.24" % Test,

  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.fusesource.jansi" % "jansi" % "1.18",

  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.typelevel" %% "cats-laws" % "2.13.0" % Test,
  "org.typelevel" %% "discipline-scalatest" % "2.3.0" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.8.0" % Test
)
