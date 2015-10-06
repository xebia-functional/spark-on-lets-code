package com.fortysevendeg.sparkon.services.twitter

import com.fortysevendeg.sparkon.common.config.ConfigRegistry._
import com.fortysevendeg.sparkon.services.twitter.domain._
import org.slf4j.LoggerFactory
import scala.language.postfixOps
import scala.util._
import twitter4j.{Twitter, TwitterFactory}
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

trait TwitterServices extends Serializable {

  val logger = LoggerFactory.getLogger(this.getClass)
  val woeid = 1 //Worldwide
  lazy val twitterClient: Twitter = 
    new TwitterFactory().getInstance(buildAuthorization)

  def getTrendingTopics = {
    val trends = Try(twitterClient
        .trends()
        .getPlaceTrends(woeid)
        .getTrends
        .map(_.getName)
        .toSet)

    trends match {
      case Success(trendSet) => 
        logger.info(s"Current Trending Topics => ${trendSet.mkString(", ")}")
        trendSet
      case Failure(e) => throw TwitterServiceException(e.getMessage(), e)
    }
  }

  def buildAuthorization =
    new OAuthAuthorization(new ConfigurationBuilder()
        .setOAuthConsumerKey(twitterAuth.consumerKey)
        .setOAuthConsumerSecret(twitterAuth.consumerSecret)
        .setOAuthAccessToken(twitterAuth.accessToken)
        .setOAuthAccessTokenSecret(twitterAuth.accessTokenSecret)
        .build())
}

object TwitterServices extends TwitterServices
