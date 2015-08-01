package com.fortysevendeg.sparkon.services.twitter

import com.fortysevendeg.sparkon.services.twitter.domain.TwitterAuth
import com.fortysevendeg.sparkon.services.util.BaseServiceTest
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

class TwitterStreamingServicesSpec extends BaseServiceTest {

  val batchDuration = Seconds(1)

  private val master: String = "local[4]"

  private val framework: String = this.getClass.getSimpleName

  "TwitterStreamingServices API Testing" should {
    "works for each combination" in {

      implicit val ssc = new StreamingContext(master = master, appName = framework, batchDuration = batchDuration)
      implicit val twitterAuth = TwitterAuth(consumerKey = "", consumerSecret = "", accessToken = "", accessTokenSecret = "")
      val filters = Seq("scala", "akka")

      TwitterStreamingServices.createTwitterStream(filters = Seq.empty)
      TwitterStreamingServices.createTwitterStream(filters = filters)
      TwitterStreamingServices.createTwitterStream(filters = filters, storageLevel = StorageLevel.MEMORY_AND_DISK_SER_2)

      ssc.stop()

      "All combinations looks good" must endWith("good")
    }
  }
}