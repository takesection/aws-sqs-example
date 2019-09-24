package jp.pigumer.example

import java.util.concurrent.Executors

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.alpakka.sqs.SqsSourceSettings
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, Attributes, Materializer}
import com.github.matsluni.akkahttpspi.AkkaHttpClient
import jp.pigumer.sqs.AwsSqs
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient

import scala.concurrent.ExecutionContext

trait ReceiverApp extends AwsSqs with ExampleFlow {

  implicit val system: ActorSystem = ActorSystem("App")
  implicit val mat: Materializer = ActorMaterializer()

  val executionContext = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(100)
  )
  implicit val awsSqsClient: SqsAsyncClient = SqsAsyncClient
    .builder()
    .region(Region.AP_NORTHEAST_1)
    .httpClient(AkkaHttpClient.builder().withActorSystem(system).withExecutionContext(executionContext).build())
    .build()

  system.registerOnTermination(awsSqsClient.close())

  val queueNames: Seq[String] = Seq(
    "HighPriority",
    "HighPriority",
    "HighPriority",
    "HighPriority",
    "HighPriority",
    "HighPriority",
    "HighPriority",
    "NormalPriority",
    "NormalPriority",
    "NormalPriority"
  )

  val runnable: RunnableGraph[NotUsed] = {
    Source(1 to queueNames.length)
      .groupBy(queueNames.length, _ % queueNames.length).async
      .via(
        SqsSourceFlow(
          SqsSourceSettings.Defaults.withMaxBatchSize(1).withMaxBufferSize(1)
        ).via(DeleteFlow)
      )
      .async
      .log("deleted")
      .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .to(Sink.ignore)
  }

  runnable.run()
}
