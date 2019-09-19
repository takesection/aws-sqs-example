package jp.pigumer.example

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, Attributes, Materializer}

import scala.concurrent.Future

object Sandbox extends App {

  implicit val system: ActorSystem = ActorSystem("Sandbox")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val Max = 3
  val s = Source(1 to Max)
  val subSource = { i: Int =>
    Source(1 to i * 10).log("sub").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  }
  val flow = Flow[Int]
    .map(i => i)
    .log("flow")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))

  val f: Future[Done] = s
    .groupBy(Max, _ % Max)
    .flatMapConcat { i =>
      subSource(i)
    }
    .via(flow).async
    .mergeSubstreams
    .runWith(Sink.ignore)

  Thread.sleep(10000)
  f.onComplete(_ => system.terminate())
}
