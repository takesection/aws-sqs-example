package jp.pigumer.example

import java.util.concurrent.CompletionStage

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.scaladsl.SqsSource
import akka.stream.scaladsl.Source
import com.github.matsluni.akkahttpspi.AkkaHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.{GetQueueUrlRequest, GetQueueUrlResponse, Message}

import scala.concurrent.Promise
import scala.concurrent.java8.FuturesConvertersImpl

trait ReceiverApp {

  implicit val system: ActorSystem = ActorSystem("App")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val awsSqsClient: SqsAsyncClient = SqsAsyncClient
    .builder()
    .region(Region.AP_NORTHEAST_1)
    .httpClient(AkkaHttpClient.builder().withActorSystem(system).build())
    .build()

  def queueUrl(name: String): CompletionStage[String] = {
    val request = GetQueueUrlRequest.builder().queueName(name).build()
    awsSqsClient.getQueueUrl(request).thenApply(_.queueUrl())
  }

  val s1 = Source.fromSourceCompletionStage(
    queueUrl("Hoge").thenApply { url =>
      SqsSource(url)
    }
  )
  val s2 = Source.fromSourceCompletionStage(
    queueUrl("Fuga").thenApply { url =>
      SqsSource(url)
    }
  )
}
