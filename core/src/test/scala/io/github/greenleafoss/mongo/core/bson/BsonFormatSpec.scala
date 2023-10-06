package io.github.greenleafoss.mongo.core.bson

import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.bson.BsonValue

import scala.language.implicitConversions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait BsonFormatSpec extends AnyFlatSpec with Matchers:

  this: GreenLeafJsonBsonOps =>

  // https://www.scalatest.org/user_guide/sharing_tests

  def bsonFormat[E: JsonFormat](e: E, bson: String): Unit =
    bsonFormat(e, bson, bson)

  def bsonFormat[E: JsonFormat](e: E, eToBson: String, bsonToE: String): Unit =
    it should s"serialize to BSON ($e) at ${System.nanoTime()}" in:
      (e: BsonValue) shouldBe eToBson.parseBson

    it should s"deserialize from BSON ($e) at ${System.nanoTime()}" in:
      bsonToE.parseBson.convertTo[E] shouldBe e
