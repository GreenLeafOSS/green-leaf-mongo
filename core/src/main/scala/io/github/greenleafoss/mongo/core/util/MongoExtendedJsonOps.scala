package io.github.greenleafoss.mongo.core.util

import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings

trait MongoExtendedJsonOps:
  // private val jws: JsonWriterSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()
  protected val jws: JsonWriterSettings = JsonWriterSettings.builder().outputMode(JsonMode.EXTENDED).build()

  protected val $date: String              = "$date"
  protected val $numberDecimal: String     = "$numberDecimal"
  protected val $numberDouble: String      = "$numberDouble"
  protected val $numberLong: String        = "$numberLong"
  protected val $numberInt: String         = "$numberInt"
  protected val $oid: String               = "$oid"
  protected val $regularExpression: String = "$regularExpression"
  // protected val $timestamp: String         = "$timestamp"
