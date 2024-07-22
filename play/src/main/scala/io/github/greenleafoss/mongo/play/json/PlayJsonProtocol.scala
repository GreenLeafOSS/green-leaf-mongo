package io.github.greenleafoss.mongo.play.json

import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps

import io.github.greenleafoss.mongo.play.util.PlayJsonBsonOps

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

import play.api.libs.json.*
import play.api.libs.json.given

trait PlayJsonProtocol extends GreenLeafMongoJsonBasicFormats with PlayJsonBsonOps:

  override protected def formatInt: JsonFormat[Int] = Format(Reads.IntReads, Writes.IntWrites)

  override protected def formatLong: JsonFormat[Long] = Format(Reads.LongReads, Writes.LongWrites)

  override protected def formatFloat: JsonFormat[Float] = Format(Reads.FloatReads, Writes.FloatWrites)

  override protected def formatDouble: JsonFormat[Double] = Format(Reads.DoubleReads, Writes.DoubleWrites)

  override protected def formatByte: JsonFormat[Byte] = Format(Reads.ByteReads, Writes.ByteWrites)

  override protected def formatShort: JsonFormat[Short] = Format(Reads.ShortReads, Writes.ShortWrites)

  override protected def formatBigDecimal: JsonFormat[BigDecimal] = Format(Reads.bigDecReads, Writes.BigDecimalWrites)

  override protected def formatBigInt: JsonFormat[BigInt] = Format(Reads.BigIntReads, Writes.BigIntWrites)

  override protected def formatUnit: JsonFormat[Unit] = new JsonFormat[Unit]:
    override def reads(json: JsValue): JsResult[Unit] = JsSuccess(())
    override def writes(o: Unit): JsValue             = JsObject.empty

  override protected def formatBoolean: JsonFormat[Boolean] = Format(Reads.BooleanReads, Writes.BooleanWrites)

  override protected def formatChar: JsonFormat[Char] = ???

  override protected def formatString: JsonFormat[String] = Format(Reads.StringReads, Writes.StringWrites)

  override protected def formatSymbol: JsonFormat[Symbol] = ???

  override protected def formatLocalDate: JsonFormat[LocalDate] =
    Format(Reads.DefaultLocalDateReads, Writes.DefaultLocalDateWrites)

  override protected def formatLocalDateTime: JsonFormat[LocalDateTime] =
    Format(Reads.DefaultLocalDateTimeReads, Writes.DefaultLocalDateTimeWrites)

  override protected def formatZonedDateTime: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime]:
    import ZonedDateTimeOps.*

    override def writes(value: ZonedDateTime): JsValue =
      JsString(value.printZonedDateTime)

    def reads(json: JsValue): JsResult[ZonedDateTime] = json match
      case JsString(zdt) => JsSuccess(zdt.parseZonedDateTime)
      case _             => JsError(s"Expected ZonedDateTime, but got $json")

  override protected def formatUUID: JsonFormat[UUID] = Format(Reads.uuidReads, Writes.UuidWrites)

  override protected def formatObjectId: JsonFormat[ObjectId] = new JsonFormat[ObjectId]:

    def writes(obj: ObjectId): JsValue = JsString(obj.toString)

    def reads(json: JsValue): JsResult[ObjectId] = json match
      case JsString(value) => JsSuccess(new ObjectId(value))
      case x               => JsError(s"Expected ObjectId, but got $x")

object PlayJsonProtocol extends PlayJsonProtocol
