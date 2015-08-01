package com.fortysevendeg.sparkon.persistence.schema

import com.datastax.spark.connector.cql.CassandraConnector
import com.fortysevendeg.sparkon.common.config.ConfigRegistry
import ConfigRegistry._
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.{Failure, Success, Try}

trait CassandraServices {

  val logger = LoggerFactory.getLogger(this.getClass)

  val keyspacePattern = "#KEYSPACE#"

  def createSchema(keyspace: String)(implicit connector: CassandraConnector): Unit = {

    val cqlStatements = Try {
      val url = getClass.getResource(cassandraCQLPath)
      val cql = Source.fromURL(url).mkString
      cql.split("\n\n").toSeq
    }

    cqlStatements match {
      case Success(cql) =>
        connector.withSessionDo { session =>
          cql filterNot { s => s.trim.isEmpty } map { newString: String =>
            val finalCQL = newString.replaceAll(keyspacePattern, keyspace)
            session.execute(finalCQL)
          }
        }
      case Failure(e) => logger.error("The Cassandra schema could not be loaded", e)
    }
  }
}

object CassandraServices extends CassandraServices