package com.fortysevendeg.sparkon.persistence.schema.domain

case class TweetsByDay(id: String,
    userId: Long, userName: String,
    userScreenName: String,
    createdTimestamp: String,
    createdDay: String,
    tweetText: String,
    lang: String,
    retweetCount: Int,
    favoriteCount: Int,
    latitude: Option[Double],
    longitude: Option[Double])

case class TweetsByTrack(
    track: String,
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    count: Long)