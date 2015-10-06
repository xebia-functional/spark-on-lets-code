package com.fortysevendeg.sparkon.services.twitter.domain

case class TwitterServiceException(message: String, cause: Throwable)
  extends RuntimeException(message, cause)
