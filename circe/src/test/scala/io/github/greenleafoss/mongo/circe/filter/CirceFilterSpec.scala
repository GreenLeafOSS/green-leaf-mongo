package io.github.greenleafoss.mongo.circe.filter

import io.github.greenleafoss.mongo.core.filter.GreenLeafMongoFilterOpsSpec

import io.github.greenleafoss.mongo.circe.bson.CirceBsonProtocol
import io.github.greenleafoss.mongo.circe.util.CirceJsonBsonOps

import scala.language.implicitConversions

import org.scalatest.wordspec.AnyWordSpec

class CirceFilterSpec extends GreenLeafMongoFilterOpsSpec with CirceBsonProtocol with CirceJsonBsonOps
