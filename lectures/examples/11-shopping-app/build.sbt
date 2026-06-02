name := "shopping-app"
version := "0.1"

scalaVersion := "3.8.2"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Werror",
  "-deprecation"
)

val catsVersion = "2.13.0"
val catsEffectVersion = "3.7.0"

val http4sVersion = "0.23.34"
val tapirVersion = "1.13.19"

val circeVersion = "0.14.15"

val flywayVersion = "12.7.0"
val doobieVersion = "1.0.0-RC12"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,

  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-cats" % tapirVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,

  "org.flywaydb" % "flyway-core" % flywayVersion,
  "org.flywaydb" % "flyway-database-postgresql" % flywayVersion,

  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,

  "com.typesafe" % "config" % "1.4.8",

  "org.mindrot" % "jbcrypt" % "0.4",

  "com.github.jwt-scala" %% "jwt-circe" % "11.0.4",

  "ch.qos.logback" % "logback-classic" % "1.5.33",
  "org.fusesource.jansi" % "jansi" % "2.4.3",

  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.typelevel" %% "cats-laws" % catsVersion % Test,
  "org.typelevel" %% "discipline-scalatest" % "2.3.0" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.8.0" % Test
)

fork := true
