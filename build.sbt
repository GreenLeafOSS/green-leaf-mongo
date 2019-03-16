name := "green-leaf-mongo"
organization := "io.github.greenleafoss"

version := "0.1.2"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-explaintypes",
  "-deprecation",
  "-Xlint:-missing-interpolator,_",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:params"
)

parallelExecution in Test := false
fork in Test := true

//libraryDependencies += "io.spray" %% "spray-json" % "1.3.4"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.8.0-beta4"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.8.0-beta4" % Test

libraryDependencies += "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.1.1" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

resolvers += Resolver.jcenterRepo
licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))
