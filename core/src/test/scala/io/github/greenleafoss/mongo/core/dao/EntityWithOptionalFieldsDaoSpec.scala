package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.mongo.TestMongoServer
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.bson.BsonNull
import org.mongodb.scala.bson.BsonValue

import scala.concurrent.Future
import scala.language.implicitConversions

object EntityWithOptionalFieldsDaoSpec:

  // **************************************************
  // MODELS
  // http://www.geonames.org
  // **************************************************

  final case class GeoKey(country: String, state: Option[String], city: Option[String])
  object GeoKeyOps:
    def apply(country: String): GeoKey                              = GeoKey(country, None, None)
    def apply(country: String, state: String): GeoKey               = GeoKey(country, Some(state), None)
    def apply(country: String, state: String, city: String): GeoKey = GeoKey(country, Some(state), Some(city))

  final case class GeoRecord(key: GeoKey, name: String, population: Int)

  // **************************************************
  // DAO BSON PROTOCOL
  // **************************************************

  trait GeoModelDaoBsonProtocol extends GreenLeafMongoDaoProtocol[GeoKey, GeoRecord]:
    this: GreenLeafMongoJsonBasicFormats with GreenLeafJsonBsonOps =>

  // **************************************************
  // DAO
  // **************************************************

  abstract class GeoModelDao extends TestGreenLeafMongoDao[GeoKey, GeoRecord]:

    this: GeoModelDaoBsonProtocol with GreenLeafMongoJsonBasicFormats with GreenLeafJsonBsonOps =>

    def findCountryBy(countryCode: String): Future[Option[GeoRecord]] =
      // will return all records with this countryCode
      // val filter = s"""{ "_id.country": $countryCode }""".parseBson

      // will not works because 'state' and 'city' fields may not exist or be nulls
      // val filter = s"""{ "_id": { "country": $countryCode } }""".parseBson

      findOne($and("_id.country" $eq countryCode, "_id.state" $eq BsonNull(), "_id.city" $eq BsonNull()))

    def findStateBy(countryCode: String, stateCode: String): Future[Option[GeoRecord]] =
      findOne($and("_id.country" $eq countryCode, "_id.state" $eq stateCode, "_id.city" $eq BsonNull()))

    def findCityBy(countryCode: String, stateCode: String, cityCode: String): Future[Option[GeoRecord]] =
      findOne($and("_id.country" $eq countryCode, "_id.state" $eq stateCode, "_id.city" $eq cityCode))

