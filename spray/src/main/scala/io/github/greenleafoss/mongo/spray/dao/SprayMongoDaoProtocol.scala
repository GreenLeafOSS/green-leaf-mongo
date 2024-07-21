package io.github.greenleafoss.mongo.spray.dao

import io.github.greenleafoss.mongo.core.dao.GreenLeafMongoDaoProtocol

import io.github.greenleafoss.mongo.spray.bson.SprayBsonProtocol
import io.github.greenleafoss.mongo.spray.util.SprayJsonBsonOps

trait SprayMongoDaoProtocol[Id, E] extends GreenLeafMongoDaoProtocol[Id, E] with SprayBsonProtocol with SprayJsonBsonOps
