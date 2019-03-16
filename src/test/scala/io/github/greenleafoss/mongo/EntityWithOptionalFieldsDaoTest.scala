package io.github.greenleafoss.mongo

import java.util.UUID

import GreenLeafMongoDao.DaoBsonProtocol
import org.mongodb.scala.{Completed, Document, MongoCollection}
import spray.json._

import scala.concurrent.Future
import scala.language.implicitConversions

object EntityWithOptionalFieldsDaoTest {
  object GeoModel {
    // http://www.geonames.org

    // MODEL
    case class GeoKey(country: String, state: Option[String] = None, city: Option[String] = None)
    case class GeoRecord(key: GeoKey, name: String, population: Int)

    object GeoKeyOps {
      def apply(country: String, state: String) = GeoKey(country, Some(state), None)
      def apply(country: String, state: String, city: String) = GeoKey(country, Some(state), Some(city))
    }

    // JSON
    trait GeoModelJsonProtocol extends GreenLeafJsonProtocol {
      implicit val GeoKeyFormat: RootJsonFormat[GeoKey] = jsonFormat3(GeoKey)
      implicit val GeoRecordFormat: RootJsonFormat[GeoRecord] = jsonFormat3(GeoRecord)
    }

    object GeoModelJsonProtocol extends GeoModelJsonProtocol

    // BSON
    trait GeoModelBsonProtocol extends GeoModelJsonProtocol with GreenLeafBsonProtocol {
      override implicit val GeoRecordFormat: RootJsonFormat[GeoRecord] = jsonFormat(
        GeoRecord, "_id", "name", "population")
    }

    object GeoModelBsonProtocol extends GeoModelBsonProtocol with DaoBsonProtocol[GeoKey, GeoRecord] {
      override implicit def idFormat: RootJsonFormat[GeoKey] = GeoKeyFormat
      override implicit def entityFormat: RootJsonFormat[GeoRecord] = GeoRecordFormat
    }
  }

  import GeoModel._

  class GeoModelDao(collectionName: String) extends TestGreenLeafMongoDao[GeoKey, GeoRecord] {

    override protected val collection: MongoCollection[Document] = db.getCollection(collectionName)

    override protected val protocol = GeoModelBsonProtocol
    import protocol._

    def findCountryBy(countryCode: String): Future[Option[GeoRecord]] = {
      // will return all records with this countryCode
      // val filter = Document(s"""{ "_id.country": $countryCode }""")

      // will not works because 'state' and 'city' fields may not exist or be nulls
      // val filter = Document(s"""{ "_id": { "country": $countryCode } }""")

      val filter = $and("_id.country" $eq countryCode, "_id.state" $eq JsNull, "_id.city" $eq JsNull)
      internalFindBy(filter, 0, 1).asOpt
    }

    def findStateBy(countryCode: String, stateCode: String): Future[Option[GeoRecord]] = {
      val filter = $and("_id.country" $eq countryCode, "_id.state" $eq stateCode, "_id.city" $eq JsNull)
      internalFindBy(filter, 0, 1).asOpt
    }

    def findCityBy(countryCode: String, stateCode: String, cityCode: String): Future[Option[GeoRecord]] = {
      val filter = $and("_id.country" $eq countryCode, "_id.state" $eq stateCode, "_id.city" $eq cityCode)
      internalFindBy(filter, 0, 1).asOpt
    }

  }

  object GeoModelDao {
    def apply(): GeoModelDao = new GeoModelDao("test-geo-model-" + UUID.randomUUID())
  }


}
class EntityWithOptionalFieldsDaoTest extends TestMongoServer {

  System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

  import EntityWithOptionalFieldsDaoTest._
  import GeoModel._

  private implicit def strToStrOpt(str: String): Option[String] = Some(str)