abstract class EntityWithOptionalFieldsDaoSpec extends TestMongoServer:

  import EntityWithOptionalFieldsDaoSpec.*

  protected def newGeoModelDao: GeoModelDao

  protected val GeoRecords: Seq[GeoRecord] = Seq(
    GeoRecord(GeoKeyOps("6252001"), "United States of America", 310232863),
    GeoRecord(GeoKeyOps("6252001", "5128638"), "New York", 19274244),
    GeoRecord(GeoKeyOps("6252001", "5128638", "5128581"), "New York City", 8175133),
    GeoRecord(GeoKeyOps("6252001", "5128638", "5133273"), "Queens", 2272771),
    GeoRecord(GeoKeyOps("6252001", "5128638", "5110302"), "Brooklyn", 2300664),
    GeoRecord(GeoKeyOps("6252001", "5101760"), "New Jersey", 8751436),
    GeoRecord(GeoKeyOps("6252001", "5101760", "5099836"), "Jersey City", 264290),
    GeoRecord(GeoKeyOps("6252001", "5101760", "5099133"), "Hoboken", 53635),
    GeoRecord(GeoKeyOps("6252001", "5332921"), "California", 37691912),
    GeoRecord(GeoKeyOps("6252001", "5332921", "5391959"), "San Francisco", 864816),
    GeoRecord(GeoKeyOps("146669"), "Republic of Cyprus", 1102677),
    GeoRecord(GeoKeyOps("2921044"), "Federal Republic of Germany", 81802257),
    GeoRecord(GeoKeyOps("2658434"), "Switzerland", 8484100),
    GeoRecord(GeoKeyOps("294640"), "State of Israel", 7353985),
    GeoRecord(GeoKeyOps("2635167"), "United Kingdom of Great Britain and Northern Ireland", 62348447),
    GeoRecord(GeoKeyOps("2750405"), "Kingdom of the Netherlands", 16645000),
    GeoRecord(GeoKeyOps("2661886"), "Kingdom of Sweden", 9828655),
    GeoRecord(GeoKeyOps("732800"), "Republic of Bulgaria", 7148785),
    GeoRecord(GeoKeyOps("719819"), "Hungary", 9982000),
    GeoRecord(GeoKeyOps("3017382"), "Republic of France", 64768389),
    GeoRecord(GeoKeyOps("798544"), "Republic of Poland", 38500000),
    GeoRecord(GeoKeyOps("690791"), "Ukraine", 45415596)
  )

  "GeoModelDao" should:

    "findCountryBy" in:
      val dao = newGeoModelDao
      for
        insertRes <- dao.insertMany(GeoRecords)

        // an explicit query {"_id.country": ... }
        usaByCode <- dao.findCountryBy("6252001")
        // implicit query from GeoKeyOps model - will be the same as the explicit query above
        usaByKey  <- dao.findById(GeoKeyOps("6252001"))

        cyprusByCode <- dao.findCountryBy("146669")
        cyprusByKey  <- dao.findById(GeoKeyOps("146669"))

        uaByCode <- dao.findCountryBy("690791")
        uaByKey  <- dao.findById(GeoKeyOps("690791"))
      yield
        insertRes.getInsertedIds should not be empty

        usaByCode shouldBe Some(GeoRecord(GeoKeyOps("6252001"), "United States of America", 310232863))
        usaByKey shouldBe Some(GeoRecord(GeoKeyOps("6252001"), "United States of America", 310232863))

        cyprusByCode shouldBe Some(GeoRecord(GeoKeyOps("146669"), "Republic of Cyprus", 1102677))
        cyprusByKey shouldBe Some(GeoRecord(GeoKeyOps("146669"), "Republic of Cyprus", 1102677))

        uaByCode shouldBe Some(GeoRecord(GeoKeyOps("690791"), "Ukraine", 45415596))
        uaByKey shouldBe Some(GeoRecord(GeoKeyOps("690791"), "Ukraine", 45415596))

    "findStateBy" in:
      val dao = newGeoModelDao
      for
        insertRes <- dao.insertMany(GeoRecords)

        nyByCode <- dao.findStateBy("6252001", "5128638")
        nyByKey  <- dao.findById(GeoKeyOps("6252001", "5128638"))

        njByCode <- dao.findStateBy("6252001", "5101760")
        njByKey  <- dao.findById(GeoKeyOps("6252001", "5101760"))

        caByCode <- dao.findStateBy("6252001", "5332921")
        caByKey  <- dao.findById(GeoKeyOps("6252001", "5332921"))
      yield
        insertRes.getInsertedIds should not be empty

        nyByCode shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5128638"), "New York", 19274244))
        nyByKey shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5128638"), "New York", 19274244))

        njByCode shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5101760"), "New Jersey", 8751436))
        njByKey shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5101760"), "New Jersey", 8751436))

        caByCode shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5332921"), "California", 37691912))
        caByKey shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5332921"), "California", 37691912))

    "findCityBy" in:
      val dao = newGeoModelDao
      for
        insertRes <- dao.insertMany(GeoRecords)

        nycByCode <- dao.findCityBy("6252001", "5128638", "5128581")
        nycByKey  <- dao.findById(GeoKeyOps("6252001", "5128638", "5128581"))

        hbkByCode <- dao.findCityBy("6252001", "5101760", "5099133")
        hbkByKey  <- dao.findById(GeoKeyOps("6252001", "5101760", "5099133"))
      yield
        insertRes.getInsertedIds should not be empty

        nycByCode shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5128638", "5128581"), "New York City", 8175133))
        nycByKey shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5128638", "5128581"), "New York City", 8175133))

        hbkByCode shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5101760", "5099133"), "Hoboken", 53635))
        hbkByKey shouldBe Some(GeoRecord(GeoKeyOps("6252001", "5101760", "5099133"), "Hoboken", 53635))
