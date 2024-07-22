package io.github.greenleafoss.mongo.circe.util

import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps.JsonBsonErr
import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps.*

import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonBoolean
import org.mongodb.scala.bson.BsonDateTime
import org.mongodb.scala.bson.BsonDecimal128
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonDouble
import org.mongodb.scala.bson.BsonInt32
import org.mongodb.scala.bson.BsonInt64
import org.mongodb.scala.bson.BsonNull
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.bson.BsonString
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.bson.ObjectId

import java.time.ZonedDateTime

import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions

import io.circe.*
import io.circe.Json as CirceJson
import io.circe.parser.*
import io.circe.syntax.*

trait CirceJsonBsonOps extends GreenLeafJsonBsonOps:

  // **************************************************
  // FORMATS
  // **************************************************

  override type JsonFormat[E] = Codec[E]
  override type Json          = CirceJson

  // **************************************************
  // JSON
  // **************************************************

  extension (string: String)
    override def parseJson: Json = parse(string) match
      case Right(json) => json
      case Left(err)   => throw err

  extension [E: JsonFormat](e: E) override def convertToJson: Json = e.asJson

  extension (json: Json)
    override def convertTo[E: JsonFormat]: E =
      decode[E](json.noSpacesSortKeys) match
        case Right(res) => res
        case Left(err)  => throw err

  import io.github.greenleafoss.mongo.circe.bson.CirceBsonProtocol.*
  import io.github.greenleafoss.mongo.circe.bson.CirceBsonProtocol.given

  override protected def convertJsonToBson(json: Json): BsonValue = json match
    case x if x.isObject  =>
      x.asObject match
        case Some(x: JsonObject) if x.contains($oid)           => BsonObjectId(json.convertTo[ObjectId])
        case Some(x: JsonObject) if x.contains($date)          => BsonDateTime(json.convertTo[ZonedDateTime].toEpochMilli)
        case Some(x: JsonObject) if x.contains($numberDecimal) => BsonDecimal128(json.convertTo[BigDecimal])
        case Some(x: JsonObject) if x.contains($numberDouble)  => BsonDouble(json.convertTo[Double])
        case Some(x: JsonObject) if x.contains($numberLong)    => BsonInt64(json.convertTo[Long])
        case Some(x: JsonObject) if x.contains($numberInt)     => BsonInt32(json.convertTo[Int])
        case Some(x: JsonObject)                               => BsonDocument(x.toMap.map { case (k, v) => k -> convertJsonToBson(v) })
        case _                                                 => throw JsonBsonErr(s"Unknown input in JSON to BSON: $json")
    case x if x.isArray   => BsonArray.fromIterable(x.asArray.getOrElse(Vector.empty).map(convertJsonToBson))
    case x if x.isBoolean => BsonBoolean(x.asBoolean.getOrElse(false))
    case x if x.isString  => BsonString(x.asString.getOrElse(""))
    case x if x.isNull    => BsonNull()
    case _                => throw JsonBsonErr(s"Unknown input in JSON to BSON: $json")

  // **************************************************
  // BSON
  // **************************************************

  override protected def convertBsonToJson(bson: BsonValue): Json = bson match
    case x: BsonDocument   => x.toJson(jws).parseJson
    case x: BsonArray      => CirceJson.fromValues(x.getValues.iterator().asScala.map(convertBsonToJson).toVector)
    case x: BsonDateTime   => x.getValue.asZonedDateTime().convertToJson
    case x: BsonString     => x.getValue.convertToJson
    case x: BsonBoolean    => x.getValue.convertToJson
    case x: BsonObjectId   => x.getValue.convertToJson
    case x: BsonInt32      => x.getValue.convertToJson
    case x: BsonInt64      => x.getValue.convertToJson
    case x: BsonDouble     => x.getValue.convertToJson
    case x: BsonDecimal128 => BigDecimal(x.decimal128Value().bigDecimalValue()).convertToJson
    case _: BsonNull       => CirceJson.Null
    case _                 => throw JsonBsonErr(s"Unknown input in BSON to JSON: $bson")

object CirceJsonBsonOps extends CirceJsonBsonOps
