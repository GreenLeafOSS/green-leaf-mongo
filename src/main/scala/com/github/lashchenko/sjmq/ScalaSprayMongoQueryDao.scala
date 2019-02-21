package com.github.lashchenko.sjmq

import com.github.lashchenko.sjmq.ScalaSprayMongoQueryDao.DaoBsonProtocol
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{FindOneAndReplaceOptions, FindOneAndUpdateOptions}
import org.mongodb.scala.{Completed, FindObservable, MongoCollection, MongoDatabase, SingleObservable}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

object ScalaSprayMongoQueryDao {
  trait DaoBsonProtocol[Id, E] {
    implicit def jsonProtocolId : JsonFormat[Id]
    implicit def jsonProtocolEntity: JsonFormat[E]
  }
}

trait ScalaSprayMongoQueryDao[Id, E]
  extends ScalaSprayMongoQueryDsl {

  protected implicit val ec: ExecutionContext

  protected val db: MongoDatabase
  protected val collection: MongoCollection[Document]

  protected val protocol: DaoBsonProtocol[Id, E]
  import protocol._

  // _id, id, key, ...
  protected val primaryKey: String = "_id"

  protected implicit class MongoFindObservableToFutureRes(x: FindObservable[Document]) {
    def asSeq[T](implicit jf: JsonFormat[T]): Future[Seq[T]] =
      x.toFuture().map(_.map(_.toJson().parseJson.convertTo[T]))
    def asSeq: Future[Seq[E]] = asSeq[E]

    def asOpt[T](implicit jf: JsonFormat[T]): Future[Option[T]] =
      x.headOption().map(_.map(_.toJson().parseJson.convertTo[T]))
    def asOpt: Future[Option[E]] = asOpt[E]

    def asObj[T](implicit jf: JsonFormat[T]): Future[T] =
      x.head().map(_.toJson().parseJson.convertTo[T])
    def asObj: Future[E] = asObj[E]
  }

  protected implicit class MongoSingleObservableDocumentToFutureRes(x: SingleObservable[Document]) {
    def asOpt[T](implicit jf: JsonFormat[T]): Future[Option[T]] = x.toFuture().map {
      case d: Document => Option(d.toJson().parseJson.convertTo[T])
      case _ /* SingleObservable may contains null (Java) */ => None
    }
    def asOpt: Future[Option[E]] = asOpt[E]
  }

  def insert(e: E): Future[Completed] =
    collection.insertOne(e.toJson).toFuture()

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
    val filter = primaryKey $eq id
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

  // ********************************************************************************
  // https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndUpdate/
  // ********************************************************************************

  protected def internalUpdateBy(filter: Bson, update: Bson, upsert: Boolean = false): SingleObservable[Document] = {
    log.trace(s"DAO.internalUpdateBy [$primaryKey] : $filter")
    // By default "ReturnDocument.BEFORE" property used and returns the document before the update
    // val option = FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
    val option = FindOneAndUpdateOptions().upsert(upsert)
    collection.findOneAndUpdate(filter, update, option)
  }

  def updateById(id: Id, e: Document, upsert: Boolean = false): Future[Option[E]] = {
    val filter = primaryKey $eq id
    log.trace(s"DAO.updateById [$primaryKey] : $filter")
    internalUpdateBy(filter, e, upsert).asOpt
  }

  def updateBy(filter: Bson, e: Document, upsert: Boolean = false): Future[Option[E]] = {
    log.trace(s"DAO.updateBy [$primaryKey] : $filter")
    internalUpdateBy(filter, e, upsert).asOpt
  }


  // ********************************************************************************
  // https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndReplace/
  // ********************************************************************************

  protected def internalReplaceBy(filter: Bson, replacement: Document, upsert: Boolean = false): SingleObservable[Document] = {
    log.trace(s"DAO.internalReplaceBy [$primaryKey] : $filter")
    // By default "ReturnDocument.BEFORE" property used and returns the document before the update
    // val option = FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
    val option = FindOneAndReplaceOptions().upsert(upsert)
    collection.findOneAndReplace(filter, replacement, option)
  }

  def replaceById(id: Id, e: E, upsert: Boolean = false): Future[Option[E]] = {
    val filter = primaryKey $eq id
    log.trace(s"DAO.replaceById [$primaryKey] : $filter")
    internalReplaceBy(filter, e.toJson, upsert).asOpt
  }

  def createOrReplaceById(id: Id, e: E): Future[Option[E]] = {
    replaceById(id, e, upsert = true)
  }

  def replaceOrInsertById(id: Id, e: E): Future[Option[E]] = {
    replaceById(id, e /* upsert = false */).flatMap {
      case beforeOpt @ Some(_) /* replaced */ => Future.successful(beforeOpt)
      case None => insert(e).map { _: Completed => None }
    }
  }

  def replaceBy(filter: Bson, e: E, upsert: Boolean = false): Future[Option[E]] = {
    internalReplaceBy(filter, e.toJson, upsert).asOpt
  }

  def createOrReplaceBy(filter: Bson, e: E): Future[Option[E]] = {
    replaceBy(filter, e, upsert = true)
  }


  def deleteById(id: Id): Future[E] = ???
  def deleteByIds(id: Seq[Id]): Future[E] = ???

}
