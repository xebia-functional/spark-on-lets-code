package com.fortysevendeg.sparkon.common.config

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

trait BaseConfig {

  val config = ConfigFactory.load()
}

trait CommonConfig extends BaseConfig {

  val sparkOnConfig = config.getConfig("spark-on")

  val sparkOnFilters = sparkOnConfig.getStringList("filters").asScala.toSet
  val windowSizeSeconds = sparkOnConfig.getInt("windowSizeSeconds")
  val cassandraCQLPath = sparkOnConfig.getString("cassandraCQLPath")
  val sparkOnJars = sparkOnConfig.getStringList("spark.jars").asScala.toSeq
  val dateFormat = sparkOnConfig.getString("dateFormat")
  val dateFormatSplitter = sparkOnConfig.getString("dateFormatSplitter")

  val twitterConfig = config.getConfig("twitter")
  val twitterCredentials = twitterConfig.getConfig("credentials")

  val consumerKey = twitterCredentials.getString("consumerKey")
  val consumerSecret = twitterCredentials.getString("consumerSecret")
  val accessToken = twitterCredentials.getString("accessToken")
  val accessTokenSecret = twitterCredentials.getString("accessTokenSecret")
}

trait SparkConfig extends BaseConfig {

  val sparkMaster = getStringFromEnvOrConfig("spark.master")
  val sparkAppName = config.getString("spark.appName")
  val sparkHome = config.getString("spark.home")
  val sparkCheckpoint = config.getString("spark.checkpoint")
  val streamingBatchInterval = config.getInt("spark.streaming.batch.interval")
  val sparkExecutorMemory = config.getBytes("spark.executor.memory")
  val sparkCoresMax = getIntFromEnvOrConfig("spark.cores.max")

  val sparkAkkaHeartbeatInterval = getIntFromEnvOrConfig("spark.akka.heartbeat.interval")

  val cassandraHosts = sys.props.get("spark.cassandra.connection.host") getOrElse {
    val hostList = config.getStringList("spark.cassandra.connection.host").asScala
    hostList.mkString(",")
  }
  val sparkCassandraKeyspace: String = config.getString("spark.cassandra.keyspace")

  private def getStringFromEnvOrConfig(configKey: String) =
    sys.props.get(configKey) getOrElse config.getString(configKey)

  private def getIntFromEnvOrConfig(configKey: String) =
    sys.props.get(configKey) map (_.toInt) getOrElse config.getInt(configKey)
}

trait HttpConfig extends BaseConfig {

  val httpConfig = config.getConfig("http")
  val interface = httpConfig.getString("interface")
  val port = httpConfig.getInt("port")
}

object ConfigRegistry extends CommonConfig with SparkConfig with HttpConfig