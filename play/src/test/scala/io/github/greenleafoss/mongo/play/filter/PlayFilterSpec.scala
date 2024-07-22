package io.github.greenleafoss.mongo.play.filter

import io.github.greenleafoss.mongo.core.filter.GreenLeafMongoFilterOpsSpec

import io.github.greenleafoss.mongo.play.bson.PlayBsonProtocol
import io.github.greenleafoss.mongo.play.util.PlayJsonBsonOps

import scala.language.implicitConversions

import org.scalatest.wordspec.AnyWordSpec

class PlayFilterSpec extends GreenLeafMongoFilterOpsSpec with PlayBsonProtocol with PlayJsonBsonOps
