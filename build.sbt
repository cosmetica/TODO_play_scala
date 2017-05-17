name := """todo-play-scala-postgres"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  evolutions,
  filters,
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % Test
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
// // The Play project itself
// lazy val root = (project in file("."))
//   .enablePlugins(Common, PlayScala, GatlingPlugin)
//   .configs(GatlingTest)
//   .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
//   .settings(
//     name := """todo-rest-api""",
//     scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
//   )

// // Documentation for this project:
// //    sbt "project docs" "~ paradox"
// //    open docs/target/paradox/site/index.html
// lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
//   settings(
//     paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
//   )

// resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
