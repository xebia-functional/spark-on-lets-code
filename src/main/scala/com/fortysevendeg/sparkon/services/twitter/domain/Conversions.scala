package com.fortysevendeg.sparkon.services.twitter.domain

import com.fortysevendeg.sparkon.persistence.schema.domain.{TweetsByDay, TweetsByTrack}
import org.apache.spark.streaming.Time
import org.joda.time.{DateTime, DateTimeZone}
import twitter4j.Status

object Conversions {

  def toTweetsByDay(statusRDD: Status): TweetsByDay = {
    val user = statusRDD.getUser
    val geoLocation = Option(statusRDD.getGeoLocation)
    TweetsByDay(
      id = statusRDD.getId.toString,
      userId = user.getId,
      userName = user.getName,
      userScreenName = user.getScreenName,
      createdTimestamp = formatMillis(user.getCreatedAt.getTime),
      createdDay = formatMillis(user.getCreatedAt.getTime, "yyyyMMdd"),
      tweetText = statusRDD.getText,
      lang = statusRDD.getLang,
      retweetCount = statusRDD.getRetweetCount,
      favoriteCount = statusRDD.getFavoriteCount,
      latitude = geoLocation map (_.getLatitude),
      longitude = geoLocation map (_.getLongitude))
  }

  def toTweetsByTrack(dateParts: Array[Int], track: String, count: Long): TweetsByTrack = {
    TweetsByTrack(
      track = track,
      year = dateParts(0),
      month = dateParts(1),
      day = dateParts(2),
      hour = dateParts(3),
      minute = dateParts(4),
      count = count)
  }

  def formatTime(time: Time, format: String = "yyyyMMddHH:mm:ss.SSS"): String =
    formatMillis(time.milliseconds, format)

  def formatMillis(millis: Long, format: String = "yyyyMMddHH:mm:ss.SSS"): String =
    new DateTime(millis, DateTimeZone.UTC).toString(format)
}