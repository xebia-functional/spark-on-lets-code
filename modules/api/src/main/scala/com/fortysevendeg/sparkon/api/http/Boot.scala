package com.fortysevendeg.sparkon.api.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.common.config.ConfigRegistry._
import com.fortysevendeg.sparkon.services.twitter.TwitterStreamingServices
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object Boot extends App with ApiHttpService {

  val sparkConf = new SparkConf()
    .setMaster(sparkMaster)
    .setAppName(sparkAppName)
    .setSparkHome(sparkHome)
    .setJars(sparkOnJars)
    .set("spark.executor.memory", sparkExecutorMemory.toString)
    .set("spark.cores.max", sparkCoresMax.toString)
    .set("spark.cassandra.connection.host", cassandraHosts)
    .set("spark.akka.heartbeat.interval", sparkAkkaHeartbeatInterval.toString)
    .set("spark.serializer", sparkSerializer)
    .set("spark.broadcast.factory", "org.apache.spark.broadcast.HttpBroadcastFactory")
    .set("spark.executorEnv.kafkaBootstrapServers", bootstrapServers)
    .set("spark.executorEnv.kafkaProducerKeySerializer", kafkaProducerKeySerializer)
    .set("spark.executorEnv.kafkaProducerValueSerializer", kafkaProducerValueSerializer)
    .set("spark.streaming.backpressure.enabled", "true")

  override implicit val system = ActorSystem("ReactiveSparkOn")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()
  override implicit val sparkContext = createSparkContext
  override implicit val ssc: StreamingContext = createStreamingContext(sparkContext)
  override implicit val cassandraConnector: CassandraConnector = CassandraConnector(sparkConf)
  override implicit val twitterStreamingServices = new TwitterStreamingServices {}

  Http().bindAndHandle(routes, interface, port)
  logger.info(s"Server started at http://$interface:$port")

  def createSparkContext: SparkContext = new SparkContext(sparkConf)

  def createStreamingContext(sparkContext: SparkContext): StreamingContext =
    new StreamingContext(sparkContext = sparkContext, batchDuration = Seconds(streamingBatchInterval))
}
