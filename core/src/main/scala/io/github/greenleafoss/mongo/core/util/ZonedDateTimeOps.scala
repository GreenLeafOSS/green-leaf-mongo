package io.github.greenleafoss.mongo.core.util

import java.time._
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

trait ZonedDateTimeOps:

  val ZonedDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)

  extension (zdt: ZonedDateTime)
    def toEpochMilli: Long         = zdt.toInstant.toEpochMilli
    def printZonedDateTime: String = zdt.format(ZonedDateTimeFormatter)

  extension (string: String) def parseZonedDateTime: ZonedDateTime = ZonedDateTime.parse(string, ZonedDateTimeFormatter)

  extension (millis: Long)
    def asZonedDateTime(zone: ZoneOffset = ZoneOffset.UTC): ZonedDateTime =
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), zone)

  def now(truncate: TemporalUnit = ChronoUnit.MILLIS): ZonedDateTime =
    ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(truncate)

object ZonedDateTimeOps extends ZonedDateTimeOps
