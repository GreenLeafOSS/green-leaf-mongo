package io.github.greenleafoss.mongo.core.model

import org.mongodb.scala.bson.ObjectId

import java.time.ZonedDateTime

final case class Model(
    id: Option[ObjectId] = None,
    string: String,
    int: Int,
    long: Long,
    boolean: Boolean,
    zdt: ZonedDateTime,
    opt: Option[String],
    set: Set[Int],
    list: List[Long])
