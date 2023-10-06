package io.github.greenleafoss.mongo.play.json

import io.github.greenleafoss.mongo.core.model.Model

import PlayJsonProtocol.given
import play.api.libs.json.*
import play.api.libs.json.given

trait PlayModelJsonProtocol extends PlayJsonProtocol:
  given modelJsonFormat: JsonFormat[Model] = Json.format[Model]
