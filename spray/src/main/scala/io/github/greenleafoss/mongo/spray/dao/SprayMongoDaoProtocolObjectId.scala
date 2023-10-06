package io.github.greenleafoss.mongo.spray.dao

import org.mongodb.scala.bson.ObjectId

trait SprayMongoDaoProtocolObjectId[E] extends SprayMongoDaoProtocol[ObjectId, E]:
  override protected given idFormat: JsonFormat[ObjectId] = formatObjectId
