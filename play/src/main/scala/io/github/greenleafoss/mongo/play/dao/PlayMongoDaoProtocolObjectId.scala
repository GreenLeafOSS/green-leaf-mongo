package io.github.greenleafoss.mongo.play.dao

import org.mongodb.scala.bson.ObjectId

trait PlayMongoDaoProtocolObjectId[E] extends PlayMongoDaoProtocol[ObjectId, E]:
  override protected given idFormat: JsonFormat[ObjectId] = formatObjectId
