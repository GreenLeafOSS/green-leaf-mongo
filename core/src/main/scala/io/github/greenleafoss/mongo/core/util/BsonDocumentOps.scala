package io.github.greenleafoss.mongo.core.util

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue

import java.util

import scala.jdk.CollectionConverters._
import scala.language.implicitConversions
import scala.util.chaining._

trait BsonDocumentOps:

  extension (doc: BsonDocument)
    def removeKey(k: String): BsonDocument = doc.tap(_.remove(k))

    def update(k: String, v: BsonValue): BsonDocument = doc.tap(_.put(k, v))

    def update(kv: (String, BsonValue)): BsonDocument = update(kv._1, kv._2)

    def entries: Seq[util.Map.Entry[String, BsonValue]] = doc.entrySet().asScala.toSeq

    def entriesKv: Seq[(String, BsonValue)] = entries.map(e => e.getKey -> e.getValue)

object BsonDocumentOps extends BsonDocumentOps
