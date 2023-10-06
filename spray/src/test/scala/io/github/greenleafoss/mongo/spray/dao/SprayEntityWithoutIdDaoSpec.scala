package io.github.greenleafoss.mongo.spray.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.*
import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.EventSource.EventSource
import io.github.greenleafoss.mongo.spray.json.SprayJsonProtocol

import spray.json.*

class SprayEntityWithoutIdDaoSpec extends EntityWithoutIdDaoSpec:

  // Let's suppose we already have some JsonFormats for these models
  private trait SprayEventDaoJsonProtocol extends SprayJsonProtocol:
    given EventSourceFormat: JsonFormat[EventSource.EventSource] = enumToJsonFormatAsString(EventSource)
    given ModelEventFormat: JsonFormat[Event]                    = DefaultJsonProtocol.jsonFormat4(Event.apply)

  // so we can reuse it and just mix SprayMongoDaoProtocolObjectId
  private trait SprayEventDaoBsonProtocol
    extends SprayEventDaoJsonProtocol
    with EventDaoBsonProtocol
    with SprayMongoDaoProtocolObjectId[Event]:
    override given eFormat: JsonFormat[Event] = ModelEventFormat

  private class SprayEventDao extends EventDao with SprayEventDaoBsonProtocol

  override protected def newEventDao: EventDao = SprayEventDao()
