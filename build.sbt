organization := "org.mmarini"

name := "linprog"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "net.jcazevedo" %% "moultingyaml" % "0.3.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Test

libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % Test

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.6" % Test

lazy val root = project in file(".")
