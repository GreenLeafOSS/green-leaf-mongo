package io.github.greenleafoss.mongo.core.filter

import io.github.greenleafoss.mongo.core.filter.GreenLeafMongoDotNotationOps.*
import io.github.greenleafoss.mongo.core.util.BsonDocumentOps.*

import org.mongodb.scala.bson.*
import org.mongodb.scala.bson.BsonValue

import scala.annotation.tailrec
import scala.annotation.targetName
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

trait GreenLeafMongoFilterOps:

  protected def $and(filters: BsonValue*): BsonDocument =
    dotNotation(BsonDocument("$and" -> BsonArray.fromIterable(filters))).asDocument()

  protected def $or(filters: BsonValue*): BsonDocument =
    dotNotation(BsonDocument("$or" -> BsonArray.fromIterable(filters))).asDocument()

  protected def $not(filter: BsonValue): BsonDocument =
    dotNotation(BsonDocument("$not" -> filter)).asDocument()

  protected def $nor(filters: BsonValue*): BsonDocument =
    dotNotation(BsonDocument("$nor" -> BsonArray.fromIterable(filters))).asDocument()

  protected def $gt(value: BsonValue): BsonDocument =
    dotNotation(BsonDocument("$gt" -> value)).asDocument()

  protected def $lt(value: BsonValue): BsonDocument =
    dotNotation(BsonDocument("$lt" -> value)).asDocument()

  protected def $gte(value: BsonValue): BsonDocument =
    dotNotation(BsonDocument("$gte" -> value)).asDocument()

  protected def $lte(value: BsonValue): BsonDocument =
    dotNotation(BsonDocument("$lte" -> value)).asDocument()

  protected def $in(values: Seq[BsonValue]): BsonDocument =
    dotNotation(BsonDocument("$in" -> BsonArray.fromIterable(values))).asDocument()

  protected def $nin(values: Seq[BsonValue]): BsonDocument =
    dotNotation(BsonDocument("$nin" -> BsonArray.fromIterable(values))).asDocument()

  protected def $exists(exists: Boolean): BsonDocument =
    dotNotation(BsonDocument("$exists" -> BsonBoolean(exists))).asDocument()

  protected def $regex(pattern: Regex): BsonRegularExpression =
    dotNotation(BsonRegularExpression(pattern)).asRegularExpression()

  protected def $regex(pattern: String): BsonDocument =
    dotNotation(BsonRegularExpression(pattern)).asDocument()

  protected def $regex(pattern: String, options: String): BsonDocument =
    dotNotation(BsonRegularExpression(pattern, options)).asDocument()

  protected def $elemMatch(filter: BsonValue): BsonDocument =
    dotNotation(BsonDocument("$elemMatch" -> filter)).asDocument()

  protected def $size(size: Int): BsonDocument =
    dotNotation(BsonDocument("$size" -> BsonInt32(size))).asDocument()

  extension (field: String)

    // @tailrec should be either a final (we can't do it with an extension methods) or private def
    // @tailrec private def $equal(values: BsonValue*): BsonValue = values match
    //  case Seq(value) => dotNotation(BsonDocument(field -> BsonDocument("$eq" -> value)))
    //  case _          => dotNotation(BsonDocument(field -> BsonDocument("$eq" -> BsonArray.fromIterable(values))))

    // @targetName("mongo-operator-eq-val")
    protected def $eq(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$eq" -> value))).asDocument()

    // @targetName("mongo-operator-eq-seq")
    protected def $eq(values: Seq[BsonValue]): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$eq" -> BsonArray.fromIterable(values)))).asDocument()

    protected def $is(value: BsonValue): BsonDocument =
      $eq(value)

    protected def $is(values: Seq[BsonValue]): BsonDocument =
      $eq(values)

    protected def $ne(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$ne" -> value))).asDocument()

    protected def $not(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$not" -> value))).asDocument()

    protected def $gt(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$gt" -> value))).asDocument()

    protected def $lt(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$lt" -> value))).asDocument()

    protected def $gte(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$gte" -> value))).asDocument()

    protected def $lte(value: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$lte" -> value))).asDocument()

    protected def $in(values: Seq[BsonValue]): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$in" -> BsonArray.fromIterable(values)))).asDocument()

    protected def $nin(values: Seq[BsonValue]): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$nin" -> BsonArray.fromIterable(values)))).asDocument()

    protected def $exists(exists: Boolean): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$exists" -> BsonBoolean(exists)))).asDocument()

    protected def $regex(pattern: Regex): BsonDocument =
      dotNotation(BsonDocument(field -> BsonRegularExpression(pattern))).asDocument()

    protected def $regex(pattern: String, options: String = ""): BsonDocument =
      dotNotation(BsonDocument(field -> BsonRegularExpression(pattern, options))).asDocument()

    protected def $elemMatch(filter: BsonValue): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$elemMatch" -> filter))).asDocument()

    protected def $size(size: Int): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$size" -> BsonInt32(size)))).asDocument()

    protected def $all(values: Seq[BsonValue]): BsonDocument =
      dotNotation(BsonDocument(field -> BsonDocument("$all" -> BsonArray.fromIterable(values)))).asDocument()

// object GreenLeafMongoFilterOps extends GreenLeafMongoFilterOps
