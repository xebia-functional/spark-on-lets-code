package com.fortysevendeg.sparkon.api.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.common.config.ConfigRegistry._
import com.fortysevendeg.sparkon.services.twitter.domain.TwitterAuth
import com.fortysevendeg.sparkon.services.twitter.{TwitterInputDStream, TwitterStreamingServices}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Boot extends App with ApiHttpService with BootHelper {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()(system)
  override implicit val ssc: StreamingContext = createStreamingContext
  override implicit val cassandraConnector: CassandraConnector = CassandraConnector(sparkConf)
  override implicit val twitterAuth: TwitterAuth = createTwitterAuth
  override implicit val twitterStreaming: TwitterInputDStream = TwitterStreamingServices.createTwitterStream()

  Http().bindAndHandle(routes, interface, port)
}

trait BootHelper {

  val sparkConf = new SparkConf()
      .setMaster(sparkMaster)
      .setAppName(sparkAppName)
      .setSparkHome(sparkHome)
      .setJars(sparkOnJars)
      .set("spark.executor.memory", sparkExecutorMemory.toString)
      .set("spark.cores.max", sparkCoresMax.toString)
      .set("spark.cassandra.connection.host", cassandraHosts)
      .set("spark.akka.heartbeat.interval", sparkAkkaHeartbeatInterval.toString)
      .set("spark.broadcast.factory", "org.apache.spark.broadcast.HttpBroadcastFactory")

  def createStreamingContext: StreamingContext =
    new StreamingContext(conf = sparkConf, batchDuration = Seconds(streamingBatchInterval))

  def createTwitterAuth: TwitterAuth =
    TwitterAuth(consumerKey = consumerKey,
      consumerSecret = consumerSecret,
      accessToken = accessToken,
      accessTokenSecret = accessTokenSecret)
}