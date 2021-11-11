package io.github.greenleafoss.mongo

import java.util.UUID

import GreenLeafMongoDao.DaoBsonProtocol
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.MongoCollection
import spray.json._

import scala.concurrent.Future

object EntityWithIdAsFieldDaoTest {

  object BuildingModel {

    // MODEL

    // ID as single field { "id": 1, "name": ... }
    case class Building(id: Long, name: String, height: Int, floors: Int, year: Int, address: String)

    // JSON

    trait BuildingModelJsonProtocol extends GreenLeafJsonProtocol {
      implicit lazy val BuildingFormat: RootJsonFormat[Building] = jsonFormat6(Building.apply)
    }

    object BuildingModelJsonProtocol extends BuildingModelJsonProtocol

    // BSON

    class BuildingModelBsonProtocol
      extends BuildingModelJsonProtocol
      with GreenLeafBsonProtocol
      with DaoBsonProtocol[Long, Building] {

      override implicit lazy val BuildingFormat: RootJsonFormat[Building] =
        jsonFormat(Building.apply, "_id", "name", "height", "floors", "year", "address")

      override implicit val idFormat: JsonFormat[Long] = LongJsonFormat
      override implicit val entityFormat: JsonFormat[Building] = BuildingFormat
    }

  }

  import BuildingModel._


  class BuildingDao(collectionName: String) extends TestGreenLeafMongoDao[Long, Building] {

    override protected val collection: MongoCollection[Document] = db.getCollection(collectionName)

    override protected val protocol: BuildingModelBsonProtocol = new BuildingModelBsonProtocol
    import protocol._

    def findByName(name: String): Future[Seq[Building]] = {
      find("name" $regex (name, "i"))
    }

    def findByFloors(minFloors: Int): Future[Seq[Building]] = {
      find("floors" $gte minFloors)
    }

    def findByAddressAndYear(address: String, year: Int): Future[Seq[Building]] = {
      find($and("address" $regex (address, "i"), "year" $gte year))
    }
  }

  object BuildingDao {
    def apply(): BuildingDao = new BuildingDao("test-building-" + UUID.randomUUID())
  }
}

class EntityWithIdAsFieldDaoTest extends TestMongoServer {

  import EntityWithIdAsFieldDaoTest._
  import BuildingModel._

  // https://en.wikipedia.org/wiki/List_of_tallest_buildings_in_New_York_City#Tallest_buildings
  val BuildingsInNyc = Map(
    1L -> Building(1, "One World Trade Center", 541, 104, 2014, "285 Fulton Street"),
    2L -> Building(2, "432 Park Avenue", 426, 96, 2015, "432 Park Avenue"),
    3L -> Building(3, "30 Hudson Yards", 387, 73, 2019, "West 33rd Street"),
    4L -> Building(4, "Empire State Building", 381, 103, 1931, "350 Fifth Avenue"),
    5L -> Building(5, "Bank of America Tower", 366, 54, 2009, "1101 Sixth Avenue"),
    6L -> Building(6, "3 World Trade Center", 329, 80, 2018, "175 Greenwich Street"),
    7L -> Building(7, "53W53", 320, 77, 2018, "53 West 53rd Street"),
    8L -> Building(8, "Chrysler Building", 319, 77, 1930, "405 Lexington Avenue"),
    9L -> Building(9, "The New York Times Building", 319, 52, 2007, "620 Eighth Avenue"),
    10L -> Building(10, "35 Hudson Yards", 308, 72, 2018, "532-560 West 33rd Street")
  )

