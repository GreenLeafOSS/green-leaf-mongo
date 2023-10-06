package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDao
import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDaoProtocol
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.*
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.result.InsertManyResult

import java.util.UUID

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

abstract class TestGreenLeafMongoDao[Id, E]
  extends GreenLeafMongoDao[Id, E](using scala.concurrent.ExecutionContext.Implicits.global):

  this: GreenLeafMongoDaoProtocol[Id, E] with GreenLeafJsonBsonOps =>

  protected lazy val db: MongoDatabase = MongoClient("mongodb://localhost:27027").getDatabase("test")

  protected lazy val collectionName: String = UUID.randomUUID().toString

  override protected val collection: MongoCollection[Document] = db.getCollection(collectionName)

  def insertDocuments(documents: Document*): Future[InsertManyResult] =
    // for tests to insert records with custom ordering of fields
    collection.insertMany(documents).toFuture()
