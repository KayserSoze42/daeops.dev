ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "dev.daeops"

// Versions
val AkkaVersion = "2.9.0"
val AkkaHttpVersion = "10.6.0"
val CirceVersion = "0.14.5"

// Common settings
lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:implicitConversions",
    "-unchecked"
  )
)

// Dependencies
lazy val commonDependencies = Seq(

  "io.spray" %%  "spray-json" % "1.3.6",

  // Akka
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,

  // JSON
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,

  // Testing
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

// Core project
lazy val core = (project in file("core"))
  .settings(
    name := "daeops-core",
    commonSettings,
    libraryDependencies ++= commonDependencies
  )

// Root project
lazy val root = (project in file("."))
  .aggregate(core)
  .settings(
    name := "daeops.dev"
  )

// Resolver settings
ThisBuild / resolvers ++= Seq(
  "Akka library repository".at("https://repo.akka.io/maven"),
  "Typesafe repository".at("https://repo.typesafe.com/typesafe/releases/")
)