package com.fortysevendeg.sparkon.persistence

import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.common.BaseServiceTest
import com.fortysevendeg.sparkon.persistence.schema.CassandraServices
import com.fortysevendeg.sparkon.persistence.schema.domain.PersistenceException
import org.specs2.mock.Mockito

class CassandraServicesSpec extends BaseServiceTest with Mockito {
  sequential

  "CassandraServices" should {
    "create the cassandra schema given a valid CQL Path" in {
      val cassandraServices: CassandraServices = new CassandraServices {}
      implicit val connector = mock[CassandraConnector]

      cassandraServices.createSchema(
        keyspace = "spark_on",
        cassandraCQLPath= "/data/spark_on_spark.cql")

      there was one(connector).withSessionDo(_ => "")
    }

    "throw an exception when the cassandra cql script is not valid" in {
      val cassandraServices: CassandraServices = new CassandraServices {}
      implicit val connector = mock[CassandraConnector]

      cassandraServices.createSchema(
        keyspace = "spark_on",
        cassandraCQLPath= "/wrong/path/to.cql") must throwA[PersistenceException]
    }
  }
}
