package jp.pigumer.example

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClient}
import com.amazonaws.services.sqs.model.{GetQueueUrlRequest, SendMessageRequest, SendMessageResult}
import spray.json._

trait SQS {
  val QueueName: String

  val format = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX")
  val currentTimestamp = {
    format.format(ZonedDateTime.now(ZoneId.of("UTC")))
  }
  def publish(json: JsObject)(implicit client: AmazonSQS): SendMessageResult = {
    val getQueueUrlRequest = new GetQueueUrlRequest().withQueueName(QueueName)
    val res = client.getQueueUrl(getQueueUrlRequest)
    val sendMessageRequest = new SendMessageRequest()
      .withQueueUrl(res.getQueueUrl)
      .withMessageBody {
        JsObject(
          "Timestamp" -> JsString(currentTimestamp),
          "Body" -> json
        ).compactPrint
      }
    client.sendMessage(sendMessageRequest)
  }
}
