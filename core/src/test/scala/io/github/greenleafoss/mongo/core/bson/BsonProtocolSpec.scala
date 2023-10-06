package io.github.greenleafoss.mongo.core.bson

import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.model.BasicFormats
import io.github.greenleafoss.mongo.core.model.Model
import io.github.greenleafoss.mongo.core.model.Models
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

trait BsonProtocolSpec extends BsonFormatSpec with BasicFormats:
  this: GreenLeafJsonBsonOps with GreenLeafMongoJsonBasicFormats =>

  given modelJsonFormat: JsonFormat[Model]

  "BSON protocol" should behave like bsonFormat(Models.default, Models.defaultBson)

  it should behave like bsonFormat(BooleanVal, BooleanBson)

  it should behave like bsonFormat(IntVal, IntBson)

  it should behave like bsonFormat(LongVal, LongBson)
  // case when we need to deserialize { $numberInt: "123" } as Long
  it should behave like bsonFormat(LongFromIntVal, LongFromIntBson, LongFromIntBsonRead)

  it should behave like bsonFormat(FloatVal, FloatBson)

  it should behave like bsonFormat(DoubleVal, DoubleBson)

  // case when we need to deserialize { $numberDecimal: "1.23" } as BigDecimal
  it should behave like bsonFormat(BigDecimalVal, BigDecimalBson)
  // case when we need to deserialize { $numberInt: "123" } as BigDecimal
  it should behave like bsonFormat(BigDecimalFromIntVal, BigDecimalFromIntBson, BigDecimalFromIntBsonRead)
  // case when we need to deserialize { $numberLong: "123" } as BigDecimal
  it should behave like bsonFormat(BigDecimalFromLongVal, BigDecimalFromLongBson, BigDecimalFromLongBsonRead)
  // case when we need to deserialize { $numberDouble: "123.0" } as BigDecimal
  it should behave like bsonFormat(BigDecimalFromDouble, BigDecimalFromDoubleBson, BigDecimalFromDoubleBsonRead)

  it should behave like bsonFormat(LocalDateVal, LocalDateBson)

  it should behave like bsonFormat(LocalDateTimeVal, LocalDateTimeBson)

  it should behave like bsonFormat(ZonedDateTimeVal, ZonedDateTimeBson)

  it should behave like bsonFormat(ObjectIdVal, ObjectIdBson)
