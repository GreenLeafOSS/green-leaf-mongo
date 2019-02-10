package com.github.lashchenko.sjmq

import java.time.ZonedDateTime
import java.util.UUID

import com.github.lashchenko.sjmq.ZonedDateTimeOps._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Indexes._
import org.mongodb.scala.{Completed, MongoCollection}
import spray.json.{JsonFormat, RootJsonFormat}

import scala.concurrent.Future

object EntityWithoutIdDaoTest {

  object EventModel {

    // MODEL

    object EventSource extends Enumeration {
      type EventSource = Value

      val Internal = Value(1, "Internal")
      val WebApp = Value(2, "WebApp")
      val MobileApp = Value(3, "MobileApp")
      val DesktopApp = Value(4, "DesktopApp")

    }

    case class Event(userId: Long, source: EventSource.EventSource, comment: String, timestamp: ZonedDateTime = now)

    // JSON

    trait EventJsonProtocol extends ScalaSprayJsonProtocol {
      implicit lazy val EventSourceFormat: JsonFormat[EventSource.EventSource] = enumToJsonFormatAsString(EventSource)
      implicit lazy val EventFormat: RootJsonFormat[Event] = jsonFormat4(Event)
    }

    object EventJsonProtocol extends EventJsonProtocol

    // BSON

    trait EventBsonProtocol extends EventJsonProtocol with ScalaSprayBsonProtocol {
      override implicit lazy val EventSourceFormat: JsonFormat[EventSource.EventSource] = enumToJsonFormatAsInt(EventSource)
    }

    object EventBsonProtocol extends EventBsonProtocol

  }

  import EventModel._

  class EventDao(collectionName: String) extends TestScalaSprayMongoQueryDao[ObjectId, Event] {

    import EventBsonProtocol._
    protected implicit val jsonProtocolId: JsonFormat[ObjectId] = ObjectIdJsonFormat
    protected implicit val jsonProtocolEntity: JsonFormat[Event] = EventFormat

    protected val collection: MongoCollection[Document] = db.getCollection(collectionName)

    collection.createIndex(key = ascending("timestamp"), IndexOptions().name("idx-timestamp")).toFuture()

    override def findAll(offset: Int = 0, limit: Int = 0): Future[Seq[Event]] = {
      internalFindBy(Document.empty, offset, limit).sort(Document("""{timestamp: 1}""")).asSeq
    }

    def findLastN(limit: Int = 0): Future[Seq[Event]] = {
      internalFindBy(Document.empty, 0, limit).sort(Document("""{timestamp: -1}""")).asSeq
    }

    def findBySource(source: EventSource.EventSource): Future[Seq[Event]] = {
      val filter = "source" $eq source
      internalFindBy(filter, 0, 0).asSeq
    }
  }

  object EventDao {
    def apply(): EventDao = new EventDao("test-event-" + UUID.randomUUID())
  }
}

class EntityWithoutIdDaoTest extends TestMongoServer {

  import EntityWithoutIdDaoTest._
  import EventModel._

  private val Events = Array(
    Event(1L, EventSource.WebApp, "Request to create an account"),
    Event(1L, EventSource.Internal, "Account created"),
    Event(1L, EventSource.WebApp, "Request to extended access"),
    Event(1L, EventSource.Internal, "Request to provide additional details"),
    Event(1L, EventSource.DesktopApp, "Additional details provided"),
    Event(1L, EventSource.Internal, "Additional details approved"),
    Event(1L, EventSource.Internal, "Access granted"),

    Event(2L, EventSource.WebApp, "Request to create an account"),
    Event(2L, EventSource.Internal, "Account created"),

    Event(3L, EventSource.WebApp, "Request to create an account"),
    Event(3L, EventSource.Internal, "Account created")
  )

  "EventDao (entity without _id)" should {

    "insert one record" in {
      val dao = EventDao()
      for {
        insertRes <- dao.insert(Events(0))
      } yield {
        insertRes shouldBe Completed()
      }
    }

    "insert multiple records" in {
      val dao = EventDao()
      for {
        insertRes <- dao.insert(Seq(Events(1), Events(2), Events(3)))
      } yield {
        insertRes shouldBe Completed()
      }
    }

    "find all" in {
      val dao = EventDao()
      for {
        insertRes <- dao.insert(Seq(Events(0), Events(1), Events(2), Events(3)))
        xAll <- dao.findAll()
      } yield {
        insertRes shouldBe Completed()
        xAll.size shouldBe 4
        xAll should contain allElementsOf Seq(Events(0), Events(1), Events(2), Events(3))
        xAll(0) shouldBe Events(0)
        xAll(1) shouldBe Events(1)
        xAll(2) shouldBe Events(2)
        xAll(3) shouldBe Events(3)
      }
    }

    "find last N events" in {
      val dao = EventDao()
      for {
        insertRes <- dao.insert(Seq(Events(0), Events(1), Events(2), Events(3)))
        xAll <- dao.findLastN(2)
      } yield {
        insertRes shouldBe Completed()
        xAll.size shouldBe 2
        xAll should contain allElementsOf Seq(Events(2), Events(3))
        xAll(0) shouldBe Events(3) // last event
        xAll(1) shouldBe Events(2) // last - 1 event
      }
    }

    "find by source" in {
      val dao = EventDao()
      for {
        insertRes <- dao.insert(Seq(Events(0), Events(1), Events(2), Events(3)))
        xAll <- dao.findBySource(EventSource.Internal)
      } yield {
        insertRes shouldBe Completed()
        xAll.size shouldBe 2
        xAll should contain allElementsOf Seq(Events(1), Events(3))
        xAll(0) shouldBe Events(1)
        xAll(1) shouldBe Events(3)
      }
    }

  }

}
