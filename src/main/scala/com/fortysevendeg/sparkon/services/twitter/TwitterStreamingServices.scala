package com.fortysevendeg.sparkon.services.twitter

import com.datastax.spark.connector.SomeColumns
import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.streaming._
import com.fortysevendeg.sparkon.common.config.ConfigRegistry._
import com.fortysevendeg.sparkon.persistence.schema.CassandraServices
import com.fortysevendeg.sparkon.services.twitter.domain.Conversions._
import com.fortysevendeg.sparkon.services.twitter.domain.TwitterAuth
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.slf4j.LoggerFactory
import twitter4j.auth.Authorization
import twitter4j.{Status, TwitterStream, TwitterStreamFactory}

import scala.language.postfixOps

trait TwitterStreamingServices extends Serializable {

  val logger = LoggerFactory.getLogger(this.getClass)

  def createCassandraSchema(implicit connector: CassandraConnector) =
    CassandraServices.createSchema(sparkCassandraKeyspace)

  def createTwitterStream(filters: Seq[String] = Seq.empty,
      storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER)
      (implicit ssc: StreamingContext,
          twitterAuth: TwitterAuth): TwitterInputDStream =
    new TwitterInputDStream(ssc = ssc,
      twitterAuth = twitterAuth,
      filters = filters,
      storageLevel = storageLevel)

  def getTwitterStream(twitterAuth: Authorization, receiver: TwitterReceiver): TwitterStream = {
    val newTwitterStream = new TwitterStreamFactory().getInstance(twitterAuth)
    newTwitterStream.addListener(new TwitterStatusListener(receiver))
    newTwitterStream
  }

  def ingestTweets(topics: Set[String], windowSize: Duration)
      (implicit ssc: StreamingContext, dsStream: DStream[Status]) = {

    // dsStream -> streaming_tweets_by_day
    tweetsByDay(dsStream)

    // dsStream -> streaming_tweets_by_track
    tweetsByTrack(dsStream = dsStream, topics = topics, windowSize = windowSize)

    ssc.checkpoint(sparkCheckpoint)
    ssc.start()
  }

  def tweetsByDay(dsStream: DStream[Status]) {
    dsStream
        .map(toTweetsByDay)
        .saveToCassandra(
          sparkCassandraKeyspace,
          "streaming_tweets_by_day",
          SomeColumns(
            "id",
            "user_id",
            "user_name",
            "user_screen_name",
            "created_timestamp",
            "created_day",
            "tweet_text",
            "lang",
            "retweet_count",
            "favorite_count",
            "latitude",
            "longitude"))
  }

  def tweetsByTrack(dsStream: DStream[Status], topics: Set[String], windowSize: Duration) {
    dsStream
        .flatMap(_.getText.toLowerCase.split( """\s+"""))
        .filter(topics.contains)
        .countByValueAndWindow(windowSize, windowSize)
        .transform {
          (rdd, time) =>
            val dateParts = formatTime(time, dateFormat).split(dateFormatSplitter) map (_.toInt)
            rdd map { case (track, count) =>
              toTweetsByTrack(dateParts, track, count)
            }
        }
        .saveToCassandra(
          sparkCassandraKeyspace,
          "streaming_tweets_by_track",
          SomeColumns(
            "track",
            "year",
            "month",
            "day",
            "hour",
            "minute",
            "count"))
  }
}

object TwitterStreamingServices extends TwitterStreamingServices