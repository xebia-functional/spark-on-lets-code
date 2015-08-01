import sbt.Keys._
import sbt._

trait Dependencies {
  this: Build =>

  val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % V.akkaStreams
  val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core-experimental" % V.akkaStreams
  val akkaHttpJson = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % V.akkaStreams
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit-experimental" % V.akkaStreams
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % V.akka
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % V.akka
  val akkaStreams = "com.typesafe.akka" %% "akka-stream-experimental" % V.akkaStreams
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % V.akka
  val cassandraSpark = "com.datastax.spark" %% "spark-cassandra-connector" % V.cassandraSpark
  val sparkCore = "org.apache.spark" %% "spark-core" % V.spark
  val sparkStreaming = "org.apache.spark" %% "spark-streaming" % V.spark
  val specs2Core = "org.specs2" %% "specs2-core" % V.specs2
  val specs2Mock = "org.specs2" %% "specs2-mock" % V.specs2
  val sprayJson = "io.spray" %% "spray-json" % V.sprayJson
  val twitter4jCore = "org.twitter4j" % "twitter4j-core" % V.twitter4j
  val twitter4jStream = "org.twitter4j" % "twitter4j-stream" % V.twitter4j

  val dependencies = Seq(libraryDependencies ++= Seq(
    akkaHttp,
    akkaHttpCore,
    akkaHttpJson,
    akkaRemote,
    akkaSlf4j,
    akkaStreams,
    cassandraSpark exclude("org.apache.spark", "*"),
    sparkCore exclude("org.spark-project.akka", "*"),
    sprayJson,
    sparkStreaming intransitive(),
    twitter4jCore,
    twitter4jStream,
    akkaHttpTestkit % "test",
    akkaTestkit % "test",
    specs2Core % "test", 
    specs2Mock % "test"))
}