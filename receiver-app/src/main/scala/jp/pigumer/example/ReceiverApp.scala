package jp.pigumer.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Attributes, Materializer}
import com.github.matsluni.akkahttpspi.AkkaHttpClient
import jp.pigumer.sqs.AwsSqs
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient

trait ReceiverApp extends AwsSqs {

  implicit val system: ActorSystem = ActorSystem("App")
  implicit val mat: Materializer = ActorMaterializer()

  implicit val awsSqsClient: SqsAsyncClient = SqsAsyncClient
    .builder()
    .region(Region.AP_NORTHEAST_1)
    .httpClient(AkkaHttpClient.builder().withActorSystem(system).build())
    .build()

  system.registerOnTermination(awsSqsClient.close())

  val queueName = (i: Int) => {
    val queueNames = Map(
      1 -> "HighPriority",
      2 -> "NormalPriority"
    )
    queueNames(i)
  }

  val runnable = {
    Source(1 to 2)
      .groupBy(2, _ % 2)
      .map(queueName)
      .via(SqsSourceFlow())
      .mergeSubstreams.log("merge").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .via(DeleteFlow)
  }

  runnable.runWith(Sink.ignore)
}
