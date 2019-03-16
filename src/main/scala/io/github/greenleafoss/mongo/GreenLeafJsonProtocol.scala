package io.github.greenleafoss.mongo

import java.time.ZonedDateTime
import java.util.UUID

import org.mongodb.scala.bson.ObjectId
import spray.json.{AdditionalFormats, CollectionFormats, DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, ProductFormats, StandardFormats, deserializationError}

trait GreenLeafJsonProtocol
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

  implicit val ZdtJsonFormat: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime] with ZonedDateTimeOps {
    def write(obj: ZonedDateTime): JsValue = JsString(obj.format(DateTimePattern))

    def read(jsValue: JsValue): ZonedDateTime = jsValue match {
      case JsString(zdt) if zdt.length >= 20 => parseDateTimeIso (zdt) // 1970-01-01T01:02:03+04:00
      case JsString(zdt) if zdt.length >= 19 && zdt.contains('T') => parseDateTimeIso(zdt) // 1970-01-01T00:00:00
      case JsString(zdt) if zdt.length == 19 => parseDateTime (zdt) // 1970-01-01 00:00:00
      case JsString(zdt) => parseDate (zdt)
      case x => deserializationError(s"Expected ZonedDateTime, but got $x")
    }
  }

  implicit val ObjectIdJsonFormat: JsonFormat[ObjectId] = new JsonFormat[ObjectId] {
    def write(obj: ObjectId): JsValue = JsString(obj.toString)

    def read(jsValue: JsValue): ObjectId = jsValue match {
      case JsString(value) => new ObjectId(value)
      case x => deserializationError(s"Expected ObjectId, but got $x")
    }
  }

  def enumToJsonFormatAsString(e: Enumeration): JsonFormat[e.Value] = new JsonFormat[e.Value] {
    def write(v: e.Value): JsValue = JsString(v.toString)

    def read(value: JsValue): e.Value = value match {
      case JsString(v) => e.withName(v)
      case x => deserializationError(s"Expected enum, but got $x")
    }
  }

  def enumToJsonFormatAsInt(e: Enumeration): JsonFormat[e.Value] = new JsonFormat[e.Value] {
    def write(v: e.Value): JsValue = JsNumber(v.id)

    def read(value: JsValue): e.Value = value match {
      case JsNumber(v) => e.apply(v.intValue())
      case x => deserializationError(s"Expected enum, but got $x")
    }
  }

  implicit def UuidAsStrJsonFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    def write(v: UUID): JsValue = JsString(v.toString)

    def read(value: JsValue): UUID = value match {
      case JsString(v) => UUID.fromString(v)
      case x => deserializationError(s"Expected UUID, but got $x")
    }
  }

}

object GreenLeafJsonProtocol extends GreenLeafJsonProtocol
