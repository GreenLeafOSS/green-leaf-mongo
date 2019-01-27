package com.github.lashchenko.sjmq

import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{Completed, MongoClient, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

abstract class TestScalaSprayMongoQueryDao[Id, E] extends ScalaSprayMongoQueryDao[Id, E] {

  override protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  override protected val db: MongoDatabase = MongoClient().getDatabase("test-in-memory-db")

  def insertDocuments(documents: Document*): Future[Completed] = {
    collection.insertMany(documents).toFuture()
  }

}