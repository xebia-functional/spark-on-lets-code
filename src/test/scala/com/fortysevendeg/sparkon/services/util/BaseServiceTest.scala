package com.fortysevendeg.sparkon.services.util

import org.specs2.mutable.Specification

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait BaseServiceTest extends Specification {

  def await[T](future: Future[T]) = Await.result(future, Duration.Inf)
}