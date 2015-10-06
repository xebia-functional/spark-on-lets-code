package com.fortysevendeg.sparkon.persistence.schema

import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.persistence.schema.domain.PersistenceException
import org.slf4j.LoggerFactory
import scala.io.Source
import scala.util.{Failure, Success, Try}

trait CassandraServices extends Serializable {

  val logger = LoggerFactory.getLogger(this.getClass)

  val keyspacePattern = "#KEYSPACE#"

  def createSchema(keyspace: String,
    cassandraCQLPath: String)(implicit connector: CassandraConnector) = {

    val cqlStatements = Try {
      val url = getClass.getResource(cassandraCQLPath)
      val cql = Source.fromURL(url).mkString
      cql.split("\n\n").toSeq
    }

    cqlStatements match {
      case Success(cql) =>
        val finalCQL =
          cql
            .filterNot(_.trim.isEmpty)
            .map(_.replaceAll(keyspacePattern, keyspace))
        connector.withSessionDo { session => finalCQL foreach session.execute }
      case Failure(e) =>
        logger.error("The Cassandra schema could not be loaded", e)
        throw PersistenceException(e.getMessage, Some(e))
    }
  }
}
