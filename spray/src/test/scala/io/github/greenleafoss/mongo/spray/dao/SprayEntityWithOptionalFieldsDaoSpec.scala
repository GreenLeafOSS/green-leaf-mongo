package io.github.greenleafoss.mongo.spray.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithOptionalFieldsDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithOptionalFieldsDaoSpec.*

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol.given

class SprayEntityWithOptionalFieldsDaoSpec extends EntityWithOptionalFieldsDaoSpec:

  private trait SprayGeoModelDaoBsonProtocol
    extends GeoModelDaoBsonProtocol
    with SprayMongoDaoProtocol[GeoKey, GeoRecord]:
    override protected given idFormat: JsonFormat[GeoKey]   =
      DefaultJsonProtocol.jsonFormat3(GeoKey.apply)
    override protected given eFormat: JsonFormat[GeoRecord] =
      DefaultJsonProtocol.jsonFormat(GeoRecord.apply, "_id", "name", "population")

  private class SprayGeoModelDao extends GeoModelDao with SprayGeoModelDaoBsonProtocol
  override protected def newGeoModelDao: GeoModelDao = SprayGeoModelDao()
