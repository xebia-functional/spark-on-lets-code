package com.fortysevendeg.sparkon.services.twitter

import akka.actor.Actor
import org.apache.spark.streaming.receiver.ActorHelper
import twitter4j._
import twitter4j.auth.Authorization

import scala.reflect.ClassTag

class TwitterReceiverActorStream[T: ClassTag](
  twitterAuth: Authorization,
  filters: List[String]
) extends Actor with ActorHelper {

  val twitterStream = new TwitterStreamFactory().getInstance(twitterAuth)
  val listener = new StatusListener() {

    def onStatus(status: Status) = self ! status
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
    def onTrackLimitationNotice(i: Int) = {}
    def onScrubGeo(l: Long, l1: Long) = {}
    def onStallWarning(stallWarning: StallWarning) = {}
    def onException(e: Exception) = e.printStackTrace()
  }

  override def preStart(): Unit = {
    twitterStream.addListener(listener)
    filters match {
      case Nil => twitterStream.sample()
      case _ =>
        val query = new FilterQuery
        query.track(filters.toArray)
        twitterStream.filter(query)
    }
  }

  def receive = {
    case data => store(data.asInstanceOf[T])
  }
}
