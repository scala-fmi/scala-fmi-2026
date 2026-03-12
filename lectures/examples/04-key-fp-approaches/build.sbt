name := "key-fp-approaches"
version := "0.1"

scalaVersion := "3.8.2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

scalacOptions ++= Seq(
  "-new-syntax",
  "-Werror",
  "-deprecation"
)
