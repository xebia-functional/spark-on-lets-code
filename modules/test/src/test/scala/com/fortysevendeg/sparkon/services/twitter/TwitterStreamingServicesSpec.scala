package com.fortysevendeg.sparkon.services.twitter

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestKitBase
import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.common.BaseServiceTest
import com.fortysevendeg.sparkon.persistence.schema.CassandraServices
import com.fortysevendeg.sparkon.persistence.schema.domain.PersistenceException
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

class TwitterStreamingServicesSpec
  extends BaseServiceTest
  with TestKitBase
  with Mockito {

  implicit lazy val system = ActorSystem()

  val batchDuration = Seconds(1)

  private val master: String = "local[4]"

  private val framework: String = this.getClass.getSimpleName

  implicit val ssc = new StreamingContext(master = master, appName = framework, batchDuration = batchDuration)

  trait CreateCassandraSchemaScope extends Scope {

    val cassandraServicesMock = mock[CassandraServices]

    val twitterStreamingServices = new TwitterStreamingServicesStub {}

    implicit val connector = mock[CassandraConnector]

    class TwitterStreamingServicesStub extends TwitterStreamingServices {
      override val cassandraServices = cassandraServicesMock
    }
  }

  trait CreateTwitterStreamScope extends Scope {
    implicit val receiverActor = mock[ActorRef]

    val twitterStreamingServices = new TwitterStreamingServices {}
  }

  "TwitterStreamingServices.createCassandraSchema" should {

    "create Cassandra works fine doing pass through to persistence " +
      "module" in new CreateCassandraSchemaScope {

      twitterStreamingServices.createCassandraSchema(connector)

      there was one(cassandraServicesMock).createSchema(any, any)(any)
    }

    "create Cassandra returns a Persistence Exception when a " +
      "new exception is thrown" in new CreateCassandraSchemaScope {

      cassandraServicesMock.createSchema(any, any)(any) throws new PersistenceException("any message")

      twitterStreamingServices.createCassandraSchema(connector) must throwA[PersistenceException]
    }
  }

  "TwitterStreamingServices.createTwitterStream" should {

    "create a new twitter streaming for an empty filters set and using " +
      "the storageLevel default value" in new CreateTwitterStreamScope {

        twitterStreamingServices.createTwitterStream(filters = Nil)

        "All combinations looks good" must endWith("good")
      }

    "create a new twitter streaming for some filters and using " +
      "the storageLevel default value" in new CreateTwitterStreamScope {
        val filters = List("scala", "akka")

        twitterStreamingServices.createTwitterStream(filters = filters)

        "All combinations looks good" must endWith("good")
      }

    "create a new twitter streaming for some filters and a " +
      "specified storage level" in new CreateTwitterStreamScope {
        val filters = List("scala", "akka")

        twitterStreamingServices.createTwitterStream(filters = filters,
          storageLevel = StorageLevel.MEMORY_AND_DISK_SER_2)

        "All combinations looks good" must endWith("good")
      }
  }
}
