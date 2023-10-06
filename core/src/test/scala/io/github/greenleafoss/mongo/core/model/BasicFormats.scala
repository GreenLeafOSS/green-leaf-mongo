package io.github.greenleafoss.mongo.core.model

import io.github.greenleafoss.mongo.core.util.LocalDateOps.*
import io.github.greenleafoss.mongo.core.util.LocalDateTimeOps.*
import io.github.greenleafoss.mongo.core.util.MongoExtendedJsonOps
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps.*

import org.mongodb.scala.bson.ObjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

trait BasicFormats extends MongoExtendedJsonOps:

  val BooleanVal: Boolean = true
  val BooleanJson: String = "true"
  val BooleanBson: String = "true"

  val IntVal: Int     = 127
  val IntJson: String = "127"
  val IntBson: String = s"""{ "${$numberInt}": "127" }"""

  val LongVal: Long    = 1234567890L
  val LongJson: String = "1234567890"
  val LongBson: String = s"""{ "${$numberLong}": "1234567890" }"""

  val LongFromIntVal: Long        = 1234567890L
  val LongFromIntBson: String     = s"""{ "${$numberLong}": "1234567890" }"""
  val LongFromIntBsonRead: String = s"""{ "${$numberInt}": "1234567890" }"""

  val FloatVal: Float   = 2.7f
  val FloatJson: String = "2.7"
  val FloatBson: String = s"""{ "${$numberDouble}": "2.7" }"""

  val DoubleVal: Double  = 3.1415d
  val DoubleJson: String = "3.1415"
  val DoubleBson: String = s"""{ "${$numberDouble}": "3.1415" }"""

  val BigDecimalVal: BigDecimal = BigDecimal("3.1415926535897932384626")
  val BigDecimalJson: String    = "3.1415926535897932384626"
  val BigDecimalBson: String    = s"""{ "${$numberDecimal}": "3.1415926535897932384626" }"""

  val BigDecimalFromIntVal: BigDecimal  = BigDecimal("1024")
  val BigDecimalFromIntBson: String     = s"""{ "${$numberDecimal}": "1024" }"""
  val BigDecimalFromIntBsonRead: String = s"""{ "${$numberInt}": "1024" }"""

  val BigDecimalFromLongVal: BigDecimal  = BigDecimal("512")
  val BigDecimalFromLongBson: String     = s"""{ "${$numberDecimal}": "512" }"""
  val BigDecimalFromLongBsonRead: String = s"""{ "${$numberLong}": "512" }"""

  val BigDecimalFromDouble: BigDecimal     = BigDecimal("2.56")
  val BigDecimalFromDoubleBson: String     = s"""{ "${$numberDecimal}": "2.56" }"""
  val BigDecimalFromDoubleBsonRead: String = s"""{ "${$numberDouble}": "2.56" }"""

  val LocalDateVal: LocalDate = "1970-01-01".parseLocalDate
  val LocalDateJson: String   = s"\"$LocalDateVal\""
  val LocalDateBson: String   = s"""{ "${$date}": { "${$numberLong}": "0" } }"""

  val LocalDateTimeVal: LocalDateTime = "1970-01-01T00:00:01".parseLocalDateTime
  val LocalDateTimeJson: String       = s"\"$LocalDateTimeVal\""
  val LocalDateTimeBson: String       = s"""{ "${$date}": { "${$numberLong}": "1000" } }"""

  val ZonedDateTimeVal: ZonedDateTime = "1970-01-01T00:00:10Z".parseZonedDateTime
  val ZonedDateTimeJson: String       = s"\"$ZonedDateTimeVal\""
  val ZonedDateTimeBson: String       = s"""{ "${$date}": { "${$numberLong}": "10000" } }"""

  val ObjectIdVal: ObjectId = new ObjectId("651897e08d0568496e7e4b96")
  val ObjectIdJson: String  = "\"651897e08d0568496e7e4b96\""
  val ObjectIdBson: String  = s"""{ "${$oid}": "651897e08d0568496e7e4b96" }"""

object BasicFormats extends BasicFormats
