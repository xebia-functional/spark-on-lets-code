package com.fortysevendeg.sparkon.services.twitter

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKitBase}
import com.fortysevendeg.sparkon.common.BaseServiceTest
import org.specs2.mock.Mockito
import scala.reflect.ClassTag
import twitter4j.{Status, StatusListener, TwitterStream}
import twitter4j.auth.Authorization

class TwitterReceiverActorStreamSpec
  extends BaseServiceTest
  with TestKitBase
  with ImplicitSender
  with Mockito {

  val twitterStreamMock = mock[TwitterStream]

  class TwitterReceiverActorStreamStub[T: ClassTag](
    twitterAuth: Authorization, filters: Seq[String])
    extends TwitterReceiverActorStream[T](twitterAuth, filters) {
     override val twitterStream = twitterStreamMock
     override val listener = mock[StatusListener]
   }

  implicit lazy val system = ActorSystem()

  "TwitterReceiverActorStream Actor" should {
    "process all the actor streaming messages" in {

      val twitterAuth = mock[Authorization]
      val filters = Seq("scala", "play", "akka", "spark", "47")
      val status = mock[Status]
      val actorRef = TestActorRef(
        new TwitterReceiverActorStreamStub[Status](twitterAuth, filters))

      actorRef ! status      

      there was one(status).asInstanceOf[Status]
    }
  }
}