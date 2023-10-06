package io.github.greenleafoss.mongo.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

trait LocalDateOps:

  val LocalDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE.withZone(ZoneOffset.UTC)

  extension (ld: LocalDate)
    def toEpochMilli(zone: ZoneOffset = ZoneOffset.UTC): Long = ld.atStartOfDay(zone).toInstant.toEpochMilli
    def printLocalDate: String                                = ld.format(LocalDateFormatter)

  extension (string: String) def parseLocalDate: LocalDate = LocalDate.parse(string, LocalDateFormatter)

  extension (millis: Long)
    def asLocalDate(zone: ZoneOffset = ZoneOffset.UTC): LocalDate =
      Instant.ofEpochMilli(millis).atZone(zone).toLocalDate

object LocalDateOps extends LocalDateOps
