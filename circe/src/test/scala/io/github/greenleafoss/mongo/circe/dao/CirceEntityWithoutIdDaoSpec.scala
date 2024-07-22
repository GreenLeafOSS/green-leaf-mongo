package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.*
import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.EventSource.EventSource

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.generic.semiauto.deriveCodec

class CirceEntityWithoutIdDaoSpec extends EntityWithoutIdDaoSpec:

  private trait CirceEventDaoBsonProtocol extends EventDaoBsonProtocol with CirceMongoDaoProtocolObjectId[Event]:
    override given EventSourceFormat: JsonFormat[EventSource.EventSource] =
      Codec.from(
        Decoder.decodeString.map(EventSource.withName),
        Encoder.encodeString.contramap(_.toString)
      )

    override given eFormat: JsonFormat[Event] = deriveCodec

  private class CirceEventDao extends EventDao with CirceEventDaoBsonProtocol

  override protected def newEventDao: EventDao = CirceEventDao()
