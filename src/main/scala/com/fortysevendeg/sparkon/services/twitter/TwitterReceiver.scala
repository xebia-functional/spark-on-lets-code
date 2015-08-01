package com.fortysevendeg.sparkon.services.twitter

import akka.actor.{PoisonPill, Props}
import com.fortysevendeg.sparkon.services.twitter.domain.{StartStreaming, StopStreaming}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.{Logging, SparkEnv}
import SparkEnv.{get => getEnv}
import twitter4j._
import twitter4j.auth.Authorization

class TwitterReceiver(twitterAuth: Authorization,
    filters: Seq[String],
    storageLevel: StorageLevel)
    extends Receiver[Status](storageLevel)
    with Logging {

  protected lazy val receiverActor = {
    val twitterStreamingServices = new TwitterStreamingServices {}
    getEnv
        .actorSystem
        .actorOf(Props(new SparkTwitterFSMActor(twitterStreamingServices)),
          "SparkTwitterFSMActor")
  }

  def onStart() {
    receiverActor
    receiverActor ! StartStreaming(receiver = this, twitterAuth = twitterAuth, filters = filters)
  }

  def onStop() {
    receiverActor ! StopStreaming
    receiverActor ! PoisonPill
  }
}