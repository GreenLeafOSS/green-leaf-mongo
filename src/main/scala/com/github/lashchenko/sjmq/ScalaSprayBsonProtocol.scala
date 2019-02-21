package com.github.lashchenko.sjmq

import java.time.{Instant, ZoneOffset, ZonedDateTime}

import org.mongodb.scala.bson.ObjectId
import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat, NullOptions, deserializationError}

import scala.util.matching.Regex

trait ScalaSprayBsonProtocol
  extends ScalaSprayJsonProtocol
  with NullOptions {

  override implicit val LongJsonFormat: JsonFormat[Long] = new JsonFormat[Long] {

    def read(jsValue: JsValue): Long = jsValue match {
      case JsObject(fields) =>
        fields("$numberLong") match {
          case JsString(v) => v.toLong
          case _ => deserializationError("Long expected")
        }
      case JsNumber(v) => v.toLong
      case _ => deserializationError("Long expected")
    }

    override def write(obj: Long): JsValue = JsObject("$numberLong" -> JsString(obj.toString))
  }

  override implicit val ZdtJsonFormat: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime] {

    def write(obj: ZonedDateTime): JsValue = {
      JsObject("$date" -> JsNumber(obj.toInstant.toEpochMilli))
    }

    def read(jsValue: JsValue): ZonedDateTime = jsValue match {
      case JsObject(fields) =>
        fields("$date") match {
          case JsNumber(v) => Instant.ofEpochMilli(v.toLong).atZone(ZoneOffset.UTC)
          case x => deserializationError("Expected ZonedDateTime, but got " + x)
        }
      case x => deserializationError("Expected ZonedDateTime, but got " + x)
    }
  }

  override implicit val ObjectIdJsonFormat: JsonFormat[ObjectId] = new JsonFormat[ObjectId] {
    def write(obj: ObjectId): JsValue = JsObject("$oid" -> JsString(obj.toString))

    def read(jsValue: JsValue): ObjectId = jsValue match {
      case JsObject(fields) =>
        fields("$oid") match {
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
