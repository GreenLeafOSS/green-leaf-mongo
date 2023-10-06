package io.github.greenleafoss.mongo.core.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

trait LocalDateTimeOps:

  val LocalDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)

  extension (ldt: LocalDateTime)
    def toEpochMilli(zone: ZoneOffset = ZoneOffset.UTC): Long = ldt.toInstant(zone).toEpochMilli
    def printLocalDateTime: String                            = ldt.format(LocalDateTimeFormatter)

  extension (string: String) def parseLocalDateTime: LocalDateTime = LocalDateTime.parse(string, LocalDateTimeFormatter)

  extension (millis: Long)
    def asLocalDateTime(zone: ZoneOffset = ZoneOffset.UTC): LocalDateTime =
      Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime

object LocalDateTimeOps extends LocalDateTimeOps
