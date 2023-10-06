package io.github.greenleafoss.mongo.spray.json

import io.github.greenleafoss.mongo.core.model.Model

import spray.json.DefaultJsonProtocol.*
import spray.json.JsonFormat

trait SprayModelJsonProtocol extends SprayJsonProtocol:
  given modelJsonFormat: JsonFormat[Model] = jsonFormat9(Model.apply)
