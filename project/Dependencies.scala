import sbt._

object Dependencies {
  lazy val AkkaStreamAlpakkaSqs = "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % "1.1.1"
  lazy val LambdaSqs = "com.amazonaws" % "aws-java-sdk-sqs" % "1.11.636"

  lazy val AwsLambdaJavaCore    = "com.amazonaws" % "aws-lambda-java-core" % "1.2.0"
  lazy val AwsLambdaJavaLog4j2  = "com.amazonaws" % "aws-lambda-java-log4j2" % "1.1.0"

  lazy val SprayJson = "io.spray" %% "spray-json" % "1.3.5"

  lazy val ReceiverAppDeps = Seq(
    AkkaStreamAlpakkaSqs
  )
  lazy val SandboxAppDeps = Seq(
    AkkaStreamAlpakkaSqs
  )
  lazy val SenderAppDeps = Seq(
    AwsLambdaJavaCore,
    AwsLambdaJavaLog4j2,
    SprayJson,
    LambdaSqs
  )
}
