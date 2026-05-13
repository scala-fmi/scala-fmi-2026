name := "monads-and-applicatives"
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
  "org.scalatest" %% "scalatest" % "3.2.20" % Test
)
