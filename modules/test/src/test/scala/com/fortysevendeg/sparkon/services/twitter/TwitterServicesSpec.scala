package com.fortysevendeg.sparkon.services.twitter

import com.fortysevendeg.sparkon.common.BaseServiceTest
import com.fortysevendeg.sparkon.common.config.ConfigRegistry
import com.fortysevendeg.sparkon.services.twitter.domain.TwitterServiceException
import org.specs2.mock.Mockito
import twitter4j._
import twitter4j.api.TrendsResources

class TwitterServicesSpec
  extends BaseServiceTest
  with Mockito {

  trait TwitterServicesStub extends TwitterServices {
    override lazy val twitterClient = mock[Twitter]
  }

  "Twitter Services" should {
    "build a twitter4j client to fetch the current trending topics " in {
      val twitterServices = new TwitterServicesStub {}

      val filters = Seq("scala", "akka")

      val trends = mock[TrendsResources]
      val placeTrends = mock[Trends]
      val mockTrend1: Trend = mock[Trend]
      val mockTrend2: Trend = mock[Trend]
      val mockTrend3: Trend = mock[Trend]

      val trendsArray = Array(mockTrend1, mockTrend2, mockTrend3)

      mockTrend1.getName returns "scala"
      mockTrend2.getName returns "play"
      mockTrend3.getName returns "spark"

      twitterServices.twitterClient.trends() returns trends
      trends.getPlaceTrends(anyInt) returns placeTrends
      placeTrends.getTrends returns trendsArray

      val result = twitterServices.getTrendingTopics

      result.size must_== 3
    }

    "return a new custom exception when twitter4j.TwitterException is thrown" in {
      val twitterServices = new TwitterServicesStub {}

      val filters = Seq("scala", "akka")

      twitterServices.twitterClient.trends() throws new RuntimeException("something wrong")

      twitterServices.getTrendingTopics must throwA[TwitterServiceException]
    }
  }
}
