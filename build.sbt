// **************************************************
// SETTINGS
// **************************************************

lazy val commonSettings = Seq(
  version              := "3.1",
  description          :=
    """
        |This extension created on top of official MongoDB Scala Driver.
        |It allows to fully utilize Spray, Play or Circe JSON and represents bidirectional serialization for case classes in BSON,
        |as well as flexible DSL for MongoDB query operators, documents and collections.
        |""".stripMargin,
  licenses             := List("Apache 2" -> new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage             := Some(url("https://github.com/GreenLeafOSS/green-leaf-mongo")),
  organization         := "io.github.greenleafoss",
  organizationName     := "greenleafoss",
  organizationHomepage := Some(url("https://github.com/greenleafoss")),
  developers           := List(
    Developer(
      id = "lashchenko",
      name = "Andrii Lashchenko",
      email = "andrew.lashchenko@gmail.com",
      url = url("https://github.com/lashchenko")
    )
  ),
  scmInfo              := Some(
    ScmInfo(
      url("https://github.com/GreenLeafOSS/green-leaf-mongo"),
      "scm:git@github.com:GreenLeafOSS/green-leaf-mongo.git"
    )
  ),
  publishTo            := {
    // https://central.sonatype.org/news/20210223_new-users-on-s01/
    val nexus = "https://s01.oss.sonatype.org"
    if (isSnapshot.value) Some("snapshots" at nexus + "/content/repositories/snapshots")
    else Some("releases" at nexus + "/service/local/staging/deploy/maven2")
  },

  // https://central.sonatype.org/news/20210223_new-users-on-s01/
  sonatypeCredentialHost                      := "https://s01.oss.sonatype.org",
  publishMavenStyle                           := true,
  publishConfiguration                        := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration                   := publishLocalConfiguration.value.withOverwrite(true),
  scalaVersion                                := "3.3.1",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-explain"
  ),
  scalafmtOnCompile                           := true,
  Test / parallelExecution                    := false,
  Test / fork                                 := true,
  libraryDependencies += "org.slf4j"           % "slf4j-api"                 % "2.0.12",
  libraryDependencies += "org.slf4j"           % "slf4j-simple"              % "2.0.13" % Test,
  libraryDependencies += "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "3.5.4"  % Test,
  libraryDependencies += "org.scalatest"      %% "scalatest"                 % "3.2.18" % Test,
  libraryDependencies += "org.immutables"      % "value"                     % "2.10.1" % Test
)

// **************************************************
// PROJECTS
// **************************************************

lazy val core = (project in file("core"))
  .settings(name := "green-leaf-mongo-core")
  .settings(commonSettings)
  .settings(libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "5.1.2" cross CrossVersion.for3Use2_13)

lazy val spray = (project in file("spray"))
  .settings(name := "green-leaf-mongo-spray")
  .settings(commonSettings)
  .settings(libraryDependencies += "io.spray" %% "spray-json" % "1.3.6")
  .dependsOn(core % "compile->compile;test->test")

lazy val play = (project in file("play"))
  .settings(name := "green-leaf-mongo-play")
  .settings(commonSettings)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.5")
  .dependsOn(core % "compile->compile;test->test")

lazy val circe = (project in file("circe"))
  .settings(name := "green-leaf-mongo-circe")
  .settings(commonSettings)
  .settings(libraryDependencies += "io.circe" %% "circe-core" % "0.14.7")
  .settings(libraryDependencies += "io.circe" %% "circe-generic" % "0.14.7")
  .settings(libraryDependencies += "io.circe" %% "circe-parser" % "0.14.7")
  .dependsOn(core % "compile->compile;test->test")

lazy val extensions: Seq[ProjectReference] = List[ProjectReference](spray, play, circe)
lazy val aggregated: Seq[ProjectReference] = List[ProjectReference](core) ++ extensions

lazy val root = (project in file("."))
  .settings(name := "green-leaf-mongo")
  .settings(commonSettings)
  // we don't need to publish core + spray + play together
  .settings(publish / skip := true)
  .aggregate(aggregated*)
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(extensions.map(_ % "compile->compile;compile->test")*)
