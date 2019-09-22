package jp.pigumer.example

import java.io.{IOException, InputStream, OutputStream}
import java.nio.charset.StandardCharsets

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClient, AmazonSQSClientBuilder}
import org.apache.logging.log4j.LogManager
import spray.json._

class Handler extends SQS {

  private val Logger = LogManager.getLogger(classOf[Handler])

  val QueueName = sys.env("QUEUE")
  implicit val client: AmazonSQS =
    AmazonSQSClientBuilder.standard.withRegion("ap-northeast-1").build()

  @throws[IOException]
  def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val request = JsonParser(
      new String(Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray, StandardCharsets.UTF_8)
    )
    val res = publish(request.asJsObject)
    Logger.info(s"${request.compactPrint} -> $res")
  }
}
