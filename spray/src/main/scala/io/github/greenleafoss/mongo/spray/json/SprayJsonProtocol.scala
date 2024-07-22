package io.github.greenleafoss.mongo.spray.json

import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.util.LocalDateOps.*
import io.github.greenleafoss.mongo.core.util.LocalDateTimeOps.*
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps.*

import io.github.greenleafoss.mongo.spray.util.SprayJsonBsonOps

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

import spray.json.*

trait SprayJsonProtocol
  extends GreenLeafMongoJsonBasicFormats
  with SprayJsonBsonOps
  with StandardFormats
  with CollectionFormats
  with ProductFormats
  with AdditionalFormats:

  override protected def formatInt: JsonFormat[Int] = DefaultJsonProtocol.IntJsonFormat

  override protected def formatLong: JsonFormat[Long] = DefaultJsonProtocol.LongJsonFormat

  override protected def formatFloat: JsonFormat[Float] = DefaultJsonProtocol.FloatJsonFormat

  override protected def formatDouble: JsonFormat[Double] = DefaultJsonProtocol.DoubleJsonFormat

  override protected def formatByte: JsonFormat[Byte] = DefaultJsonProtocol.ByteJsonFormat

  override protected def formatShort: JsonFormat[Short] = DefaultJsonProtocol.ShortJsonFormat

  override protected def formatBigDecimal: JsonFormat[BigDecimal] = DefaultJsonProtocol.BigDecimalJsonFormat

  override protected def formatBigInt: JsonFormat[BigInt] = DefaultJsonProtocol.BigIntJsonFormat

  override protected def formatUnit: JsonFormat[Unit] = DefaultJsonProtocol.UnitJsonFormat

  override protected def formatBoolean: JsonFormat[Boolean] = DefaultJsonProtocol.BooleanJsonFormat

  override protected def formatChar: JsonFormat[Char] = DefaultJsonProtocol.CharJsonFormat

  override protected def formatString: JsonFormat[String] = DefaultJsonProtocol.StringJsonFormat

  override protected def formatSymbol: JsonFormat[Symbol] = DefaultJsonProtocol.SymbolJsonFormat

  override protected def formatLocalDate: JsonFormat[LocalDate] = new JsonFormat[LocalDate]:
    def write(value: LocalDate): JsValue = JsString(value.printLocalDate)

    def read(json: JsValue): LocalDate = json match
      case JsString(ld) => ld.parseLocalDate
      case _            => deserializationError(s"Expected LocalDateTime, but got $json")

  override protected def formatLocalDateTime: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime]:
    def write(value: LocalDateTime): JsValue = JsString(value.printLocalDateTime)

    def read(json: JsValue): LocalDateTime = json match
      case JsString(ldt) => ldt.parseLocalDateTime
      case _             => deserializationError(s"Expected LocalDateTime, but got $json")

  override protected def formatZonedDateTime: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime]:
    def write(value: ZonedDateTime): JsValue = JsString(value.printZonedDateTime)

    def read(json: JsValue): ZonedDateTime = json match
      case JsString(zdt) => zdt.parseZonedDateTime
      case _             => deserializationError(s"Expected ZonedDateTime, but got $json")

  override protected def formatUUID: JsonFormat[UUID] = new JsonFormat[UUID]:
    def write(v: UUID): JsValue = JsString(v.toString)

    def read(value: JsValue): UUID = value match
      case JsString(v) => UUID.fromString(v)
      case x           => deserializationError(s"Expected UUID, but got $x")

  override protected def formatObjectId: JsonFormat[ObjectId] = new JsonFormat[ObjectId]:
    def write(obj: ObjectId): JsValue = JsString(obj.toString)

    def read(jsValue: JsValue): ObjectId = jsValue match
      case JsString(value) => new ObjectId(value)
      case x               => deserializationError(s"Expected ObjectId, but got $x")

  def enumToJsonFormatAsString(e: Enumeration): JsonFormat[e.Value] = new JsonFormat[e.Value]:
    def write(v: e.Value): JsValue    = StringJsonFormat.write(v.toString)
    def read(value: JsValue): e.Value = e.withName(StringJsonFormat.read(value))

  def enumToJsonFormatAsInt(e: Enumeration): JsonFormat[e.Value] = new JsonFormat[e.Value]:
    def write(v: e.Value): JsValue    = IntJsonFormat.write(v.id)
    def read(value: JsValue): e.Value = e.apply(IntJsonFormat.read(value))

object SprayJsonProtocol extends SprayJsonProtocol
