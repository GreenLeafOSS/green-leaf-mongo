package com.github.lashchenko.sjmq

import java.time.{Instant, ZoneOffset, ZonedDateTime}

import org.mongodb.scala.bson.ObjectId
import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat, NullOptions, deserializationError}

import scala.util.matching.Regex

trait ScalaSprayBsonProtocol
  extends ScalaSprayJsonProtocol
  with NullOptions {

  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#numberlong
  override implicit val LongJsonFormat: JsonFormat[Long] = new JsonFormat[Long] {

    def read(jsValue: JsValue): Long = jsValue match {
      case JsObject(fields) => fields("$numberLong") match {
        case JsString(v) => v.toLong
        case x => deserializationError("Expected Long, but got " + x)
      }
      case JsNumber(v) => v.toLong
      case x => deserializationError("Expected Long, but got " + x)
    }

    override def write(obj: Long): JsValue = obj match {
      case x if Int.MinValue <= x && x <= Int.MaxValue => JsNumber(x)
      case x => JsObject("$numberLong" -> JsString(x.toString))
    }
  }

  // https://docs.mongodb.com/manual/core/shell-types/#numberdecimal
  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#numberdecimal
  override implicit val BigDecimalJsonFormat: JsonFormat[BigDecimal] = new JsonFormat[BigDecimal] {
    override def read(jsValue: JsValue): BigDecimal = jsValue match {
      case JsNumber(v) => v
      case JsString(v) => BigDecimal(v)
      case JsObject(fields) => fields("$numberDecimal") match {
        case JsString(v) => BigDecimal(v)
        case x => deserializationError("Expected BigDecimal/NumberDecimal, but got " + x)
      }
      case x => deserializationError("Expected BigDecimal/NumberDecimal, but got " + x)
    }

    override def write(obj: BigDecimal): JsValue = {
      JsObject("$numberDecimal" -> JsString(obj.toString()))
    }
  }

  // https://docs.mongodb.com/manual/reference/bson-types/#date
  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#date
  override implicit val ZdtJsonFormat: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime] {

    def write(obj: ZonedDateTime): JsValue = {
      JsObject("$date" -> JsNumber(obj.toInstant.toEpochMilli))
    }

    def read(jsValue: JsValue): ZonedDateTime = jsValue match {
      case JsObject(fields) => fields("$date") match {
        case JsNumber(v) => Instant.ofEpochMilli(v.toLong).atZone(ZoneOffset.UTC)
        case x => deserializationError("Expected ZonedDateTime, but got " + x)
      }
      case x => deserializationError("Expected ZonedDateTime, but got " + x)
    }
  }

  // https://docs.mongodb.com/manual/reference/bson-types/#objectid
  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#oid
  override implicit val ObjectIdJsonFormat: JsonFormat[ObjectId] = new JsonFormat[ObjectId] {
    def write(obj: ObjectId): JsValue = JsObject("$oid" -> JsString(obj.toString))

    def read(jsValue: JsValue): ObjectId = jsValue match {
      case JsObject(fields) => fields("$oid") match {
        case JsString(oid) => new ObjectId(oid)
        case x => deserializationError("Expected ObjectId, but got " + x)
      }
      case x => deserializationError("Expected ObjectId, but got " + x)
    }
  }

  implicit val RegexJsonFormat: JsonFormat[Regex] = new JsonFormat[Regex] {

    override def read(jsValue: JsValue): Regex = jsValue match {
      case JsObject(fields) =>
        fields("$regex") match {
          case JsString(v) => v.r
          case _ => deserializationError("Regex expected")
        }
      case x => deserializationError(s"Regex expected: $x")
    }

    override def write(obj: Regex): JsValue = JsObject("$regex" -> JsString(obj.toString))
  }
}

object ScalaSprayBsonProtocol extends ScalaSprayBsonProtocol
