package com.fortysevendeg.sparkon.api.http

import spray.json.DefaultJsonProtocol

trait Protocols extends DefaultJsonProtocol {
  implicit val infoFormat = jsonFormat1(Info.apply)
}