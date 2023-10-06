package io.github.greenleafoss.mongo.spray.bson

import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps.*
import io.github.greenleafoss.mongo.spray.json.SprayJsonProtocol

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

import scala.util.matching.Regex

import spray.json.*

trait SprayBsonProtocol extends SprayJsonProtocol:

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Int32
   * {{{
   * { "$numberInt": "<number>" }
   * }}}
   **/
  override protected def formatInt: JsonFormat[Int] = new JsonFormat[Int]:
    override def write(v: Int): JsValue = JsObject($numberInt -> JsString(v.toString))

    override def read(json: JsValue): Int = json match
      case JsObject(fields) if fields.contains($numberInt) =>
        fields($numberInt) match
          case JsString(v) => v.toInt
          case _           => deserializationError(s"Expected Int as {${$numberInt}: <value>}, but got $json")

      case _ => deserializationError(s"Expected Int as {${$numberInt}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Int64
   * {{{
   *   { "$numberLong": "<number>" }
   * }}}
   */
  override protected def formatLong: JsonFormat[Long] = new JsonFormat[Long]:
    override def write(v: Long): JsValue = JsObject($numberLong -> JsString(v.toString))

    override def read(json: JsValue): Long = json match
      case JsObject(fields) if fields.contains($numberLong) =>
        fields($numberLong) match
          case JsString(v) => v.toLong
          case _           => deserializationError(s"Expected Long as {${$numberLong}: <value>}, but got $json")

      case JsObject(fields) if fields.contains($numberInt) => IntJsonFormat.read(json).toLong

      case _ => deserializationError(s"Expected Long as {${$numberLong}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Double
   * {{{
   *   {"$numberDouble": "<decimal string>" }
   * }}}
   */
  override protected def formatFloat: JsonFormat[Float] = new JsonFormat[Float]:
    override def write(v: Float): JsValue = v match
      case Float.NegativeInfinity => JsObject($numberDouble -> JsString("-Infinity"))
      case Float.PositiveInfinity => JsObject($numberDouble -> JsString("Infinity"))
      case Float.NaN              => JsObject($numberDouble -> JsString("NaN"))
      case x                      => JsObject($numberDouble -> JsString(x.toString))

    override def read(json: JsValue): Float = json match
      case JsObject(fields) if fields.contains($numberDouble) =>
        fields($numberDouble) match
          case JsString("-Infinity") => Float.NegativeInfinity
          case JsString("Infinity")  => Float.PositiveInfinity
          case JsString("NaN")       => Float.NaN
          case JsString(x)           => x.toFloat

          case _ => deserializationError(s"Expected Float as {${$numberDouble}: <value>}, but got $json")

      case _ => deserializationError(s"Expected Float as {${$numberDouble}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Double
   * {{{
   *   {"$numberDouble": "<decimal string>" }
   * }}}
   */
  override protected def formatDouble: JsonFormat[Double] = new JsonFormat[Double]:
    override def write(value: Double): JsValue = value match
      case Double.NegativeInfinity => JsObject($numberDouble -> JsString("-Infinity"))
      case Double.PositiveInfinity => JsObject($numberDouble -> JsString("Infinity"))
      case Double.NaN              => JsObject($numberDouble -> JsString("NaN"))
      case x                       => JsObject($numberDouble -> JsString(x.toString))

    override def read(json: JsValue): Double = json match
      case JsObject(fields) if fields.contains($numberDouble) =>
        fields($numberDouble) match
          case JsString("-Infinity") => Double.NegativeInfinity
          case JsString("Infinity")  => Double.PositiveInfinity
          case JsString("NaN")       => Double.NaN
          case JsString(x)           => x.toDouble

          case _ => deserializationError(s"Expected Double as {${$numberDouble}: <value>}, but got $json")

      case _ => deserializationError(s"Expected Double as {${$numberDouble}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-Decimal128
   * {{{
   *   { "$numberDecimal": "<number>" }
   * }}}
   */
  override protected def formatBigDecimal: JsonFormat[BigDecimal] = new JsonFormat[BigDecimal]:
    override def write(value: BigDecimal): JsValue = JsObject($numberDecimal -> JsString(value.toString))

    override def read(json: JsValue): BigDecimal = json match
      case JsObject(fields) if fields.contains($numberDecimal) =>
        fields($numberDecimal) match
          case JsString(v) => BigDecimal(v)
          case _           => deserializationError(s"Expected BigDecimal as {${$numberDecimal}: <value>}, but got $json")

      case JsObject(fields) if fields.contains($numberInt)    => BigDecimal(IntJsonFormat.read(json))
      case JsObject(fields) if fields.contains($numberLong)   => BigDecimal(LongJsonFormat.read(json))
      case JsObject(fields) if fields.contains($numberDouble) => BigDecimal(DoubleJsonFormat.read(json))
      case JsString(value)                                    => BigDecimal(value)
      case JsNumber(value)                                    => value

      case _ => deserializationError(s"Expected BigDecimal as {${$numberDecimal}: <value>}, but got $json")

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatZonedDateTime: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime]:
    override def write(value: ZonedDateTime): JsValue = JsObject($date -> LongJsonFormat.write(value.toEpochMilli))

    override def read(json: JsValue): ZonedDateTime = json match
      // $date is millis
      case JsObject(fields) if fields.contains($date) => LongJsonFormat.read(fields($date)).asZonedDateTime()

      // unexpected json
      case _ => deserializationError(s"Expected ZonedDateTime as {${$date}: <${$numberLong}>}, but got $json")

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatLocalDateTime: JsonFormat[LocalDateTime] = new JsonFormat[LocalDateTime]:
    override def write(value: LocalDateTime): JsValue =
      ZonedDateTimeJsonFormat.write(ZonedDateTime.from(value.atZone(ZoneOffset.UTC)))
    override def read(json: JsValue): LocalDateTime   = LocalDateTime.from(ZonedDateTimeJsonFormat.read(json))

  /**
   * https://www.mongodb.com/docs/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
   * {{{
   *   {"$date": {"$numberLong": "<millis>"}}
   * }}}
   */
  override protected def formatLocalDate: JsonFormat[LocalDate] = new JsonFormat[LocalDate]:
    override def write(value: LocalDate): JsValue = LocalDateTimeJsonFormat.write(value.atStartOfDay())
    override def read(json: JsValue): LocalDate   = LocalDate.from(LocalDateTimeJsonFormat.read(json))

  /**
   * https://www.mongodb.com/docs/manual/reference/mongodb-extended-json/#mongodb-bsontype-ObjectId
   * {{{
   *   { "$oid": "<ObjectId bytes>" }
   * }}}
   */
  override protected def formatObjectId: JsonFormat[ObjectId] = new JsonFormat[ObjectId]:
    override def write(value: ObjectId): JsValue = JsObject($oid -> JsString(value.toString))

    override def read(json: JsValue): ObjectId = json match
      case JsObject(fields) if fields.contains($oid) =>
        fields($oid) match
          case JsString(oid) => new ObjectId(oid)
          case _             => deserializationError(s"Expected ObjectId as {${$oid}: <value>}, but got $json")

      case _ => deserializationError(s"Expected ObjectId as {${$oid}: <value>}, but got $json")

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
    override def write(value: Regex): JsValue =
      JsObject(
        $regularExpression -> JsObject(
          "pattern" -> JsString(value.toString),
          "options" -> JsString.empty
        )
      )

    override def read(json: JsValue): Regex = json match
      case JsObject(fields) if fields.contains($regularExpression) =>
        fields($regularExpression) match
          case JsObject(regularExpression) if regularExpression.contains("pattern") =>
            regularExpression("pattern") match
              case JsString(pattern) => pattern.r

              case _ => deserializationError(s"Expected Regex as {${$regularExpression}}, but got $json")

          case _ => deserializationError(s"Expected Regex as {${$regularExpression}}, but got $json")

      case _ => deserializationError(s"Expected Regex as {${$regularExpression}}, but got $json")

object SprayBsonProtocol extends SprayBsonProtocol
