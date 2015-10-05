import sbt.Keys._
import sbt._

trait Dependencies extends Excludes {
  this: Build =>

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % V.akka
  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % V.akkaStreams
  val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core-experimental" % V.akkaStreams
  val akkaHttpJson = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % V.akkaStreams
  val akkaHttpXml = "com.typesafe.akka" %% "akka-http-xml-experimental" % V.akkaStreams
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit-experimental" % V.akkaStreams
  val akkaParsing = "com.typesafe.akka" %% "akka-parsing-experimental" % V.akkaStreams
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % V.akka
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % V.akka
  val akkaStreams = "com.typesafe.akka" %% "akka-stream-experimental" % V.akkaStreams
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % V.akka
  val cassandraSpark = "com.datastax.spark" %% "spark-cassandra-connector" % V.cassandraSpark
  val config = "com.typesafe" % "config" % V.config
  val commonsCodec = "commons-codec" % "commons-codec" % V.commonsCodec
  val hadoopClient = "org.apache.hadoop" % "hadoop-client" % V.hadoopClient
  val jodaTime = "joda-time" % "joda-time" % V.jodaTime
  val jodaConvert = "org.joda" % "joda-convert" % V.jodaConvert
  val kafka = "org.apache.kafka" %% "kafka" % V.kafka
  val logback = "ch.qos.logback" % "logback-classic" % V.logback
  val phantomDsl = "com.websudos" %% "phantom-dsl" % V.phantom
  val phantomTestkit = "com.websudos" %% "phantom-testkit" % V.phantom
  val reactiveKafka = "com.softwaremill.reactivekafka" %% "reactive-kafka-core" % V.reactiveKafka
  val sparkCore = "org.apache.spark" %% "spark-core" % V.spark
  val sparkStreaming = "org.apache.spark" %% "spark-streaming" % V.spark
  val sparkStreamingKafka = "org.apache.spark" %% "spark-streaming-kafka" % V.spark
  val specs2Core = "org.specs2" %% "specs2-core" % V.specs2
  val specs2Mock = "org.specs2" %% "specs2-mock" % V.specs2
  val sprayHttp = "io.spray" %% "spray-http" % V.spray
  val sprayHttpx = "io.spray" %% "spray-httpx" % V.spray
  val sprayUtil = "io.spray" %% "spray-util" % V.spray
  val sprayClient = "io.spray" %% "spray-client" % V.spray
  val sprayCan = "io.spray" %% "spray-can" % V.spray
  val sprayCaching = "io.spray" %% "spray-caching" % V.spray
  val sprayRouting = "io.spray" %% "spray-routing" % V.spray
  val sprayJson = "io.spray" %% "spray-json" % V.sprayJson
  val sprayTestKit = "io.spray" %% "spray-testkit" % V.sprayJson
  val twitter4jCore = "org.twitter4j" % "twitter4j-core" % V.twitter4j
  val twitter4jStream = "org.twitter4j" % "twitter4j-stream" % V.twitter4j

  val baseDepts = Seq(specs2Core % "test", specs2Mock % "test")

  val commonDeps = Seq(libraryDependencies ++= Seq(config, logback))

  val persistenceDeps = Seq(libraryDependencies ++= Seq(
    akkaRemote,
    akkaSlf4j,
    cassandraSpark exclude("org.apache.spark", "*"),
    phantomDsl,
    phantomTestkit,
    sparkCore exclude("org.spark-project.akka", "*")))

  val servicesDeps = Seq(libraryDependencies ++= Seq(
    kafka exclusionsForKafka,
    sparkStreaming intransitive(),
    sparkStreamingKafka intransitive(),
    twitter4jCore,
    twitter4jStream,
    akkaTestkit % "test"))

  val testDeps = Seq(libraryDependencies ++= baseDepts ++ Seq(
    twitter4jCore,
    akkaHttpTestkit % "test"))

  val apiDeps = Seq(libraryDependencies ++= Seq(
    akkaHttp,
    akkaHttpCore,
    akkaHttpJson,
    akkaStreams,
    hadoopClient,
    reactiveKafka,
    sprayJson))
}
