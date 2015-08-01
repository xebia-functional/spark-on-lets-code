package com.fortysevendeg.sparkon.services.twitter

import akka.actor.ActorSystem
import akka.testkit._
import com.fortysevendeg.sparkon.services.twitter.domain._
import com.fortysevendeg.sparkon.services.util.BaseServiceTest
import org.specs2.mock.Mockito
import twitter4j.TwitterStream
import twitter4j.auth.Authorization

class SparkTwitterFSMActorSpec
    extends BaseServiceTest
    with TestKitBase
    with Mockito {
  sequential

  implicit lazy val system = ActorSystem()

  val mockTwitterStreamingServices = mock[TwitterStreamingServices]
  val fsm = TestFSMRef(new SparkTwitterFSMActor(mockTwitterStreamingServices))
  val mustBeTypedProperly: TestActorRef[SparkTwitterFSMActor] = fsm

  "SparkTwitterFSMActor" >> {
    "state is 'Stopped' initially without any streaming data" in {

      fsm.stateName mustEqual Stopped
      fsm.stateData mustEqual NoStreamingData
    }

    "state will be 'Streaming' after a 'StartStreaming' message has been sent" in {

      val mockReceiver = mock[TwitterReceiver]
      val mockTwitterStream = mock[TwitterStream]
      val mockTwitterAuth = mock[Authorization]

      mockTwitterStreamingServices
          .getTwitterStream(mockTwitterAuth, mockReceiver) returns mockTwitterStream

      fsm ! StartStreaming(mockReceiver, mockTwitterAuth, Seq.empty)

      fsm.stateName mustEqual Streaming
    }

    "state will be 'Stopped' after a 'StopStreaming' message has been sent" in {

      fsm ! StopStreaming

      fsm.stateName mustEqual Stopped
      fsm.stateData mustEqual NoStreamingData
    }

    "state will be 'Stopped' if its current status is 'Stopped' and a 'StopStreaming' message has been sent" in {

      fsm ! StopStreaming

      fsm.stateName mustEqual Stopped
      fsm.stateData mustEqual NoStreamingData
    }

    "state will be 'Streaming' if its current status is 'Streaming' and a 'StartStreaming' message has been sent" in {

      val mockReceiver = mock[TwitterReceiver]
      val mockTwitterStream = mock[TwitterStream]
      val mockTwitterAuth = mock[Authorization]

      mockTwitterStreamingServices
          .getTwitterStream(mockTwitterAuth, mockReceiver) returns mockTwitterStream

      fsm ! StartStreaming(mockReceiver, mockTwitterAuth, Seq.empty)

      fsm.stateName mustEqual Streaming
    }
  }
}