  protected val GeoRecords = Seq(
    GeoRecord(GeoKey("6252001"), "United States of America", 310232863),
    GeoRecord(GeoKey("6252001", "5128638"), "New York", 19274244),
    GeoRecord(GeoKey("6252001", "5128638", "5128581"), "New York City", 8175133),
    GeoRecord(GeoKey("6252001", "5128638", "5133273"), "Queens", 2272771),
    GeoRecord(GeoKey("6252001", "5128638", "5110302"), "Brooklyn", 2300664),
    GeoRecord(GeoKey("6252001", "5101760"), "New Jersey", 8751436),
    GeoRecord(GeoKey("6252001", "5101760", "5099836"), "Jersey City", 264290),
    GeoRecord(GeoKey("6252001", "5101760", "5099133"), "Hoboken", 53635),
    GeoRecord(GeoKey("6252001", "5332921"), "California", 37691912),
    GeoRecord(GeoKey("6252001", "5332921", "5391959"), "San Francisco", 864816),
    GeoRecord(GeoKey("146669"), "Republic of Cyprus", 1102677),
    GeoRecord(GeoKey("2921044"), "Federal Republic of Germany", 81802257),
    GeoRecord(GeoKey("2658434"), "Switzerland", 8484100),
    GeoRecord(GeoKey("294640"), "State of Israel", 7353985),
    GeoRecord(GeoKey("2635167"), "United Kingdom of Great Britain and Northern Ireland", 62348447),
    GeoRecord(GeoKey("2750405"), "Kingdom of the Netherlands", 16645000),
    GeoRecord(GeoKey("2661886"), "Kingdom of Sweden", 9828655),
    GeoRecord(GeoKey("732800"), "Republic of Bulgaria", 7148785),
    GeoRecord(GeoKey("719819"), "Hungary", 9982000),
    GeoRecord(GeoKey("3017382"), "Republic of France", 64768389),
    GeoRecord(GeoKey("798544"), "Republic of Poland", 38500000),
    GeoRecord(GeoKey("690791"), "Ukraine", 45415596)
  )

  "GeoModelDao" should {

    "findCountryBy" in {
      val dao = GeoModelDao()
      for {
        insertRes <- dao.insert(GeoRecords)

        usaByCode <- dao.findCountryBy("6252001")
        usaByKey <- dao.findById(GeoKey("6252001"))

        cyprusByCode <- dao.findCountryBy("146669")
        cyprusByKey <- dao.findById(GeoKey("146669"))

        uaByCode <- dao.findCountryBy("690791")
        uaByKey <- dao.findById(GeoKey("690791"))
      } yield {
        insertRes shouldBe Completed()

        usaByCode shouldBe Some(GeoRecord(GeoKey("6252001"), "United States of America", 310232863))
        usaByKey shouldBe Some(GeoRecord(GeoKey("6252001"), "United States of America", 310232863))

        cyprusByCode shouldBe Some(GeoRecord(GeoKey("146669"), "Republic of Cyprus", 1102677))
        cyprusByKey shouldBe Some(GeoRecord(GeoKey("146669"), "Republic of Cyprus", 1102677))

        uaByCode shouldBe Some(GeoRecord(GeoKey("690791"), "Ukraine", 45415596))
        uaByKey shouldBe Some(GeoRecord(GeoKey("690791"), "Ukraine", 45415596))

      }
    }


    "findStateBy" in {
      val dao = GeoModelDao()
      for {
        insertRes <- dao.insert(GeoRecords)

        nyByCode <- dao.findStateBy("6252001", "5128638")
        nyByKey <- dao.findById(GeoKey("6252001", "5128638"))

        njByCode <- dao.findStateBy("6252001", "5101760")
        njByKey <- dao.findById(GeoKey("6252001", "5101760"))

        caByCode <- dao.findStateBy("6252001", "5332921")
        caByKey <- dao.findById(GeoKey("6252001", "5332921"))
      } yield {
        insertRes shouldBe Completed()

        nyByCode shouldBe Some(GeoRecord(GeoKey("6252001", "5128638"), "New York", 19274244))
        nyByKey shouldBe Some(GeoRecord(GeoKey("6252001", "5128638"), "New York", 19274244))

        njByCode shouldBe Some(GeoRecord(GeoKey("6252001", "5101760"), "New Jersey", 8751436))
        njByKey shouldBe Some(GeoRecord(GeoKey("6252001", "5101760"), "New Jersey", 8751436))

        caByCode shouldBe Some(GeoRecord(GeoKey("6252001", "5332921"), "California", 37691912))
        caByKey shouldBe Some(GeoRecord(GeoKey("6252001", "5332921"), "California", 37691912))
      }
    }

    "findCityBy" in {
      val dao = GeoModelDao()
      for {
        insertRes <- dao.insert(GeoRecords)

        nycByCode <- dao.findCityBy("6252001", "5128638", "5128581")
        nycByKey <- dao.findById(GeoKey("6252001", "5128638", "5128581"))

        hbkByCode <- dao.findCityBy("6252001", "5101760", "5099133")
        hbkByKey <- dao.findById(GeoKey("6252001", "5101760", "5099133"))
      } yield {
        insertRes shouldBe Completed()

        nycByCode shouldBe Some(GeoRecord(GeoKey("6252001", "5128638", "5128581"), "New York City", 8175133))
        nycByKey shouldBe Some(GeoRecord(GeoKey("6252001", "5128638", "5128581"), "New York City", 8175133))

        hbkByCode shouldBe Some(GeoRecord(GeoKey("6252001", "5101760", "5099133"), "Hoboken", 53635))
        hbkByKey shouldBe Some(GeoRecord(GeoKey("6252001", "5101760", "5099133"), "Hoboken", 53635))
      }
    }

  }

}
