package com.github.lashchenko.sjmq

import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.FindOneAndReplaceOptions
import org.mongodb.scala.{Completed, FindObservable, MongoCollection, MongoDatabase, SingleObservable}
import org.slf4j.{Logger, LoggerFactory}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

trait ScalaSprayMongoQueryDao[Id, E]
  extends ScalaSprayMongoQueryDsl {

  protected val log: Logger = LoggerFactory.getLogger(getClass)

  protected implicit val ec: ExecutionContext

  protected implicit val jsonProtocolId: JsonFormat[Id]
  protected implicit val jsonProtocolEntity: JsonFormat[E]

  protected val db: MongoDatabase
  protected val collection: MongoCollection[Document]

  // _id, id, key, ...
  protected val primaryKey: String = "_id"

  protected implicit class MongoFindObservableToFutureRes(x: FindObservable[Document]) {
    def asSeq: Future[Seq[E]] = x.toFuture().map(_.map(_.toJson().parseJson.convertTo[E]))
    def asOpt: Future[Option[E]] = x.headOption().map(_.map(_.toJson().parseJson.convertTo[E]))
    def asObj: Future[E] = x.head().map(_.toJson().parseJson.convertTo[E])
  }
  protected implicit class MongoSingleObservableToFutureRes(x: SingleObservable[Document]) {
    def asObj: Future[E] = x.toFuture().map(_.toJson().parseJson.convertTo[E])
  }

  def insert(e: E): Future[Completed] =
    collection.insertOne(Document(e.toJson.compactPrint)).toFuture()

  def insert(entities: Seq[E]): Future[Completed] = {
    // Document([ obj1, obj2, ... ]) can't be created
    // [ Document(obj1), Document(obj2), ... ] - OK
    val documents = entities.map(d => Document(d.toJson.compactPrint))
    collection.insertMany(documents).toFuture()
  }

  protected def internalFindBy(filter: Bson, offset: Int, limit: Int): FindObservable[Document] = {
    // TODO add sort: Bson parameter
    log.trace("DAO.internalFindBy: " + filter.toString)
    collection.find(filter).skip(offset).limit(limit)
  }

  def getById(id: Id): Future[E] = {
    val filter = primaryKey $eq id.asJsonExpanded(primaryKey)
    log.trace(s"DAO.getById [$primaryKey] : $filter")
    internalFindBy(filter, 0, 1).asObj
  }

  def findById(id: Id): Future[Option[E]] = {
    val filter = primaryKey $eq id
    log.trace(s"DAO.findById [$primaryKey] : $filter")
    internalFindBy(filter, 0, 1).asOpt
  }

  // JSON fields can have different order, so if Id type is object don't use this query.
  // find({"id": { $in: [ {a: 1, b: 2 }, {a: 3, b: 4 }, ...] } }) - order of 'a' and 'b' fields may change
  // find({"id": { $in: [ {"id.a": 1, "id.b": 2}, ... ] } }) - will not work
  def findByIdsIn(ids: Seq[Id], offset: Int = 0, limit: Int = 0): Future[Seq[E]] = ids match {
    case Nil => Future.successful(Seq.empty)
    case id :: Nil => findById(id).map(_.toSeq)
    case _ => internalFindBy(primaryKey $in (ids.map(_.asJsonExpanded): _*), offset, limit).asSeq
  }

  def findByIdsOr(ids: Seq[Id], offset: Int = 0, limit: Int = 0): Future[Seq[E]] = ids match {
    case Nil => Future.successful(Seq.empty)
    case id :: Nil => findById(id).map(_.toSeq)
    case _ => internalFindBy($or(ids.map(_.asJsonExpanded(primaryKey)): _*), offset, limit).asSeq
  }

  def findAll(offset: Int = 0, limit: Int = 0): Future[Seq[E]] = {
    internalFindBy(Document.empty, offset, limit).asSeq
  }

  def updateById(id: Id, e: E): Future[E] = {
    val filter = primaryKey $eq id
    log.trace(s"DAO.updateById [$primaryKey] : $filter")
    val update = Document(e.toJson.compactPrint)
    // By default "ReturnDocument.BEFORE" property used and returns the document before the update
    // val option = FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
    val option = FindOneAndReplaceOptions().upsert(true)
    collection.findOneAndReplace(filter, update, option).asObj
  }

  def deleteById(id: Id): Future[E] = ???
  def deleteByIds(id: Seq[Id]): Future[E] = ???

}
