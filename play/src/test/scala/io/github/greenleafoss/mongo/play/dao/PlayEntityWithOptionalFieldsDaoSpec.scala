package io.github.greenleafoss.mongo.play.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithOptionalFieldsDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithOptionalFieldsDaoSpec.*

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

class PlayEntityWithOptionalFieldsDaoSpec extends EntityWithOptionalFieldsDaoSpec:

  private trait PlayGeoModelDaoBsonProtocol
    extends GeoModelDaoBsonProtocol
    with PlayMongoDaoProtocol[GeoKey, GeoRecord]:
    override protected given idFormat: JsonFormat[GeoKey]   = Json.format[GeoKey]
    override protected given eFormat: JsonFormat[GeoRecord] = (
      // we want to use 'key' field as '_id'
      (JsPath \ "_id").format[GeoKey] and
        (JsPath \ "name").format[String] and
        (JsPath \ "population").format[Int]
    )(GeoRecord.apply, x => (x.key, x.name, x.population))

  private class PlayGeoModelDao extends GeoModelDao with PlayGeoModelDaoBsonProtocol
  override protected def newGeoModelDao: GeoModelDao = PlayGeoModelDao()
