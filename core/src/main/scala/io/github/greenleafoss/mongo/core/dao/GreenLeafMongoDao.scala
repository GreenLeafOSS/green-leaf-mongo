package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.filter.GreenLeafMongoFilterOps
import io.github.greenleafoss.mongo.core.log.Log
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.*
import org.mongodb.scala.FindObservable
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.SingleObservable
import org.mongodb.scala.bson.*
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.FindOneAndReplaceOptions
import org.mongodb.scala.model.FindOneAndUpdateOptions
import org.mongodb.scala.result.InsertManyResult
import org.mongodb.scala.result.InsertOneResult

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.reflect.ClassTag

abstract class GreenLeafMongoDao[Id, E](
    using
    override protected val ec: ExecutionContext)
  extends GreenLeafMongoObservableToFutureOps
  with GreenLeafMongoFilterOps
  with Log:

  this: GreenLeafMongoDaoProtocol[Id, E] with GreenLeafJsonBsonOps =>

  protected val collection: MongoCollection[Document]

  // _id, id, key, ...
  protected val primaryKey: String = "_id"

  protected def defaultSortBy: BsonValue = BsonDocument()

  def insert(e: E): Future[InsertOneResult] =
    val bson: BsonValue = e
    log.debug(s"DAO.insertOne: $bson")
    collection.insertOne(bson.asDocument()).toFuture()

  def insertMany(entities: Seq[E]): Future[InsertManyResult] =
    val documents: Seq[Document] = entities.map(e => (e: BsonValue).asDocument())
    log.debug(s"DAO.insertMany: $documents")
    collection.insertMany(documents).toFuture()

  protected def internalFind(
      filter: BsonValue,
      offset: Int,
      limit: Int,
      sortBy: BsonValue = defaultSortBy
    ): FindObservable[Document] =
    log.debug(s"DAO.internalFind: $filter")
    collection.find(filter.asDocument()).skip(offset).limit(limit).sort(sortBy.asDocument())

  def findOne(
      filter: BsonValue,
      offset: Int = 0,
      sortBy: BsonValue = defaultSortBy
    ): Future[Option[E]] =
    internalFind(filter, offset, limit = 1, sortBy).asOpt[E]

  def find(
      filter: BsonValue,
      offset: Int = 0,
      limit: Int = 0,
      sortBy: BsonValue = defaultSortBy
    ): Future[Seq[E]] =
    internalFind(filter, offset, limit, sortBy).asSeq[E]

  def getById(id: Id): Future[E] =
    internalFind(primaryKey $eq id, 0, 1).asObj[E]

  def findById(id: Id): Future[Option[E]] =
    internalFind(primaryKey $eq id, 0, 1).asOpt[E]

  // JSON fields can have different order, so if Id type is object don't use this query.
  // find({"id": { $in: [ {a: 1, b: 2 }, {a: 3, b: 4 }, ...] } }) - order of 'a' and 'b' fields may change
  // find({"id": { $in: [ {"id.a": 1, "id.b": 2}, ... ] } }) - will not work
  def findByIdsIn(
      ids: Seq[Id],
      offset: Int = 0,
      limit: Int = 0,
      sortBy: BsonValue = defaultSortBy
    ): Future[Seq[E]] =
    internalFind(primaryKey $in ids.map(id => id: BsonValue), offset, limit, sortBy).asSeq[E]

  def findByIdsOr(
      ids: Seq[Id],
      offset: Int = 0,
      limit: Int = 0,
      sortBy: BsonValue = defaultSortBy
    ): Future[Seq[E]] =
    internalFind($or(ids.map(id => primaryKey $is id): _*), offset, limit, sortBy).asSeq[E]

  def findAll(offset: Int = 0, limit: Int = 0, sortBy: BsonValue = defaultSortBy): Future[Seq[E]] =
    find(BsonDocument(), offset, limit, sortBy)

  // ********************************************************************************
  // https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndUpdate/
  // ********************************************************************************

  protected def internalUpdate(
      filter: BsonValue,
      update: BsonValue,
      upsert: Boolean = false
    ): SingleObservable[Document] =
    log.trace(s"DAO.internalUpdateBy [$primaryKey] : $filter")
    // By default "ReturnDocument.BEFORE" property used and returns the document before the update
    // val options = FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
    val options = FindOneAndUpdateOptions().upsert(upsert)
    collection.findOneAndUpdate(filter.asDocument(), update.asDocument(), options)

  def updateById(id: Id, e: BsonValue, upsert: Boolean = false): Future[Option[E]] =
    internalUpdate(primaryKey $eq id, e, upsert).asOpt[E]

  def update(
      filter: BsonValue,
      e: BsonValue,
      upsert: Boolean = false
    ): Future[Option[E]] =
    internalUpdate(filter, e, upsert).asOpt[E]

  // ********************************************************************************
  // https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndReplace/
  // ********************************************************************************

  protected def internalReplace(
      filter: BsonValue,
      replacement: BsonValue,
      upsert: Boolean = false
    ): SingleObservable[Document] =
    // By default "ReturnDocument.BEFORE" property used and returns the document before the update
    // val option = FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
    val options = FindOneAndReplaceOptions().upsert(upsert)
    collection.findOneAndReplace(filter.asDocument(), replacement.asDocument(), options)

  def replaceById(id: Id, e: E, upsert: Boolean = false): Future[Option[E]] =
    internalReplace(primaryKey $eq id, e, upsert).asOpt[E]

  def createOrReplaceById(id: Id, e: E): Future[Option[E]] =
    replaceById(id, e, upsert = true)

  /**
   * NOT ATOMICALLY find a document and replace it.
   * Impossible to upsert:true with a Dotted _id Query
   * https://docs.mongodb.com/manual/reference/method/db.collection.update/#upsert-true-with-a-dotted-id-query
   * @param id primary key filter
   * @param e entity to replace
   * @return None if document was created and Some(previous document) if the document was updated
   */
  def replaceOrInsertById(id: Id, e: E): Future[Option[E]] =
    replaceById(id, e /* upsert = false */ ).flatMap {
      case beforeOpt @ Some(_) /* replaced */ => Future.successful(beforeOpt)
      case None                               => insert(e).map { (_: InsertOneResult) => None }
    }

  def replace(filter: BsonValue, e: E, upsert: Boolean = false): Future[Option[E]] =
    internalReplace(filter, e, upsert).asOpt[E]

  def createOrReplace(filter: BsonValue, e: E): Future[Option[E]] =
    replace(filter, e, upsert = true)

  def distinct[T: ClassTag](fieldName: String, filter: BsonValue): Future[Seq[T]] =
    collection.distinct[T](fieldName, filter.asDocument()).toFuture()

  def aggregateBsonDocuments[A: JsonFormat](pipeline: Bson*): Future[Seq[Document]] =
    collection.aggregate(pipeline).toFuture()

  def aggregate[A: JsonFormat](pipeline: Bson*): Future[Seq[A]] =
    collection.aggregate(pipeline).asSeq[A]

  // def deleteById(id: Id): Future[E]       = ???
  // def deleteByIds(id: Seq[Id]): Future[E] = ???
