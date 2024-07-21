package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsFieldDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsFieldDaoSpec.*

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder

class CirceEntityWithIdAsFieldDaoSpec extends EntityWithIdAsFieldDaoSpec:

  private trait PlayBuildingModelBsonProtocol
    extends BuildingModelBsonProtocol
    with CirceMongoDaoProtocol[Long, Building]:
    override given idFormat: JsonFormat[Long]    = formatLong
    override given eFormat: JsonFormat[Building] =
      Codec.from(
        Decoder.forProduct6("_id", "name", "height", "floors", "year", "address")(Building.apply),
        Encoder.forProduct6("_id", "name", "height", "floors", "year", "address")(b =>
          (b.id, b.name, b.height, b.floors, b.year, b.address)
        )
      )

  private class PlayBuildingDao extends BuildingDao with PlayBuildingModelBsonProtocol

  override protected def newBuildingDao: BuildingDao = PlayBuildingDao()
