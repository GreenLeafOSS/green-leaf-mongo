package io.github.greenleafoss.mongo

import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.mongodb.scala.result.InsertManyResult
import org.mongodb.scala._

import scala.concurrent.{ExecutionContext, Future}

abstract class TestGreenLeafMongoDao[Id, E] extends GreenLeafMongoDao[Id, E] {

  override protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  // TODO move to config
  override protected val db: MongoDatabase = MongoClient("mongodb://localhost:27027").getDatabase("test")

  def insertDocuments(documents: Document*): Future[InsertManyResult] = {
    collection.insertMany(documents).toFuture()
  }

}
