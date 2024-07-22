package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec.*
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec.Currency

import java.time.ZonedDateTime

import scala.util.Try

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.KeyDecoder
import io.circe.KeyEncoder
import io.circe.generic.semiauto.deriveCodec

class CirceEntityWithIdAsObjectDaoSpec extends EntityWithIdAsObjectDaoSpec:
  private trait PlayExchangeRateDaoBsonProtocol
    extends ExchangeRateDaoBsonProtocol
    with CirceMongoDaoProtocol[ExchangeRateId, ExchangeRate]:
    override given CurrencyFormat: JsonFormat[Currency.Currency] =
      Codec.from(
        Decoder.decodeString.map(Currency.withName),
        Encoder.encodeString.contramap(_.toString)
      )

    given keyDecoder: KeyDecoder[Currency.Currency] =
      KeyDecoder.instance(x => Try(Currency.withName(x)).toOption)

    given keyEncoder: KeyEncoder[Currency.Currency] =
      KeyEncoder[String].contramap(_.toString)

    override given idFormat: JsonFormat[ExchangeRateId] = deriveCodec

    // override given eFormat: JsonFormat[ExchangeRate] = deriveCodec
    override given eFormat: JsonFormat[ExchangeRate] =
      Codec.from(
        Decoder.forProduct3("_id", "rates", "updated")(ExchangeRate.apply),
        Encoder.forProduct3("_id", "rates", "updated")(x => (x.id, x.rates, x.updated))
      )

  private class PlayExchangeRateDao extends ExchangeRateDao with PlayExchangeRateDaoBsonProtocol

  override protected def newExchangeRateDao: ExchangeRateDao = PlayExchangeRateDao()
