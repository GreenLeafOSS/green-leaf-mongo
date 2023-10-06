package io.github.greenleafoss.mongo.core.util

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue

import scala.language.implicitConversions
import scala.util.Try
import scala.util.chaining.*

trait GreenLeafJsonBsonOps extends MongoExtendedJsonOps:

  // **************************************************
  // FORMATS
  // **************************************************

  type JsonFormat[_]
  type Json

  // **************************************************
  // JSON
  // **************************************************

  extension (string: String) def parseJson: Json
  extension [E: JsonFormat](e: E) def convertToJson: Json
  extension (json: Json) def convertTo[E: JsonFormat]: E

  // **************************************************
  // BSON
  // **************************************************

  // find("number" $eq 123)
  given convertToBson[E: JsonFormat]: Conversion[E, BsonValue] = _.pipe(convertToJson).pipe(convertJsonToBson)

  // find("number" $in Seq(1, 2, 3))
  given convertSeqToSeqBson[E: JsonFormat]: Conversion[Seq[E], Seq[BsonValue]] = _.map(e => e: BsonValue)

  extension (string: String)
    def parseBson: BsonValue = Try(string.parseBsonDocument).getOrElse(convertJsonToBson(string.parseJson))

    def parseBsonDocument: BsonDocument = BsonDocument(string)

  extension (bson: BsonValue) def convertTo[E: JsonFormat]: E = convertBsonToJson(bson).convertTo[E]

  // **************************************************
  // JSON <=> BSON
  // **************************************************

  protected def convertJsonToBson(json: Json): BsonValue
  protected def convertBsonToJson(bson: BsonValue): Json

object GreenLeafJsonBsonOps:
  final case class JsonBsonErr(msg: String) extends RuntimeException(msg)
