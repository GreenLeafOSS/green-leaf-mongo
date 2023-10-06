package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.bson.ObjectId

trait GreenLeafMongoDaoProtocolObjectId[E] extends GreenLeafMongoDaoProtocol[ObjectId, E]:
  this: GreenLeafJsonBsonOps =>
