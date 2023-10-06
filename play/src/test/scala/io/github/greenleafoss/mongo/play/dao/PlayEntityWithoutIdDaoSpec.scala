package io.github.greenleafoss.mongo.play.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.*
import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.EventSource.EventSource

import play.api.libs.json.Json

class PlayEntityWithoutIdDaoSpec extends EntityWithoutIdDaoSpec:

  private trait PlayEventDaoBsonProtocol extends EventDaoBsonProtocol with PlayMongoDaoProtocolObjectId[Event]:
    override given EventSourceFormat: JsonFormat[EventSource.EventSource] = Json.formatEnum(EventSource)
    override given eFormat: JsonFormat[Event]                             = Json.format[Event]

  private class PlayEventDao extends EventDao with PlayEventDaoBsonProtocol

  override protected def newEventDao: EventDao = PlayEventDao()
