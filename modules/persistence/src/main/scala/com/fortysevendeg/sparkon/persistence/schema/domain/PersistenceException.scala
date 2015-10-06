package com.fortysevendeg.sparkon.persistence.schema.domain

case class PersistenceException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message, cause.orNull)
