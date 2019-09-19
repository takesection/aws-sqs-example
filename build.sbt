import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val receiverApp = (project in file("receiver-app"))
  .settings(
    name := "receiver-app",
    mainClass := Some("jp.pigumer.example.Main"),
    libraryDependencies ++= ReceiverAppDeps
  )

lazy val sandboxApp = (project in file("sandbox"))
  .settings(
    name := "sandbox-app",
    mainClass := Some("jp.pigumer.example.Sandbox"),
    libraryDependencies ++= ReceiverAppDeps
  )
