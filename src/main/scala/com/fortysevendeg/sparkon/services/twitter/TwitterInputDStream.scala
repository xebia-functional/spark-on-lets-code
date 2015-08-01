package com.fortysevendeg.sparkon.services.twitter

import com.fortysevendeg.sparkon.services.twitter.domain.TwitterAuth
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver
import twitter4j._
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

class TwitterInputDStream(
    ssc: StreamingContext,
    twitterAuth: TwitterAuth,
    filters: Seq[String],
    storageLevel: StorageLevel
    ) extends ReceiverInputDStream[Status](ssc) {

  private[this] val authorization = new OAuthAuthorization(new ConfigurationBuilder()
      .setOAuthConsumerKey(twitterAuth.consumerKey)
      .setOAuthConsumerSecret(twitterAuth.consumerSecret)
      .setOAuthAccessToken(twitterAuth.accessToken)
      .setOAuthAccessTokenSecret(twitterAuth.accessTokenSecret)
      .build())

  override def getReceiver(): Receiver[Status] = new TwitterReceiver(
    twitterAuth = authorization,
    filters = filters,
    storageLevel = storageLevel)
}