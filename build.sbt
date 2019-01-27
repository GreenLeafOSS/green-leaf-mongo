name := "spray-json-mongodb-queries"

version := "0.1"

scalaVersion := "2.12.8"

parallelExecution in Test := false

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

//libraryDependencies += "io.spray" %% "spray-json" % "1.3.4"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.5.0"

libraryDependencies += "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.1.1" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
