package io.github.greenleafoss.mongo.circe.json

import io.github.greenleafoss.mongo.circe.util.CirceJsonBsonOps
import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

import io.circe.Codec
import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.Encoder
import io.circe.HCursor

trait CirceJsonProtocol extends GreenLeafMongoJsonBasicFormats with CirceJsonBsonOps:

  override protected def formatInt: JsonFormat[Int] =
    Codec.from(Decoder.decodeInt, Encoder.encodeInt)

  override protected def formatLong: JsonFormat[Long] =
    Codec.from(Decoder.decodeLong, Encoder.encodeLong)

  override protected def formatFloat: JsonFormat[Float] =
    Codec.from(Decoder.decodeFloat, Encoder.encodeFloat)

  override protected def formatDouble: JsonFormat[Double] =
    Codec.from(Decoder.decodeDouble, Encoder.encodeDouble)

  override protected def formatByte: JsonFormat[Byte] =
    Codec.from(Decoder.decodeByte, Encoder.encodeByte)

  override protected def formatShort: JsonFormat[Short] =
    Codec.from(Decoder.decodeShort, Encoder.encodeShort)

  override protected def formatBigDecimal: JsonFormat[BigDecimal] =
    Codec.from(Decoder.decodeBigDecimal, Encoder.encodeBigDecimal)

  override protected def formatBigInt: JsonFormat[BigInt] =
    Codec.from(Decoder.decodeBigInt, Encoder.encodeBigInt)

  override protected def formatUnit: JsonFormat[Unit] =
    Codec.from(Decoder.decodeUnit, Encoder.encodeUnit)

  override protected def formatBoolean: JsonFormat[Boolean] =
    Codec.from(Decoder.decodeBoolean, Encoder.encodeBoolean)

  override protected def formatChar: JsonFormat[Char] =
    Codec.from(Decoder.decodeChar, Encoder.encodeChar)

  override protected def formatString: JsonFormat[String] =
    Codec.from(Decoder.decodeString, Encoder.encodeString)

  override protected def formatSymbol: JsonFormat[Symbol] = ???

  override protected def formatLocalDate: JsonFormat[LocalDate] =
    Codec.from(Decoder.decodeLocalDate, Encoder.encodeLocalDate)

  override protected def formatLocalDateTime: JsonFormat[LocalDateTime] =
    Codec.from(Decoder.decodeLocalDateTime, Encoder.encodeLocalDateTime)

  override protected def formatZonedDateTime: JsonFormat[ZonedDateTime] =
    Codec.from(Decoder.decodeZonedDateTime, Encoder.encodeZonedDateTime)

  override protected def formatUUID: JsonFormat[UUID] =
    Codec.from(Decoder.decodeUUID, Encoder.encodeUUID)

  override protected def formatObjectId: JsonFormat[ObjectId] = new JsonFormat[ObjectId]:
    override def apply(c: HCursor): Result[ObjectId] =
      StringJsonFormat(c).map(oid => new ObjectId(oid))

    override def apply(a: ObjectId): Json =
      StringJsonFormat(a.toString)

object CirceJsonProtocol extends CirceJsonProtocol
