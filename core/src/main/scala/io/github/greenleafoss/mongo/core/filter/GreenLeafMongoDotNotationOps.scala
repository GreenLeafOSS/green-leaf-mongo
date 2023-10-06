package io.github.greenleafoss.mongo.core.filter

import io.github.greenleafoss.mongo.core.util.BsonDocumentOps.*

import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue

import scala.jdk.CollectionConverters._

trait GreenLeafMongoDotNotationOps:
  // https://www.mongodb.com/docs/v5.0/core/document/#dot-notation

  private def dotNotation(doc: BsonDocument): BsonValue = doc.entriesKv.foldLeft(doc) {
    // { $exists: { ... } }
    case (res, (opK, nestedDoc: BsonDocument)) if opK.startsWith("$") => res.update(opK -> dotNotation(nestedDoc))

    // { $and: [ ... ] }
    case (res, (opK, nestedArr: BsonArray)) if opK.startsWith("$") => res.update(opK -> dotNotation(nestedArr))

    // { $operator: 123 }
    case (res, (opK, nestedVal: BsonValue)) if opK.startsWith("$") => res.update(opK -> nestedVal)

    // {"_id.base": {"$eq": "USD"}, "_id.date": {"$eq": {"$date": "1970-01-01T00:00:00Z"}}
    case (res, (fieldK, nestedDoc: BsonDocument)) if nestedDoc.containsKey("$eq") =>
      nestedDoc.get("$eq") match {
        case d: BsonDocument =>
          //  will replace by dot notation or the same data below
          res.removeKey(fieldK)
          // { field: { $eq: { ... } } }
          d.entries.foldLeft(res) {
            case (_, e) if !e.getKey.startsWith("$") =>
              // "_id": {"$eq": {"date": {"$eq": {$date": "1970-01-01T00:00:00Z"}} } }
              res.update(s"$fieldK.${e.getKey}", BsonDocument("$eq" -> dotNotation(e.getValue)))
            // "_id.date": {"$eq": {"$date": "2019-01-03T00:00:00Z"}}
            case (_, e)                              =>
              // keep this KV as is, case like "_id.date": {"$eq": {"$date": "1970-01-01T00:00:00Z"}}
              res.update(s"$fieldK", BsonDocument("$eq" -> BsonDocument(e.getKey -> e.getValue)))
          }

        // { field: { $eq: 123 } }
        case _               => res
      }

    // { $and: [ ... ] }
    case (res, (fieldK, nestedArr: BsonArray))                                    =>
      res.update(fieldK -> dotNotation(nestedArr))

    case (res, _) => res
  }

  private def dotNotation(arr: BsonArray): BsonValue =
    BsonArray.fromIterable(
      arr
        .iterator()
        .asScala
        .map {
          case d: BsonDocument => dotNotation(d)
          case a: BsonArray    => dotNotation(a)
          case v: BsonValue    => v
        }
        .iterator
        .to(Iterable)
    )

  def dotNotation(bson: BsonValue): BsonValue = bson match {
    case doc: BsonDocument => dotNotation(doc)
    case arr: BsonArray    => dotNotation(arr)
    case value: BsonValue  => value
  }

object GreenLeafMongoDotNotationOps extends GreenLeafMongoDotNotationOps
