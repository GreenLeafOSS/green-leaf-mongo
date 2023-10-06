package io.github.greenleafoss.mongo.play.dao

import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDaoProtocol
import io.github.greenleafoss.mongo.play.bson.PlayBsonProtocol
import io.github.greenleafoss.mongo.play.util.PlayJsonBsonOps

trait PlayMongoDaoProtocol[Id, E] extends GreenLeafMongoDaoProtocol[Id, E] with PlayBsonProtocol with PlayJsonBsonOps
