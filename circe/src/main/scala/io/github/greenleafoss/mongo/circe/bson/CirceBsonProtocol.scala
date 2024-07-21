package io.github.greenleafoss.mongo.circe.bson

import io.github.greenleafoss.mongo.circe.json.CirceJsonProtocol
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps.*

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

import scala.jdk.CollectionConverters.*
import scala.util
import scala.util.matching.Regex

import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.HCursor
import io.circe.Json as CirceJson

trait CirceBsonProtocol extends CirceJsonProtocol:

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Int32
   * {{{
   * { "$numberInt": "<number>" }
   * }}}
   * */
  override protected def formatInt: JsonFormat[Int] = new JsonFormat[Int]:
    override def apply(c: HCursor): Result[Int] =
      c.get[String]($numberInt).map(_.toInt).orElse(Decoder.decodeInt(c))

    override def apply(a: Int): Json =
      CirceJson.obj($numberInt -> CirceJson.fromString(a.toString))

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Int64
   * {{{
   *   { "$numberLong": "<number>" }
   * }}}
   */
  override protected def formatLong: JsonFormat[Long] = new JsonFormat[Long]:
    override def apply(c: HCursor): Result[Long] = {
      c.get[String]($numberLong)
        .map(_.toLong)
        .orElse(IntJsonFormat(c).map(_.toLong))
        .orElse(Decoder.decodeLong(c))
    }

    override def apply(a: Long): Json =
      CirceJson.obj($numberLong -> CirceJson.fromString(a.toString))

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Double
   * {{{
   *   {"$numberDouble": "<decimal string>" }
   * }}}
   */
  override protected def formatFloat: JsonFormat[Float] = new JsonFormat[Float]:
    override def apply(c: HCursor): Result[Float] =
      c.get[String]($numberDouble).map(java.lang.Float.valueOf).map(Float.unbox).orElse(Decoder.decodeFloat(c))

    override def apply(a: Float): Json =
      CirceJson.obj($numberDouble -> CirceJson.fromString(a.toString))

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Double
   * {{{
   *   {"$numberDouble": "<decimal string>" }
   * }}}
   */
  override protected def formatDouble: JsonFormat[Double] = new JsonFormat[Double]:
    override def apply(c: HCursor): Result[Double] =
      c.get[String]($numberDouble)
        .map(java.lang.Double.valueOf)
        .map(Double.unbox)
        .orElse(Decoder.decodeDouble(c))

    override def apply(a: Double): Json =
      CirceJson.obj($numberDouble -> CirceJson.fromString(a.toString))

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Decimal128
   * {{{
   *   { "$numberDecimal": "<number>" }
   * }}}
   */
  override protected def formatBigDecimal: JsonFormat[BigDecimal] = new JsonFormat[BigDecimal]:
    override def apply(c: HCursor): Result[BigDecimal] =
      c.get[String]($numberDecimal)
        .map(BigDecimal.apply)
        .orElse(IntJsonFormat.tryDecode(c.downField($numberInt)).map(BigDecimal.apply))
        .orElse(LongJsonFormat.tryDecode(c.downField($numberLong)).map(BigDecimal.apply))
        .orElse(DoubleJsonFormat.tryDecode(c.downField($numberDouble)).map(BigDecimal.apply))
        .orElse(Decoder.decodeBigDecimal(c))

    override def apply(a: BigDecimal): Json = CirceJson.obj($numberDecimal -> CirceJson.fromString(a.toString))

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatZonedDateTime: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime]:
    override def apply(c: HCursor): Result[ZonedDateTime] =
      c.downField($date)
        .get[String]($numberLong)
        .map(_.toLong.asZonedDateTime())
        .orElse(Decoder.decodeZonedDateTime(c))

    override def apply(a: ZonedDateTime): CirceJson =
      CirceJson.obj($date -> LongJsonFormat(a.toEpochMilli))

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatLocalDateTime: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime]:
    override def apply(c: HCursor): Result[LocalDateTime] =
      ZonedDateTimeJsonFormat(c).map(LocalDateTime.from)

    override def apply(a: LocalDateTime): CirceJson =
      ZonedDateTimeJsonFormat(ZonedDateTime.from(a.atZone(ZoneOffset.UTC)))

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatLocalDate: JsonFormat[LocalDate] = new JsonFormat[LocalDate]:
    override def apply(c: HCursor): Result[LocalDate] =
      LocalDateTimeJsonFormat(c).map(LocalDate.from)

    override def apply(a: LocalDate): CirceJson =
      LocalDateTimeJsonFormat(a.atStartOfDay())

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-ObjectId
   * {{{
   *   { "$oid": "<ObjectId bytes>" }
   * }}}
   */
  override protected def formatObjectId: JsonFormat[ObjectId] = new JsonFormat[ObjectId]:
    override def apply(c: HCursor): Result[ObjectId] =
      c.get[String]($oid).map(oid => new ObjectId(oid))

    override def apply(a: ObjectId): CirceJson =
      CirceJson.obj($oid -> CirceJson.fromString(a.toString))

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Regular-Expression
   * {{{
   *   {
   *     "$regularExpression": {
   *       "pattern": "<regexPattern>",
   *       "options": "<options>"
   *     }
   *   }
   * }}}
   */
  given RegexBsonFormat: JsonFormat[Regex] = new JsonFormat[Regex]:
    override def apply(c: HCursor): Result[Regex] =
      c.downField($regularExpression).get[String]("pattern").map(_.r)

    override def apply(a: Regex): CirceJson =
      CirceJson.obj(
        $regularExpression -> CirceJson.obj(
          "pattern" -> CirceJson.fromString(a.toString),
          "options" -> CirceJson.fromString("")
        )
      )

object CirceBsonProtocol extends CirceBsonProtocol
