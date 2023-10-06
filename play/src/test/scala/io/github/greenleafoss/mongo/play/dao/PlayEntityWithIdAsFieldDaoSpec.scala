package io.github.greenleafoss.mongo.play.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsFieldDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsFieldDaoSpec.*

import play.api.libs.functional.syntax.*
import play.api.libs.functional.syntax.given
import play.api.libs.json.*

class PlayEntityWithIdAsFieldDaoSpec extends EntityWithIdAsFieldDaoSpec:

  private trait PlayBuildingModelBsonProtocol
    extends BuildingModelBsonProtocol
    with PlayMongoDaoProtocol[Long, Building]:
    override given idFormat: JsonFormat[Long]    = formatLong
    override given eFormat: JsonFormat[Building] = (
      (JsPath \ "_id").format[Long] and
        (JsPath \ "name").format[String] and
        (JsPath \ "height").format[Int] and
        (JsPath \ "floors").format[Int] and
        (JsPath \ "year").format[Int] and
        (JsPath \ "address").format[String]
        // )(Building.apply, unlift(Building.unapply))
    )(Building.apply, b => (b.id, b.name, b.height, b.floors, b.year, b.address))

  private class PlayBuildingDao extends BuildingDao with PlayBuildingModelBsonProtocol

  override protected def newBuildingDao: BuildingDao = PlayBuildingDao()
