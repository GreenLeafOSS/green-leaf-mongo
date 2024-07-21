package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.circe.util.CirceJsonBsonOps.JsonFormat

import org.mongodb.scala.bson.ObjectId

trait CirceMongoDaoProtocolObjectId[E] extends CirceMongoDaoProtocol[ObjectId, E]:
  override protected given idFormat: JsonFormat[ObjectId] = ObjectIdJsonFormat
