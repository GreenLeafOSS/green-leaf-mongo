package io.github.greenleafoss.mongo.core.json

import io.github.greenleafoss.mongo.core.log.Log
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import java.util.UUID

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait JsonFormatSpec extends AnyFlatSpec with Matchers with Log:

  this: GreenLeafJsonBsonOps =>

  // https://www.scalatest.org/user_guide/sharing_tests

  def jsonFormat[E: JsonFormat](e: E, json: String): Unit =
    it should s"serialize to JSON ($e)" in:
      convertToJson(e) shouldBe parseJson(json)

    it should s"deserialize from JSON ($e)" in:
      parseJson(json).convertTo[E] shouldBe e
