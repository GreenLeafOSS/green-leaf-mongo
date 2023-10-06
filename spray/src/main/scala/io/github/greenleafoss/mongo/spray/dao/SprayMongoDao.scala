package io.github.greenleafoss.mongo.spray.dao

import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDao
import io.github.greenleafoss.mongo.spray.util.SprayJsonBsonOps

import scala.concurrent.ExecutionContext

abstract class SprayMongoDao[Id, E](
    using
    override protected val ec: ExecutionContext)
  extends GreenLeafMongoDao[Id, E]
  with SprayMongoDaoProtocol[Id, E]
  with SprayJsonBsonOps
