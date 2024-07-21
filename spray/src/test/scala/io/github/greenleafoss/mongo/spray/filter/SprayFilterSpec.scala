package io.github.greenleafoss.mongo.spray.filter

import io.github.greenleafoss.mongo.core.filter.GreenLeafMongoFilterOpsSpec

import io.github.greenleafoss.mongo.spray.bson.SprayBsonProtocol
import io.github.greenleafoss.mongo.spray.util.SprayJsonBsonOps

import scala.language.implicitConversions

import org.scalatest.wordspec.AnyWordSpec

class SprayFilterSpec extends GreenLeafMongoFilterOpsSpec with SprayBsonProtocol with SprayJsonBsonOps
