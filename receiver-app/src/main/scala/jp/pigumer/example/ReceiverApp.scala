package jp.pigumer.example

import java.util.concurrent.CompletionStage

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.alpakka.sqs.MessageAction
import akka.stream.{ActorMaterializer, Attributes, ClosedShape, FanInShape2, FlowShape, Materializer}
import akka.stream.alpakka.sqs.scaladsl.{SqsAckFlow, SqsAckSink, SqsSource}
import akka.stream.javadsl.RunnableGraph
import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source, ZipWith}
import com.github.matsluni.akkahttpspi.AkkaHttpClient
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.{GetQueueUrlRequest, Message}

trait ReceiverApp {

  implicit val system: ActorSystem = ActorSystem("App")
  implicit val mat: Materializer = ActorMaterializer()

  implicit val awsSqsClient: SqsAsyncClient = SqsAsyncClient
    .builder()
    .region(Region.AP_NORTHEAST_1)
    .httpClient(AkkaHttpClient.builder().withActorSystem(system).build())
    .build()

  def queueUrl(name: String): CompletionStage[String] = {
    val request = GetQueueUrlRequest.builder().queueName(name).build()
    awsSqsClient.getQueueUrl(request).thenApply(_.queueUrl())
  }

  val s1 = Source.fromSourceCompletionStage[(String, Message), NotUsed](
    queueUrl("HighPriority").thenApply { url =>
      SqsSource(url)
        .log("HighPriority")
        .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
        .map(message => (url, message))
    }
  ).flatMapConcat {
    case (url: String, message: Message) =>
      Source.single(message).map(MessageAction.delete(_)).via(SqsAckFlow(url)).map(_ => message)
  }

  val s2 = Source.fromSourceCompletionStage[(String, Message), NotUsed](
    queueUrl("NormalPriority").thenApply { url =>
      SqsSource(url)
        .log("NormalPriority")
        .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
        .map(message => (url, message))
    }
  ).flatMapConcat {
    case (url: String, message: Message) =>
      Source.single(message).map(MessageAction.delete(_)).via(SqsAckFlow(url)).map(_ => message)
  }
  val flow = Flow[Unit].log("flow").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  val runnable = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val zip = builder.add(ZipWith[Message, Message, Unit]((_, _) => ()))

    s1 ~> zip.in0
    s2 ~> zip.in1

    zip.out ~> flow ~> Sink.ignore
    ClosedShape
  })

  runnable.run(mat)
}
