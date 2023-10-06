package io.github.greenleafoss.mongo.play.bson

import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps.*
import io.github.greenleafoss.mongo.play.json.PlayJsonProtocol

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

import scala.util.Try
import scala.util.matching.Regex

import play.api.libs.json.*

trait PlayBsonProtocol extends PlayJsonProtocol:

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Int32
   * {{{
   * { "$numberInt": "<number>" }
   * }}}
   * */
  override protected def formatInt: JsonFormat[Int] = new JsonFormat[Int]:
    override def writes(v: Int): JsValue = JsObject(Map($numberInt -> JsString(v.toString)))

    override def reads(json: JsValue): JsResult[Int] = json \ $numberInt match
      case JsDefined(JsString(v)) => JsSuccess(v.toInt)
      case _                      => JsError(s"Expected Int as {${$numberInt}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Int64
   * {{{
   *   { "$numberLong": "<number>" }
   * }}}
   */
  override protected def formatLong: JsonFormat[Long] = new JsonFormat[Long]:
    override def writes(v: Long): JsValue = JsObject(Map($numberLong -> JsString(v.toString)))

    override def reads(json: JsValue): JsResult[Long] = json match
      case JsObject(fields) if fields.contains($numberLong) =>
        json \ $numberLong match
          case JsDefined(JsString(v)) => JsSuccess(v.toLong)
          case _                      => JsError(s"Expected Long as {${$numberLong}: <value>}, but got $json")

      case JsObject(fields) if fields.contains($numberInt) => IntJsonFormat.reads(json).map(_.toLong)

      case _ => JsError(s"Expected Long as {${$numberLong}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Double
   * {{{
   *   {"$numberDouble": "<decimal string>" }
   * }}}
   */
  override protected def formatFloat: JsonFormat[Float] = new JsonFormat[Float]:
    override def writes(v: Float): JsValue = v match
      case Float.NegativeInfinity => JsObject(Map($numberDouble -> JsString("-Infinity")))
      case Float.PositiveInfinity => JsObject(Map($numberDouble -> JsString("Infinity")))
      case Float.NaN              => JsObject(Map($numberDouble -> JsString("NaN")))
      case x                      => JsObject(Map($numberDouble -> JsString(x.toString)))

    override def reads(json: JsValue): JsResult[Float] = json \ $numberDouble match
      case JsDefined(JsString("-Infinity")) => JsSuccess(Float.NegativeInfinity)
      case JsDefined(JsString("Infinity"))  => JsSuccess(Float.PositiveInfinity)
      case JsDefined(JsString("NaN"))       => JsSuccess(Float.NaN)
      case JsDefined(JsString(x))           => JsSuccess(x.toFloat)
      case _                                => JsError(s"Expected Float as {${$numberDouble}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Double
   * {{{
   *   {"$numberDouble": "<decimal string>" }
   * }}}
   */
  override protected def formatDouble: JsonFormat[Double] = new JsonFormat[Double]:
    override def writes(value: Double): JsValue = value match
      case Double.NegativeInfinity => JsObject(Map($numberDouble -> JsString("-Infinity")))
      case Double.PositiveInfinity => JsObject(Map($numberDouble -> JsString("Infinity")))
      case Double.NaN              => JsObject(Map($numberDouble -> JsString("NaN")))
      case x                       => JsObject(Map($numberDouble -> JsString(x.toString)))

    override def reads(json: JsValue): JsResult[Double] = json match
      case JsObject(fields) if fields.contains($numberDouble) =>
        json \ $numberDouble match
          case JsDefined(JsString("-Infinity")) => JsSuccess(Double.NegativeInfinity)
          case JsDefined(JsString("Infinity"))  => JsSuccess(Double.PositiveInfinity)
          case JsDefined(JsString("NaN"))       => JsSuccess(Double.NaN)
          case JsDefined(JsString(x))           => JsSuccess(x.toDouble)

          case _ => JsError(s"Expected Double as {${$numberDouble}: <value>}, but got $json")

      case _ => JsError(s"Expected Double as {${$numberDouble}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Decimal128
   * {{{
   *   { "$numberDecimal": "<number>" }
   * }}}
   */
  override protected def formatBigDecimal: JsonFormat[BigDecimal] = new JsonFormat[BigDecimal]:
    override def writes(value: BigDecimal): JsValue = JsObject(Map($numberDecimal -> JsString(value.toString)))

    override def reads(json: JsValue): JsResult[BigDecimal] = json match
      case JsObject(fields) if fields.contains($numberDecimal) =>
        json \ $numberDecimal match
          case JsDefined(JsString(v)) => JsResult.fromTry(Try(BigDecimal(v)))
          case _                      => JsError(s"Expected BigDecimal as {${$numberDecimal}: <value>}, but got $json")

      case JsObject(fields) if fields.contains($numberInt)    => IntJsonFormat.reads(json).map(BigDecimal.apply)
      case JsObject(fields) if fields.contains($numberLong)   => LongJsonFormat.reads(json).map(BigDecimal.apply)
      case JsObject(fields) if fields.contains($numberDouble) => DoubleJsonFormat.reads(json).map(BigDecimal.apply)
      case JsString(value)                                    => JsResult.fromTry(Try(BigDecimal(value)))
      case JsNumber(value)                                    => JsSuccess(value)

      case _ => JsError(s"Expected BigDecimal as {${$numberDecimal}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatZonedDateTime: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime]:
    override def writes(value: ZonedDateTime): JsValue = JsObject(
      Map($date -> LongJsonFormat.writes(value.toEpochMilli))
    )

    override def reads(json: JsValue): JsResult[ZonedDateTime] = json \ $date match
      // $date is millis
      case JsDefined(millis) => LongJsonFormat.reads(millis).map(_.asZonedDateTime())

      // unexpected json
      case _ => JsError(s"Expected ZonedDateTime as {${$date}: <${$numberLong}>}, but got $json")

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatLocalDateTime: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime]:
    override def writes(value: LocalDateTime): JsValue =
      ZonedDateTimeJsonFormat.writes(ZonedDateTime.from(value.atZone(ZoneOffset.UTC)))

    override def reads(json: JsValue): JsResult[LocalDateTime] =
      ZonedDateTimeJsonFormat.reads(json).map(LocalDateTime.from)

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatLocalDate: JsonFormat[LocalDate] = new JsonFormat[LocalDate]:
    override def writes(value: LocalDate): JsValue = LocalDateTimeJsonFormat.writes(value.atStartOfDay())

    override def reads(json: JsValue): JsResult[LocalDate] = LocalDateTimeJsonFormat.reads(json).map(LocalDate.from)

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-ObjectId
   * {{{
   *   { "$oid": "<ObjectId bytes>" }
   * }}}
   */
  override protected def formatObjectId: JsonFormat[ObjectId] = new JsonFormat[ObjectId]:
    override def writes(value: ObjectId): JsValue = JsObject(Map($oid -> JsString(value.toString)))

    override def reads(json: JsValue): JsResult[ObjectId] = json \ $oid match
      case JsDefined(JsString(oid)) => JsSuccess(new ObjectId(oid))
      case _                        => JsError(s"Expected ObjectId as {${$oid}: <value>}, but got $json")

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
    override def writes(value: Regex): JsValue =
      JsObject(
        Map($regularExpression -> JsObject(Map("pattern" -> JsString(value.toString), "options" -> JsString(""))))
      )

    override def reads(json: JsValue): JsResult[Regex] = json \ $regularExpression \ "pattern" match
      case JsDefined(JsString(pattern)) => JsSuccess(pattern.r)
      case _                            => JsError(s"Expected Regex as {${$regularExpression}}, but got $json")

object PlayBsonProtocol extends PlayBsonProtocol
