import sbt.Keys._

//lazy val GatlingTest = config("gatling") extend Test

scalaVersion := "2.11.8"

libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.14"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"
libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
libraryDependencies += "net.jcazevedo" %% "moultingyaml" % "0.3.0"
libraryDependencies += "org.scalanlp" %% "breeze" % "0.12"
libraryDependencies += "org.scalanlp" %% "breeze-natives" % "0.12"
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14"
libraryDependencies += jdbc
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"
//libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"

//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Test
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.6" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
//libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % Test
//libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.2" % Test
    
// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala)
//  .enablePlugins(Common, PlayScala, GatlingPlugin)
//  .configs(GatlingTest)
//  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """linprog"""
//    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
//lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
//  settings(
// paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
//  )
