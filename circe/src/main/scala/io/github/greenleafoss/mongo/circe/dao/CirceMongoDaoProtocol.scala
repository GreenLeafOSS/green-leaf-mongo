package io.github.greenleafoss.mongo.circe.dao

import io.github.greenleafoss.mongo.circe.bson.CirceBsonProtocol
import io.github.greenleafoss.mongo.circe.util.CirceJsonBsonOps
import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDaoProtocol

trait CirceMongoDaoProtocol[Id, E] extends GreenLeafMongoDaoProtocol[Id, E] with CirceBsonProtocol with CirceJsonBsonOps
