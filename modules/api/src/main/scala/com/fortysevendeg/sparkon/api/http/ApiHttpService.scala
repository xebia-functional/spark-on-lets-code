package com.fortysevendeg.sparkon.api.http

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.common.config.ConfigRegistry._
import com.fortysevendeg.sparkon.services.twitter._
import com.softwaremill.react.kafka.KafkaMessages._
import com.softwaremill.react.kafka.{ConsumerProperties, ReactiveKafka}
import kafka.serializer.StringDecoder
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor

case class Info(message: String)

case class ApiStreamingRequest(recreateDatabaseSchema: Boolean, filters: Seq[String])

trait ApiHttpService extends Protocols {

  val logger = LoggerFactory.getLogger(this.getClass)

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  implicit val sparkContext: SparkContext
  implicit val ssc: StreamingContext
  implicit val cassandraConnector: CassandraConnector
  implicit val twitterStreamingServices: TwitterStreamingServices

  val routes = {
    logRequestResult("web-socket-services") {
      pathPrefix("trending-topics") {
        get {
          handleWebsocketMessages(handler = kafkaServiceFlow)
        }
      }
    } ~ {
      logRequestResult("twitter-streaming-services") {
        pathPrefix("twitter-streaming") {
          get {
            complete {
              Info(message = ssc.getState() match {
                case StreamingContextState.INITIALIZED => "The streaming has been created, but not been started yet"
                case StreamingContextState.ACTIVE => "The streaming has been started and running"
                case StreamingContextState.STOPPED => "The streaming has been stopped"
              })
            }
          } ~
            post {
              implicit val apiStreamingRequestFormat = jsonFormat2(ApiStreamingRequest)
              entity(as[ApiStreamingRequest]) { request =>
                complete {
                  ssc.getState() match {
                    case StreamingContextState.INITIALIZED =>
                      if (request.recreateDatabaseSchema) {
                        twitterStreamingServices.createCassandraSchema
                      }
                      val filters = TwitterServices.getTrendingTopics ++ request.filters

                      logger.info(s"Streaming Filters [${filters.mkString(",\n")}]")

                      implicit val dsStream = twitterStreamingServices.createTwitterStream()
                      twitterStreamingServices.ingestTweets(topics = filters,
                        windowSize = Seconds(windowSizeSeconds),
                        slideDuration = Seconds(slideDuration))
                      Info(message = "Started")
                    case StreamingContextState.ACTIVE =>
                      BadRequest -> "The streaming has already started"
                    case StreamingContextState.STOPPED =>
                      BadRequest -> "The streaming has already stopped"
                  }
                }
              }
            } ~
            delete {
              complete {
                ssc.getState() match {
                  case StreamingContextState.INITIALIZED =>
                    Info(message = "The streaming has been created, but not been started yet")
                  case StreamingContextState.ACTIVE =>
                    ssc.stop(stopSparkContext = false, stopGracefully = true)
                    ssc.awaitTermination()
                    Info(message = "The streaming has been stopped")
                  case StreamingContextState.STOPPED =>
                    BadRequest -> "The streaming has already stopped"
                }
              }
            }
        }
      }
    }
  }

  def kafkaServiceFlow: Flow[Message, Message, _] = {

    val kafka = new ReactiveKafka()
    val publisher: Publisher[StringKafkaMessage] =
      kafka.consume(
        ConsumerProperties(
          brokerList = bootstrapServers,
          zooKeeperHost = s"$zookeeperHost:$zookeeperPort",
          topic = kafkaTopicRaw,
          groupId = kafkaGroupId,
          decoder = new StringDecoder()
        )
      )

    Flow.wrap(Sink.ignore, Source(publisher) map toMessage)(Keep.none)
  }

  def toMessage(t: KafkaMessage[String]) = TextMessage("Received: " + t.message)
}
