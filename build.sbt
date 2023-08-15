
name := "green-leaf-mongo"

version := "0.1.13"

description := "This extension created on top of official MongoDB Scala Driver, allows to fully utilize Spray JSON and represents bidirectional serialization for case classes in BSON, as well as flexible DSL for MongoDB query operators, documents and collections."
licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/GreenLeafOSS/green-leaf-mongo"))

organization := "io.github.greenleafoss"
organizationName := "greenleafoss"
organizationHomepage := Some(url("https://github.com/greenleafoss"))

developers := List(
  Developer(
    id = "lashchenko",
    name = "Andrii Lashchenko",
    email = "andrew.lashchenko@gmail.com",
    url = url("https://github.com/lashchenko")
  )
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/GreenLeafOSS/green-leaf-mongo"),
    "scm:git@github.com:GreenLeafOSS/green-leaf-mongo.git"
  )
)

publishTo := {
  // https://central.sonatype.org/news/20210223_new-users-on-s01/
  val nexus = "https://s01.oss.sonatype.org"
  if (isSnapshot.value) Some("snapshots" at nexus + "/content/repositories/snapshots")
  else Some("releases" at nexus + "/service/local/staging/deploy/maven2")
}

// https://central.sonatype.org/news/20210223_new-users-on-s01/
sonatypeCredentialHost := "https://s01.oss.sonatype.org"

publishMavenStyle := true

publishConfiguration := publishConfiguration.value.withOverwrite(true)


scalaVersion := "3.3.0"

crossScalaVersions := Seq("2.12.18", "2.13.11")

scalacOptions ++= Seq(
  "-deprecation"
)

Test / parallelExecution := false
Test / fork := true

libraryDependencies += "io.spray" %% "spray-json" % "1.3.6"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.10.2" cross CrossVersion.for3Use2_13


libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.7"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.7" % Test

libraryDependencies += "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "4.8.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
libraryDependencies += "org.immutables" % "value" % "2.9.3" % Test
