package io.github.greenleafoss.mongo.spray.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec.*
import io.github.greenleafoss.mongo.core.dao.EntityWithIdAsObjectDaoSpec.Currency.Currency

import spray.json.*
import spray.json.DefaultJsonProtocol.*

class SprayEntityWithIdAsObjectDaoSpec extends EntityWithIdAsObjectDaoSpec:
  private trait SprayExchangeRateDaoBsonProtocol
    extends ExchangeRateDaoBsonProtocol
    with SprayMongoDaoProtocol[ExchangeRateId, ExchangeRate]:
    override given CurrencyFormat: JsonFormat[Currency.Currency] = enumToJsonFormatAsString(Currency)

    override given idFormat: JsonFormat[ExchangeRateId] = jsonFormat2(ExchangeRateId.apply)

    override given eFormat: JsonFormat[ExchangeRate] = jsonFormat(ExchangeRate.apply, "_id", "rates", "updated")

  private class SprayExchangeRateDao extends ExchangeRateDao with SprayExchangeRateDaoBsonProtocol

  override protected def newExchangeRateDao: ExchangeRateDao = SprayExchangeRateDao()
