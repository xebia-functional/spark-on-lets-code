package com.fortysevendeg.sparkon.services.twitter

import akka.actor.FSM
import com.fortysevendeg.sparkon.services.twitter.domain._
import org.slf4j.LoggerFactory
import twitter4j._

class SparkTwitterFSMActor(twitterStreamingServices: TwitterStreamingServices)
    extends FSM[State, Data] {

  val logger = LoggerFactory.getLogger(this.getClass)

  startWith(Stopped, NoStreamingData)

  when(Stopped) {
    case Event(StopStreaming, _) =>
      stay using NoStreamingData
    case Event(ss: StartStreaming, _) =>
      val newTwitterStream: TwitterStream =
        twitterStreamingServices.getTwitterStream(ss.twitterAuth, ss.receiver)

      ss.filters match {
        case Nil => newTwitterStream.sample()
        case _ =>
          val query = new FilterQuery
          query.track(ss.filters.toArray)
          newTwitterStream.filter(query)
      }

      goto(Streaming) using StreamingData(stream = newTwitterStream)
    case _ =>
      logger.warn("Case not expected in 'Stopped' State")
      stay()
  }

  when(Streaming) {
    case Event(ss: StartStreaming, sd: StreamingData) =>
      stay using sd
    case Event(StopStreaming, sd: StreamingData) =>
      sd.stream.shutdown()
      goto(Stopped) using NoStreamingData
    case _ =>
      logger.warn("Case not expected in 'Streaming' State")
      stay()
  }
}

class TwitterStatusListener(receiver: TwitterReceiver) extends StatusListener {
  def onStatus(status: Status): Unit = receiver.store(status)
  def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
  def onTrackLimitationNotice(i: Int) {}
  def onScrubGeo(l: Long, l1: Long) {}
  def onStallWarning(stallWarning: StallWarning) {}
  def onException(e: Exception) {
    receiver.restart("Unexpected error receiving tweets", e)
  }
}