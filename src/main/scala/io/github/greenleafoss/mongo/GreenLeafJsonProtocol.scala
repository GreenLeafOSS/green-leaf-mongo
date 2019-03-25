package io.github.greenleafoss.mongo

import java.time.ZonedDateTime
import java.util.UUID

import org.mongodb.scala.bson.ObjectId
import spray.json.{AdditionalFormats, CollectionFormats, DefaultJsonProtocol, JsBoolean, JsFalse, JsNull, JsNumber, JsString, JsTrue, JsValue, JsonFormat, ProductFormats, StandardFormats, deserializationError}

trait GreenLeafJsonProtocol
  extends StandardFormats
  with CollectionFormats
  with ProductFormats
  with AdditionalFormats {

  implicit def IntJsonFormat: JsonFormat[Int] = new JsonFormat[Int] {
    def write(x: Int): JsNumber = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): Int = value match {
      case JsNumber(x) => x.intValue
      case JsString.empty => 0
      case JsString(x) => x.toInt
      case x => deserializationError("Expected Int as JsNumber/JsString, but got " + x)
    }
  }

  implicit def LongJsonFormat: JsonFormat[Long] = new JsonFormat[Long] {
    def write(x: Long): JsNumber = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): Long = value match {
      case JsNumber(x) => x.longValue
      case JsString.empty => 0L
      case JsString(x) => x.toLong
      case x => deserializationError("Expected Long as JsNumber/JsString, but got " + x)
    }
  }

  implicit def FloatJsonFormat: JsonFormat[Float] = new JsonFormat[Float] {
    def write(x: Float): JsValue = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): Float = value match {
      case JsNumber(x) => x.floatValue
      case JsString.empty => 0f
      case JsString(x) => x.toFloat
      case JsNull      => Float.NaN
      case x => deserializationError("Expected Float as JsNumber/JsString, but got " + x)
    }
  }

  implicit def DoubleJsonFormat: JsonFormat[Double] = new JsonFormat[Double] {
    def write(x: Double): JsValue = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): Double = value match {
      case JsNumber(x) => x.doubleValue
      case JsString.empty => 0d
      case JsString(x) => x.toDouble
      case JsNull      => Double.NaN
      case x => deserializationError("Expected Double as JsNumber/JsString, but got " + x)
    }
  }

  implicit def ByteJsonFormat: JsonFormat[Byte] = new JsonFormat[Byte] {
    def write(x: Byte): JsNumber = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): Byte = value match {
      case JsNumber(x) => x.byteValue
      case JsString.empty => 0.toByte
      case JsString(x) => x.toByte
      case x => deserializationError("Expected Byte as JsNumber/JsString, but got " + x)
    }
  }

  implicit def ShortJsonFormat: JsonFormat[Short] = new JsonFormat[Short] {
    def write(x: Short): JsNumber = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): Short = value match {
      case JsNumber(x) => x.shortValue
      case JsString.empty => 0.toShort
      case JsString(x) => x.toShort
      case x => deserializationError("Expected Short as JsNumber/JsString, but got " + x)
    }
  }

  implicit def BigDecimalJsonFormat: JsonFormat[BigDecimal] = new JsonFormat[BigDecimal] {
    def write(x: BigDecimal): JsNumber = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): BigDecimal = value match {
      case JsNumber(x) => x
      case JsString.empty => BigDecimal(0)
      case JsString(x) => BigDecimal(x)
      case x => deserializationError("Expected BigDecimal as JsNumber/JsString, but got " + x)
    }
  }

  implicit def BigIntJsonFormat: JsonFormat[BigInt] = new JsonFormat[BigInt] {
    def write(x: BigInt): JsNumber = {
      require(x ne null)
      JsNumber(x)
    }

    def read(value: JsValue): BigInt = value match {
      case JsNumber(x) => x.toBigInt
      case JsString.empty => BigInt(0)
      case JsString(x) => BigInt(x)
      case x => deserializationError("Expected BigInt as JsNumber/JsString, but got " + x)
    }
  }

  implicit def UnitJsonFormat: JsonFormat[Unit] = DefaultJsonProtocol.UnitJsonFormat

  implicit def BooleanJsonFormat: JsonFormat[Boolean] = new JsonFormat[Boolean] {
    def write(x: Boolean): JsBoolean = {
      require(x ne null)
      JsBoolean(x)
    }

    def read(value: JsValue): Boolean = value match {
      case JsTrue => true
      case JsFalse => false
      case JsString(x) => java.lang.Boolean.parseBoolean(x)
      case x => deserializationError("Expected JsBoolean/JsString, but got " + x)
    }
  }

  implicit def CharJsonFormat: JsonFormat[Char] = DefaultJsonProtocol.CharJsonFormat

  implicit def StringJsonFormat: JsonFormat[String] = DefaultJsonProtocol.StringJsonFormat

  implicit def SymbolJsonFormat: JsonFormat[Symbol] = DefaultJsonProtocol.SymbolJsonFormat

  implicit def ZdtJsonFormat: JsonFormat[ZonedDateTime] = new JsonFormat[ZonedDateTime] with ZonedDateTimeOps {
    def write(obj: ZonedDateTime): JsValue = JsString(obj.format(DateTimePattern))

    def read(jsValue: JsValue): ZonedDateTime = jsValue match {
      case JsString(zdt) if zdt.length >= 20 => parseDateTimeIso (zdt) // 1970-01-01T01:02:03+04:00
      case JsString(zdt) if zdt.length >= 19 && zdt.contains('T') => parseDateTimeIso(zdt) // 1970-01-01T00:00:00
      case JsString(zdt) if zdt.length == 19 => parseDateTime (zdt) // 1970-01-01 00:00:00
      case JsString(zdt) => parseDate (zdt)
      case x => deserializationError(s"Expected ZonedDateTime, but got $x")
    }
  }

  implicit def ObjectIdJsonFormat: JsonFormat[ObjectId] = new JsonFormat[ObjectId] {
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
