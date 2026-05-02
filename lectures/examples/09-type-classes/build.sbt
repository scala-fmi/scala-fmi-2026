import scala.collection.Seq

name := "type-classes"
version := "0.1"

scalaVersion := "3.8.2"

scalacOptions ++= Seq(
  "-new-syntax",
  "-Werror",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "spire" % "0.18.0",
  "org.scalatest" %% "scalatest" % "3.2.20" % Test
)
