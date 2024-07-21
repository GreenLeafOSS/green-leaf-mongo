package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDao

import io.github.greenleafoss.mongo.circe.util.CirceJsonBsonOps

import scala.concurrent.ExecutionContext

trait CirceMongoDao[Id, E](
    using
    override protected val ec: ExecutionContext)
  extends GreenLeafMongoDao[Id, E]
  with CirceMongoDaoProtocol[Id, E]
  with CirceJsonBsonOps
