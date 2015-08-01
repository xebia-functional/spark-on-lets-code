package com.fortysevendeg.sparkon.services.twitter.domain

import com.fortysevendeg.sparkon.services.twitter.TwitterReceiver
import twitter4j.TwitterStream
import twitter4j.auth.Authorization

// States
sealed trait State

case object Stopped extends State

case object Streaming extends State

//Data
sealed trait Data

case object NoStreamingData extends Data

final case class StreamingData(stream: TwitterStream) extends Data

//Events
final case class StartStreaming(receiver: TwitterReceiver, twitterAuth: Authorization, filters: Seq[String])

case object StopStreaming