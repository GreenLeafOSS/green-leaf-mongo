package io.github.greenleafoss.mongo.play.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec.*
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec.Currency

import java.time.ZonedDateTime

import scala.util.Try

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import play.api.libs.json.given

class PlayEntityWithIdAsObjectDaoSpec extends EntityWithIdAsObjectDaoSpec:
  private trait PlayExchangeRateDaoBsonProtocol
    extends ExchangeRateDaoBsonProtocol
    with PlayMongoDaoProtocol[ExchangeRateId, ExchangeRate]:
    override given CurrencyFormat: JsonFormat[Currency.Currency] = Json.formatEnum(Currency)

    given CurrencyKeyReads: KeyReads[Currency.Currency]   = KeyReads(x => JsResult.fromTry(Try(Currency.withName(x))))
    given CurrencyKeyWrites: KeyWrites[Currency.Currency] = KeyWrites(_.toString)

    override given idFormat: JsonFormat[ExchangeRateId] = Json.format[ExchangeRateId]

    // override given eFormat: JsonFormat[ExchangeRate] = Json.format[ExchangeRate]
    override given eFormat: JsonFormat[ExchangeRate] = (
      (JsPath \ "_id").format[ExchangeRateId] and
        (JsPath \ "rates").format[Map[Currency.Currency, BigDecimal]] and
        (JsPath \ "updated").format[ZonedDateTime]
    )(ExchangeRate.apply, e => (e.id, e.rates, e.updated))

  private class PlayExchangeRateDao extends ExchangeRateDao with PlayExchangeRateDaoBsonProtocol

  override protected def newExchangeRateDao: ExchangeRateDao = PlayExchangeRateDao()
