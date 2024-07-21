package io.github.greenleafoss.mongo.play.dao

import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDao

import io.github.greenleafoss.mongo.play.util.PlayJsonBsonOps

import scala.concurrent.ExecutionContext

trait PlayMongoDao[Id, E](
    using
    override protected val ec: ExecutionContext)
  extends GreenLeafMongoDao[Id, E]
  with PlayMongoDaoProtocol[Id, E]
  with PlayJsonBsonOps
