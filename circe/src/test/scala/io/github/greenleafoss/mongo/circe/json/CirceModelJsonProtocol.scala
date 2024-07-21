package io.github.greenleafoss.mongo.circe.json

import io.github.greenleafoss.mongo.core.model.Model

import io.github.greenleafoss.mongo.circe.json.CirceJsonProtocol

import io.circe.generic.semiauto.deriveCodec

trait CirceModelJsonProtocol extends CirceJsonProtocol:
  given modelJsonFormat: JsonFormat[Model] = deriveCodec
