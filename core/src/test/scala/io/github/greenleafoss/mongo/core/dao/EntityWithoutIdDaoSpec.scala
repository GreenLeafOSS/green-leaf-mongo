package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.dao.EntityWithoutIdDaoSpec.EventSource.EventSource
import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.mongo.TestMongoServer
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps

import org.mongodb.scala.*
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Indexes.*

import java.time.ZonedDateTime

import scala.concurrent.Future
import scala.language.implicitConversions

object EntityWithoutIdDaoSpec:

  // **************************************************
  // MODELS
  // **************************************************

  object EventSource extends Enumeration:
    type EventSource = Value

    val Internal: Value   = Value(1, "Internal")
    val WebApp: Value     = Value(2, "WebApp")
    val MobileApp: Value  = Value(3, "MobileApp")
    val DesktopApp: Value = Value(4, "DesktopApp")

  final case class Event(
      userId: Long,
      source: EventSource.EventSource,
      comment: String,
      timestamp: ZonedDateTime = ZonedDateTimeOps.now())

  // **************************************************
  // DAO BSON PROTOCOL
  // **************************************************

  trait EventDaoBsonProtocol extends GreenLeafMongoDaoProtocolObjectId[Event]:
    this: GreenLeafMongoJsonBasicFormats with GreenLeafJsonBsonOps =>
    given EventSourceFormat: JsonFormat[EventSource]

  // **************************************************
  // DAO
  // **************************************************

  abstract class EventDao extends TestGreenLeafMongoDao[ObjectId, Event]:

    this: EventDaoBsonProtocol with GreenLeafMongoJsonBasicFormats with GreenLeafJsonBsonOps =>

    collection.createIndex(key = ascending("timestamp"), IndexOptions().name("idx-timestamp")).toFuture()

    override def findAll(offset: Int = 0, limit: Int = 0, sortBy: BsonValue = """{timestamp: 1}""".parseBson)
        : Future[Seq[Event]] =
      find(BsonDocument(), offset, limit, sortBy)

    def findLastN(limit: Int = 0, sortBy: BsonValue = """{timestamp: -1}""".parseBson): Future[Seq[Event]] =
      find(BsonDocument(), 0, limit, sortBy)

    def findBySource(source: EventSource.EventSource): Future[Seq[Event]] =
      find("source" $eq source)

abstract class EntityWithoutIdDaoSpec extends TestMongoServer:

  import EntityWithoutIdDaoSpec.*

  protected def newEventDao: EventDao

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

  "EventDao (entity without _id)" should:

    "insert one record" in:
      val dao = newEventDao
      for insertRes <- dao.insert(Events(0))
      yield insertRes.wasAcknowledged shouldBe true

    "insert multiple records" in:
      val dao = newEventDao
      for insertRes <- dao.insertMany(Seq(Events(1), Events(2), Events(3)))
      yield insertRes.getInsertedIds should not be empty

    "find all" in:
      val dao = newEventDao
      for
        insertRes <- dao.insertMany(Seq(Events(0), Events(1), Events(2), Events(3)))
        xAll      <- dao.findAll()
      yield
        insertRes.getInsertedIds should not be empty
        xAll.size shouldBe 4
        xAll should contain allElementsOf Seq(Events(0), Events(1), Events(2), Events(3))
        xAll(0) shouldBe Events(0)
        xAll(1) shouldBe Events(1)
        xAll(2) shouldBe Events(2)
        xAll(3) shouldBe Events(3)

    "find last N events" in:
      val dao = newEventDao
      for
        insertRes <- dao.insertMany(Seq(Events(0), Events(1), Events(2), Events(3)))
        xAll      <- dao.findLastN(2)
      yield
        insertRes.getInsertedIds should not be empty
        xAll.size shouldBe 2
        xAll should contain allElementsOf Seq(Events(2), Events(3))
        xAll(0) shouldBe Events(3) // last event
        xAll(1) shouldBe Events(2) // last - 1 event

    "find by source" in:
      val dao = newEventDao
      for
        insertRes <- dao.insertMany(Seq(Events(0), Events(1), Events(2), Events(3)))
        xAll      <- dao.findBySource(EventSource.Internal)
      yield
        insertRes.getInsertedIds should not be empty
        xAll.size shouldBe 2
        xAll should contain allElementsOf Seq(Events(1), Events(3))
        xAll(0) shouldBe Events(1)
        xAll(1) shouldBe Events(3)
