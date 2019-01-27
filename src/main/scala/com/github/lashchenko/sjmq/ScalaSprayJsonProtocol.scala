package com.github.lashchenko.sjmq

import java.time.ZonedDateTime

import org.mongodb.scala.bson.ObjectId
import spray.json.{AdditionalFormats, CollectionFormats, DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, ProductFormats, StandardFormats, deserializationError}

trait ScalaSprayJsonProtocol
  extends StandardFormats
  with CollectionFormats
  with ProductFormats
  with AdditionalFormats {

  implicit val IntJsonFormat: JsonFormat[Int] = DefaultJsonProtocol.IntJsonFormat

  implicit val LongJsonFormat: JsonFormat[Long] = DefaultJsonProtocol.LongJsonFormat

  implicit val FloatJsonFormat: JsonFormat[Float] = DefaultJsonProtocol.FloatJsonFormat

  implicit val DoubleJsonFormat: JsonFormat[Double] = DefaultJsonProtocol.DoubleJsonFormat

  implicit val ByteJsonFormat: JsonFormat[Byte] = DefaultJsonProtocol.ByteJsonFormat

  implicit val ShortJsonFormat: JsonFormat[Short] = DefaultJsonProtocol.ShortJsonFormat

  implicit val BigDecimalJsonFormat: JsonFormat[BigDecimal] = DefaultJsonProtocol.BigDecimalJsonFormat

  implicit val BigIntJsonFormat: JsonFormat[BigInt] = DefaultJsonProtocol.BigIntJsonFormat

  implicit val UnitJsonFormat: JsonFormat[Unit] = DefaultJsonProtocol.UnitJsonFormat

  implicit val BooleanJsonFormat: JsonFormat[Boolean] = DefaultJsonProtocol.BooleanJsonFormat

  implicit val CharJsonFormat: JsonFormat[Char] = DefaultJsonProtocol.CharJsonFormat

  implicit val StringJsonFormat: JsonFormat[String] = DefaultJsonProtocol.StringJsonFormat

  implicit val SymbolJsonFormat: JsonFormat[Symbol] = DefaultJsonProtocol.SymbolJsonFormat

  implicit val ZdtJsonFormat: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime] {

    import ZonedDateTimeOps._

    def write(obj: ZonedDateTime): JsValue = {
      JsString(obj.format(DateTimePattern))
    }

    def read(jsValue: JsValue): ZonedDateTime = jsValue match {
      case JsString(zdt) if zdt.length >= 20 => parseDateTimeIso(zdt)
      case JsString(zdt) if zdt.length > 10 => parseDateTime(zdt)
      case JsString(zdt) => parseDate(zdt)
      case x => deserializationError("Expected ZonedDateTime, but got " + x)
    }
  }

  implicit val ObjectIdJsonFormat: JsonFormat[ObjectId] = new JsonFormat[ObjectId] {
    def write(obj: ObjectId): JsValue = JsString(obj.toString)

    def read(jsValue: JsValue): ObjectId = jsValue match {
      case JsString(value) => new ObjectId(value)
      case x => deserializationError("Expected ObjectId, but got " + x)
    }
  }

  def enumToJsonFormatAsString(e: Enumeration): JsonFormat[e.Value] = new JsonFormat[e.Value] {
    def write(v: e.Value): JsValue = JsString(v.toString)

    def read(value: JsValue): e.Value = value match {
      case JsString(v) => e.withName(v)
      case _ => deserializationError(s"Unexpected enum $value")
    }
  }

  def enumToJsonFormatAsInt(e: Enumeration): JsonFormat[e.Value] = new JsonFormat[e.Value] {
    def write(v: e.Value): JsValue = JsNumber(v.id)

    def read(value: JsValue): e.Value = value match {
      case JsNumber(v) => e.apply(v.intValue())
      case _ => deserializationError(s"Unexpected enum $value")
    }
  }

}

object ScalaSprayJsonProtocol extends ScalaSprayJsonProtocol
