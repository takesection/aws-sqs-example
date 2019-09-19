import sbt._

object Dependencies {
  lazy val AkkaStreamAlpakkaSqs = "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % "1.1.1"

  lazy val ReceiverAppDeps = Seq(
    AkkaStreamAlpakkaSqs
  )
  lazy val SandboxAppDeps = Seq(
    AkkaStreamAlpakkaSqs
  )
}
