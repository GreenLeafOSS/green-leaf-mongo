package io.github.greenleafoss.mongo

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import org.mongodb.scala.bson.ObjectId
import spray.json.{JsNull, JsNumber, JsObject, JsString, JsValue, JsonFormat, NullOptions, deserializationError}

import scala.util.matching.Regex

trait GreenLeafBsonProtocol
  extends GreenLeafJsonProtocol
  with NullOptions {

  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#numberlong
  override implicit def LongJsonFormat: JsonFormat[Long] = new JsonFormat[Long] {

    override def write(obj: Long): JsValue = JsNumber(obj)

    def read(jsValue: JsValue): Long = jsValue match {
      case JsObject(fields) => fields("$numberLong") match {
        case JsString(v) => v.toLong
        case JsNumber(v) => v.toLong
        case x => deserializationError(s"Expected Long as {$$numberLong: <value>}, but got $x")
      }
      case JsString(v) => v.toLong
      case JsNumber(v) => v.toLong
      case x => deserializationError(s"Expected Long as JsString/JsNumber, but got $x")
    }
  }

  override implicit def FloatJsonFormat: JsonFormat[Float] = new JsonFormat[Float] {

    def write(value: Float): JsValue = value match {
      case Float.NegativeInfinity => JsObject("$numberDouble" -> JsString("-Infinity"))
      case Float.PositiveInfinity => JsObject("$numberDouble" -> JsString("Infinity"))
      case Float.NaN => JsObject("$numberDouble" -> JsString("NaN"))
      case x => JsNumber(x)
    }

    def read(value: JsValue): Float = value match {
      case JsObject(fields) => fields("$numberDouble") match {
        case JsString("-Infinity") => Float.NegativeInfinity
        case JsString("Infinity") => Float.PositiveInfinity
        case JsString("NaN") | JsNull | JsString.empty => Float.NaN
        case x => deserializationError(s"Expected Float as {$$numberDouble: <value>}, but got $x")
      }
      case JsNumber(x) => x.floatValue
      case JsString(x) => x.toFloat
      case x => deserializationError(s"Expected Float as JsNumber/JsString, but got $x")
    }
  }

  override implicit def DoubleJsonFormat: JsonFormat[Double] = new JsonFormat[Double] {

    def write(value: Double): JsValue = value match {
      case Float.NegativeInfinity => JsObject("$numberDouble" -> JsString("-Infinity"))
      case Float.PositiveInfinity => JsObject("$numberDouble" -> JsString("Infinity"))
      case Float.NaN => JsObject("$numberDouble" -> JsString("NaN"))
      case x => JsNumber(x)
    }

    def read(value: JsValue): Double = value match {
      case JsObject(fields) => fields("$numberDouble") match {
        case JsString("-Infinity") => Double.NegativeInfinity
        case JsString("Infinity") => Double.PositiveInfinity
        case JsString("NaN") => Double.NaN
        case x => deserializationError(s"Expected Double as {$$numberDouble: <value>}, but got $x")
      }
      case JsNumber(x) => x.floatValue
      case JsString(x) => x.toFloat
      case x => deserializationError(s"Expected Double as JsNumber/JsString, but got $x")
    }
  }

  // https://docs.mongodb.com/manual/core/shell-types/#numberdecimal
  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#numberdecimal
  override implicit def BigDecimalJsonFormat: JsonFormat[BigDecimal] = new JsonFormat[BigDecimal] {

    override def write(obj: BigDecimal): JsValue = JsObject("$numberDecimal" -> JsString(obj.toString()))

    override def read(jsValue: JsValue): BigDecimal = jsValue match {
      case JsNumber(v) => v
      case JsString(v) => BigDecimal(v)
      case JsObject(fields) => fields("$numberDecimal") match {
        case JsString(v) => BigDecimal(v)
        case x => deserializationError(s"Expected BigDecimal as {$$numberDecimal: <value>}, but got $x")
      }
      case x => deserializationError(s"Expected BigDecimal as JsString, but got $x")
    }


  }

  // https://docs.mongodb.com/manual/reference/bson-types/#date
  // https://docs.mongodb.com/upcoming/reference/mongodb-extended-json/#mongodb-bsontype-Date
  override implicit def ZdtJsonFormat: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime] with ZonedDateTimeOps {

    def write(obj: ZonedDateTime): JsValue = obj match {
      case _ => JsObject("$date" -> JsString(obj.format(DateTimeIsoPattern)))
    }

    def read(jsValue: JsValue): ZonedDateTime = jsValue match {
      case JsObject(fields) => fields("$date") match {
        case JsNumber(v) => Instant.ofEpochMilli(v.toLong).atZone(ZoneOffset.UTC)
        // {"$date": "<ISO-8601 Date/Time Format>"} e.g. 1970-01-01T01:02:03+04:00
        case JsString(zdt) if zdt.length >= 20 => parseDateTimeIso (zdt)
        case x => deserializationError(s"Expected ZonedDateTime as {$$date: <value>}, but got $x")
      }
      case x => deserializationError(s"Expected ZonedDateTime as {$$date: <value>}, but got $x")
    }
  }

  // https://docs.mongodb.com/manual/reference/bson-types/#objectid
  // https://docs.mongodb.com/manual/reference/mongodb-extended-json/#oid
  override implicit def ObjectIdJsonFormat: JsonFormat[ObjectId] = new JsonFormat[ObjectId] {
    def write(obj: ObjectId): JsValue = JsObject("$oid" -> JsString(obj.toString))

    def read(jsValue: JsValue): ObjectId = jsValue match {
      case JsObject(fields) => fields("$oid") match {
        case JsString(oid) => new ObjectId(oid)
        case x => deserializationError(s"Expected ObjectId as {$$oid: <value>}, but got $x")
      }
      case x => deserializationError(s"Expected ObjectId as {$$oid: <value>}, but got $x")
    }
  }

  implicit def RegexJsonFormat: JsonFormat[Regex] = new JsonFormat[Regex] {

    override def write(obj: Regex): JsValue = JsObject("$regex" -> JsString(obj.toString))

    override def read(jsValue: JsValue): Regex = jsValue match {
      case JsObject(fields) => fields("$regex") match {
        case JsString(v) => v.r
        case x => deserializationError(s"Expected Regex as {$$regex: <value>}, but got $x")
      }
      case x => deserializationError(s"Expected Regex as {$$regex: <value>}, but got $x")
    }

  }
}

object GreenLeafBsonProtocol extends GreenLeafBsonProtocol
