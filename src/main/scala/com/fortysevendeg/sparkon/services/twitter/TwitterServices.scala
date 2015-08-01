package com.fortysevendeg.sparkon.services.twitter

import com.fortysevendeg.sparkon.services.twitter.domain.TwitterAuth
import org.slf4j.LoggerFactory
import twitter4j.TwitterFactory
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

import scala.language.postfixOps

trait TwitterServices extends Serializable {

  val logger = LoggerFactory.getLogger(this.getClass)
  val woeid = 1 //Worldwide

  def getTrendingTopics(implicit twitterAuth: TwitterAuth) = {

    val twitter = new TwitterFactory().getInstance(buildAuthorization(twitterAuth))
    val trends = twitter
        .trends()
        .getPlaceTrends(woeid)
        .getTrends
        .map(_.getName)
        .toSet

    logger.info(s"Current Trending Topics => ${trends.mkString("\n")}")

    trends
  }

  def buildAuthorization(twitterAuth: TwitterAuth) =
    new OAuthAuthorization(new ConfigurationBuilder()
        .setOAuthConsumerKey(twitterAuth.consumerKey)
        .setOAuthConsumerSecret(twitterAuth.consumerSecret)
        .setOAuthAccessToken(twitterAuth.accessToken)
        .setOAuthAccessTokenSecret(twitterAuth.accessTokenSecret)
        .build())
}

object TwitterServices extends TwitterServices