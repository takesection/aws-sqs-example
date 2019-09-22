package jp.pigumer.sqs

import java.util.concurrent.CompletionStage

import akka.NotUsed
import akka.stream.alpakka.sqs.{MessageAction, SqsSourceSettings}
import akka.stream.alpakka.sqs.scaladsl.{SqsAckFlow, SqsSource}
import akka.stream.scaladsl.{Flow, Source}
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.{GetQueueUrlRequest, Message}

trait AwsSqs {

  def queueUrl(queueName: String)(implicit client: SqsAsyncClient): CompletionStage[String] = {
    val request = GetQueueUrlRequest.builder().queueName(queueName).build()
    client.getQueueUrl(request).thenApply(_.queueUrl())
  }

  def source(queueUrl: String, settings: SqsSourceSettings)(implicit client: SqsAsyncClient): Source[(String, Message), NotUsed] =
    SqsSource(queueUrl, settings).map(message => (queueUrl, message))

  def SqsSourceFlow(settings: SqsSourceSettings = SqsSourceSettings.Defaults)(implicit client: SqsAsyncClient): Flow[String, (String, Message), NotUsed] =
    Flow[String].flatMapConcat { queueName =>
      Source.fromSourceCompletionStage(queueUrl(queueName).thenApply(url => source(url, settings)))
    }

  def DeleteFlow(implicit client: SqsAsyncClient): Flow[(String, Message), (String, Message), NotUsed] =
    Flow[(String, Message)].flatMapConcat {
      case (url: String, message: Message) =>
        Source
          .single(message)
          .map(MessageAction.Delete(_))
          .via(SqsAckFlow(url))
          .map(_ => (url, message))
    }
}

