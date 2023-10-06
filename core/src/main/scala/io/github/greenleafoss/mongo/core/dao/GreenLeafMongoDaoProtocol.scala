package io.github.greenleafoss.mongo.core.dao

import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

trait GreenLeafMongoDaoProtocol[Id, E]:

  this: GreenLeafJsonBsonOps =>

  protected given idFormat: JsonFormat[Id]

  protected given eFormat: JsonFormat[E]
