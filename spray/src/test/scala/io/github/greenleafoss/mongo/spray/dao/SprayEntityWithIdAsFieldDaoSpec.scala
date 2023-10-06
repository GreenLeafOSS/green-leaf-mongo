package io.github.greenleafoss.mongo.spray.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsFieldDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsFieldDaoSpec.*

import spray.json.DefaultJsonProtocol

class SprayEntityWithIdAsFieldDaoSpec extends EntityWithIdAsFieldDaoSpec:
  private trait SprayBuildingModelBsonProtocol
    extends BuildingModelBsonProtocol
    with SprayMongoDaoProtocol[Long, Building]:
    override given idFormat: JsonFormat[Long]    = formatLong
    override given eFormat: JsonFormat[Building] =
      DefaultJsonProtocol.jsonFormat(Building.apply, "_id", "name", "height", "floors", "year", "address")

  private class SprayBuildingDao extends BuildingDao with SprayBuildingModelBsonProtocol

  override protected def newBuildingDao: BuildingDao = SprayBuildingDao()
