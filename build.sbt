val scala3Version = "3.8.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "JohnScala67",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.1.0"
  )
