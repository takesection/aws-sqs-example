import Dependencies._
import Log4j2MergeStrategy._

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


lazy val senderApp = (project in file("sender-app"))
  .settings(
    name := "sender-app",
    assemblyMergeStrategy in assembly := {
      case PathList(ps @ _*) if ps.last == "Log4j2Plugins.dat" => Log4j2MergeStrategy.plugincache
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    libraryDependencies ++= SenderAppDeps
  )

lazy val sandboxApp = (project in file("sandbox"))
  .settings(
    name := "sandbox-app",
    mainClass := Some("jp.pigumer.example.Sandbox"),
    libraryDependencies ++= ReceiverAppDeps
  )
