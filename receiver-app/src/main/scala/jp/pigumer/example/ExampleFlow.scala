package jp.pigumer.example

import akka.NotUsed
import akka.event.Logging
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Source}
import software.amazon.awssdk.services.sqs.model.Message

trait ExampleFlow {

  val exampleFlow: Flow[(String, Message), (String, Message), NotUsed] =
    Flow[(String, Message)]
      .flatMapMerge(2, {
        case (url, message) =>
          Source
            .single(message)
            .map(_ => (url, message))
      })
}
