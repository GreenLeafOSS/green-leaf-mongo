package io.github.greenleafoss.mongo.core.json

import io.github.greenleafoss.mongo.core.model.BasicFormats
import io.github.greenleafoss.mongo.core.model.Model
import io.github.greenleafoss.mongo.core.model.Models
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

trait JsonProtocolSpec extends JsonFormatSpec:
  this: GreenLeafJsonBsonOps with GreenLeafMongoJsonBasicFormats =>
  given modelJsonFormat: JsonFormat[Model]

  "JSON protocol" should behave like jsonFormat(Models.default, Models.defaultJson)
  it should behave like jsonFormat(BasicFormats.IntVal, BasicFormats.IntJson)
  it should behave like jsonFormat(BasicFormats.LongVal, BasicFormats.LongJson)
  it should behave like jsonFormat(BasicFormats.FloatVal, BasicFormats.FloatJson)
  it should behave like jsonFormat(BasicFormats.DoubleVal, BasicFormats.DoubleJson)
  it should behave like jsonFormat(BasicFormats.BigDecimalVal, BasicFormats.BigDecimalJson)
  it should behave like jsonFormat(BasicFormats.LocalDateVal, BasicFormats.LocalDateJson)
  it should behave like jsonFormat(BasicFormats.LocalDateTimeVal, BasicFormats.LocalDateTimeJson)
  it should behave like jsonFormat(BasicFormats.ZonedDateTimeVal, BasicFormats.ZonedDateTimeJson)
  it should behave like jsonFormat(BasicFormats.BooleanVal, BasicFormats.BooleanJson)
  it should behave like jsonFormat(BasicFormats.ObjectIdVal, BasicFormats.ObjectIdJson)
