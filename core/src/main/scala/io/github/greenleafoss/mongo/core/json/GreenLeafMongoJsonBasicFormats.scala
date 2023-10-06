package io.github.greenleafoss.mongo.core.json

import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

trait GreenLeafMongoJsonBasicFormats:
  this: GreenLeafJsonBsonOps =>

  // INT

  // protected val $numberInt: String = "$numberInt"
  protected def formatInt: JsonFormat[Int]
  given IntJsonFormat: JsonFormat[Int] = formatInt

  // LONG

  // protected val $numberLong: String = "$numberLong"
  protected def formatLong: JsonFormat[Long]
  given LongJsonFormat: JsonFormat[Long] = formatLong

  // FLOAT

  protected def formatFloat: JsonFormat[Float]
  given FloatJsonFormat: JsonFormat[Float] = formatFloat

  // DOUBLE

  // protected val $numberDouble: String = "$numberDouble"
  protected def formatDouble: JsonFormat[Double]
  given DoubleJsonFormat: JsonFormat[Double] = formatDouble

  // BYTE

  protected def formatByte: JsonFormat[Byte]
  given ByteJsonFormat: JsonFormat[Byte] = formatByte

  // SHORT

  protected def formatShort: JsonFormat[Short]
  given ShortJsonFormat: JsonFormat[Short] = formatShort

  // BIG DECIMAL

  // protected val $numberDecimal: String = "$numberDecimal"

  protected def formatBigDecimal: JsonFormat[BigDecimal]
  given BigDecimalJsonFormat: JsonFormat[BigDecimal] = formatBigDecimal

  // BIG INT

  protected def formatBigInt: JsonFormat[BigInt]
  given BigIntJsonFormat: JsonFormat[BigInt] = formatBigInt

  // UNIT

  protected def formatUnit: JsonFormat[Unit]
  given UnitJsonFormat: JsonFormat[Unit] = formatUnit

  // BOOLEAN

  protected def formatBoolean: JsonFormat[Boolean]
  given BooleanJsonFormat: JsonFormat[Boolean] = formatBoolean

  // CHAR

  protected def formatChar: JsonFormat[Char]
  given CharJsonFormat: JsonFormat[Char] = formatChar

  // STRING

  protected def formatString: JsonFormat[String]
  given StringJsonFormat: JsonFormat[String] = formatString

  // SYMBOL

  protected def formatSymbol: JsonFormat[Symbol]
  given SymbolJsonFormat: JsonFormat[Symbol] = formatSymbol

  // LOCAL DATE

  // protected val $date: String = "$date"
  protected def formatLocalDate: JsonFormat[LocalDate]
  given LocalDateJsonFormat: JsonFormat[LocalDate] = formatLocalDate

  // LOCAL DATE TIME

  protected def formatLocalDateTime: JsonFormat[LocalDateTime]
  given LocalDateTimeJsonFormat: JsonFormat[LocalDateTime] = formatLocalDateTime

  // ZONED DATE TIME

  protected def formatZonedDateTime: JsonFormat[ZonedDateTime]
  given ZonedDateTimeJsonFormat: JsonFormat[ZonedDateTime] = formatZonedDateTime

  // UUID

  // protected val $oid: String = "$oid"
  protected def formatUUID: JsonFormat[UUID]
  given UUIDJsonFormat: JsonFormat[UUID] = formatUUID

  // OBJECT ID

  // protected val $regularExpression: String = "$regularExpression"
  protected def formatObjectId: JsonFormat[ObjectId]
  given ObjectIdJsonFormat: JsonFormat[ObjectId] = formatObjectId