  "BuildingDao (id as single field)" should {

    "insert one record" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(BuildingsInNyc(1))
      } yield {
        insertRes.wasAcknowledged shouldBe true
      }
    }

    "insert multiple records" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(Seq(BuildingsInNyc(2), BuildingsInNyc(3), BuildingsInNyc(4)))
      } yield {
        insertRes.wasAcknowledged shouldBe true
      }
    }

    "find by id" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(BuildingsInNyc(5))
        findRes <- dao.findById(5)
        getRes <- dao.getById(5)
      } yield {
        insertRes.wasAcknowledged shouldBe true
        findRes shouldBe Some(BuildingsInNyc(5))
        getRes shouldBe BuildingsInNyc(5)
      }
    }

    "find by ids" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(Seq(BuildingsInNyc(6), BuildingsInNyc(7), BuildingsInNyc(8), BuildingsInNyc(9)))
        x <- dao.findByIdsIn(Seq(6, 7, 8))
        y <- dao.findByIdsIn(Seq(9))
      } yield {
        insertRes.getInsertedIds should not be empty
        x should contain allElementsOf Seq(BuildingsInNyc(6), BuildingsInNyc(7), BuildingsInNyc(8))
        y should contain allElementsOf Seq(BuildingsInNyc(9))
      }
    }

    "find all" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(BuildingsInNyc.values.toSeq)
        findAllRes <- dao.findAll()
      } yield {
        insertRes.getInsertedIds should not be empty
        findAllRes should contain allElementsOf Seq(
          BuildingsInNyc(1), BuildingsInNyc(2), BuildingsInNyc(3), BuildingsInNyc(4), BuildingsInNyc(5),
          BuildingsInNyc(6), BuildingsInNyc(7), BuildingsInNyc(8), BuildingsInNyc(9), BuildingsInNyc(10))
      }
    }

    "findByName" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(Seq(BuildingsInNyc(9), BuildingsInNyc(10)))
        xNewYorkTimes <- dao.findByName("new york times")
        x35Hudson <- dao.findByName("35 Hudson")
      } yield {
        insertRes.getInsertedIds should not be empty
        xNewYorkTimes should contain only BuildingsInNyc(9)
        x35Hudson should contain only BuildingsInNyc(10)
      }
    }

    "findByFloors" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(BuildingsInNyc.values.toSeq)
        xGte90 <- dao.findByFloors(90)
        xGte100 <- dao.findByFloors(100)
      } yield {
        insertRes.getInsertedIds should not be empty
        xGte90 should contain only (BuildingsInNyc(1), BuildingsInNyc(2), BuildingsInNyc(4))
        xGte100 should contain only (BuildingsInNyc(1), BuildingsInNyc(4))
      }
    }

    "findByAddressAndYear" in {
      val dao = BuildingDao()
      for {
        insertRes <- dao.insert(BuildingsInNyc.values.toSeq)
        xAvenue2000 <- dao.findByAddressAndYear("aVeNue", 2000)
      } yield {
        insertRes.getInsertedIds should not be empty
        xAvenue2000 should contain only (BuildingsInNyc(2), BuildingsInNyc(5), BuildingsInNyc(9))
      }
    }

    "replaceById if previous entity doesn't exist" in {
      val dao = BuildingDao()
      for {
        updateRes <- dao.replaceById(1, BuildingsInNyc(1))
        findRes <- dao.findById(1)
      } yield {
        updateRes shouldBe None
        // entity doesn't exist and upsert = false by default
        findRes shouldBe None
      }
    }

    "createOrReplaceById if previous entity doesn't exist" in {
      val dao = BuildingDao()
      for {
        updateRes <- dao.createOrReplaceById(1, BuildingsInNyc(1))
        findRes <- dao.findById(1)
        getRes <- dao.getById(1)
      } yield {
        updateRes shouldBe None
        // entity doesn't exist but upsert = true in this case
        findRes shouldBe Some(BuildingsInNyc(1))
        getRes shouldBe BuildingsInNyc(1)
      }
    }

    "replaceById if previous entity exists" in {
      val dao = BuildingDao()
      val entityToCreate = BuildingsInNyc(1)
      val entityToUpdate = BuildingsInNyc(1).copy(name = "UPDATED")
      for {
        insertRes <- dao.insert(entityToCreate)
        updateRes <- dao.replaceById(1, entityToUpdate)
        findRes <- dao.findById(1)
        getRes <- dao.getById(1)
      } yield {
        insertRes.wasAcknowledged shouldBe true
        updateRes shouldBe Some(entityToCreate)
        findRes shouldBe Some(entityToUpdate)
        getRes shouldBe entityToUpdate
      }
    }

    "createOrReplaceById if previous entity exists" in {
      val dao = BuildingDao()
      val entityToCreate = BuildingsInNyc(1)
      val entityToUpdate = BuildingsInNyc(1).copy(name = "UPDATED")
      for {
        insertRes <- dao.insert(entityToCreate)
        updateRes <- dao.createOrReplaceById(1, entityToUpdate)
        findRes <- dao.findById(1)
        getRes <- dao.getById(1)
      } yield {
        insertRes.wasAcknowledged shouldBe true
        updateRes shouldBe Some(entityToCreate)
        findRes shouldBe Some(entityToUpdate)
        getRes shouldBe entityToUpdate
      }
    }

  }

}

