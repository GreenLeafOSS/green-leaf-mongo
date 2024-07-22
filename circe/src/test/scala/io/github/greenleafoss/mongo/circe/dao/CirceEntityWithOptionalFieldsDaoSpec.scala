package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithOptionalFieldsDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithOptionalFieldsDaoSpec.*

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveCodec

class CirceEntityWithOptionalFieldsDaoSpec extends EntityWithOptionalFieldsDaoSpec:

  private trait PlayGeoModelDaoBsonProtocol
    extends GeoModelDaoBsonProtocol
    with CirceMongoDaoProtocol[GeoKey, GeoRecord]:
    override protected given idFormat: JsonFormat[GeoKey]   = deriveCodec
    override protected given eFormat: JsonFormat[GeoRecord] =
      Codec.from(
        Decoder.forProduct3("_id", "name", "population")(GeoRecord.apply),
        // Encoder.forProduct3("_id", "name", "population")(Tuple.fromProductTyped)
        Encoder.forProduct3("_id", "name", "population")(x => (x.key, x.name, x.population))
      )

  private class PlayGeoModelDao extends GeoModelDao with PlayGeoModelDaoBsonProtocol
  override protected def newGeoModelDao: GeoModelDao = PlayGeoModelDao()
