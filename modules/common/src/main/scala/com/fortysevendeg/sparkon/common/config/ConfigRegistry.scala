package com.fortysevendeg.sparkon.common.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.language.postfixOps

case class TwitterAuth(consumerKey: String,
    consumerSecret: String,
    accessToken: String,
    accessTokenSecret: String)

object ConfigRegistry {

  val config = ConfigFactory.load()

  // APP Configuration keys:

  lazy val sparkOnConfig = config.getConfig("spark-on")

  lazy val sparkOnFilters = sparkOnConfig.getStringList("filters").asScala.toSet
  lazy val windowSizeSeconds = sparkOnConfig.getLong("windowSizeSeconds")
  lazy val slideDuration = sparkOnConfig.getLong("slideDuration")
  lazy val cassandraCQLPath = sparkOnConfig.getString("cassandraCQLPath")
  lazy val sparkOnJars = sparkOnConfig.getStringList("spark.jars").asScala.toList
  lazy val dateFormat = sparkOnConfig.getString("dateFormat")
  lazy val dateFormatSplitter = sparkOnConfig.getString("dateFormatSplitter")

  // Twitter Configuration keys:

  lazy val twitterConfig = config.getConfig("twitter")
  lazy val twitterCredentials = twitterConfig.getConfig("credentials")

  lazy val consumerKey = twitterCredentials.getString("consumerKey")
  lazy val consumerSecret = twitterCredentials.getString("consumerSecret")
  lazy val accessToken = twitterCredentials.getString("accessToken")
  lazy val accessTokenSecret = twitterCredentials.getString("accessTokenSecret")

  lazy val twitterAuth = TwitterAuth(
    consumerKey,
    consumerSecret,
    accessToken,
    accessTokenSecret)

  // Spark Configuration keys:

  lazy val sparkMasterHost = getStringFromEnvOrConfig("spark.master")
  lazy val sparkMasterPort = getStringFromEnvOrConfig("spark.port")
  lazy val sparkMaster = sparkMasterHost.contains("local") match {
    case true => sparkMasterHost
    case _ => "spark://$sparkMasterHost:$sparkMasterPort"
  }

  lazy val sparkAppName = config.getString("spark.appName")
  lazy val sparkHome = config.getString("spark.home")
  lazy val sparkCheckpoint = config.getString("spark.checkpoint")
  lazy val streamingBatchInterval = config.getLong("spark.streaming.batch.interval")
  lazy val sparkExecutorMemory = config.getBytes("spark.executor.memory")
  lazy val sparkCoresMax = getIntFromEnvOrConfig("spark.cores.max")
  lazy val sparkSerializer = getStringFromEnvOrConfig("spark.serializer")

  lazy val sparkAkkaHeartbeatInterval = getIntFromEnvOrConfig("spark.akka.heartbeat.interval")

  // Cassandra Configuration keys:

  lazy val cassandraNodesValues: List[String] = List(sys.env.get(s"CASSANDRA_SEED_PORT_9160_TCP_ADDR")) ++ {
    1 to 10 map { index =>
      sys.env.get(s"CASSANDRA_SLAVE_${index}_PORT_9160_TCP_ADDR")
    }
  } flatten

  lazy val cassandraHosts = mkStringNodes(nodes = cassandraNodesValues, 
    propKey = "spark.cassandra.connection.host", 
    cfg = config, 
    configurationKeyList = "spark.cassandra.connection.host")

  lazy val sparkCassandraKeyspace: String = config.getString("spark.cassandra.keyspace")

  // APP HTTP Configuration keys:

  lazy val httpConfig = config.getConfig("http")
  lazy val interface = httpConfig.getString("interface")
  lazy val port = httpConfig.getInt("port")

  // Kakfa Configuration keys:

  lazy val kafkaConfig = config.getConfig("kafka")

  lazy val kafkaNodesEnvVariables = 1 to 10 map { index =>
    (sys.env.get(s"KAFKA_${index}_PORT_9092_TCP_ADDR"),
        sys.env.get(s"KAFKA_${index}_PORT_9092_TCP_PORT"))
  } toList

  lazy val kafkaNodesValues: List[String] = kafkaNodesEnvVariables flatMap {
    case (Some(h), Some(p)) => Some(s"$h:$p")
    case _ => None
  }

  lazy val bootstrapServers = mkStringNodes(nodes = kafkaNodesValues,
    propKey = "kafka.hosts",
    cfg = kafkaConfig,
    configurationKeyList = "hosts")
  lazy val kafkaTopics = kafkaConfig.getString("topics").split(",").toSet

  lazy val zookeeperHost = kafkaConfig.getString("zookeeper.host")
  lazy val zookeeperPort = kafkaConfig.getInt("zookeeper.port")

  lazy val kafkaGroupId = kafkaConfig.getString("group.id")
  lazy val kafkaTopicRaw = kafkaConfig.getString("topic.raw")

  lazy val kafkaProducerKeySerializer = kafkaConfig.getString("producer.key.serializer")
  lazy val kafkaProducerValueSerializer = kafkaConfig.getString("producer.value.serializer")

  // Helper methods:

  private[config] def getStringFromEnvOrConfig(configKey: String) =
    sys.props.get(configKey) getOrElse config.getString(configKey)

  private[config] def getIntFromEnvOrConfig(configKey: String) =
    sys.props.get(configKey) map (_.toInt) getOrElse config.getInt(configKey)

  private[config] def mkStringNodes(nodes: List[String], propKey: String, cfg: Config, configurationKeyList: String): String =
    if (nodes.nonEmpty) nodes.mkString(",")
    else sys.props.get(propKey) getOrElse {
      val hostList = cfg.getStringList(configurationKeyList).asScala
      hostList.mkString(",")
    }
}
