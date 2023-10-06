package io.github.greenleafoss.mongo.core.model

import io.github.greenleafoss.mongo.core.util.ZonedDateTimeOps

import org.mongodb.scala.bson.ObjectId

object Models:
  val default: Model = Model(
    id = Some(new ObjectId("6513d8b74729ff3782b39571")),
    string = "STRING",
    int = 127,
    long = 1234567890L,
    boolean = true,
    zdt = ZonedDateTimeOps.parseZonedDateTime("1970-01-01T00:00:00Z"),
    opt = Some("defined"),
    set = Set(1, 2, 3),
    list = List(100L, 200L, 300L)
  )

  val defaultJson: String =
    """
      |{
      |  "boolean": true,
      |  "id": "6513d8b74729ff3782b39571",
      |  "int": 127,
      |  "list": [
      |    100,
      |    200,
      |    300
      |  ],
      |  "long": 1234567890,
      |  "opt": "defined",
      |  "set": [
      |    1,
      |    2,
      |    3
      |  ],
      |  "string": "STRING",
      |  "zdt": "1970-01-01T00:00:00Z"
      |}
      |""".stripMargin

  val defaultBson: String =
    """
      |{
      |  "boolean": true,
      |  "id": {
      |    "$oid": "6513d8b74729ff3782b39571"
      |  },
      |  "int": {
      |    "$numberInt": "127"
      |  },
      |  "long": {
      |    "$numberLong": "1234567890"
      |  },
      |  "string": "STRING",
      |  "zdt": {
      |    "$date": {
      |      "$numberLong": "0"
      |    }
      |  },
      |  "opt": "defined",
      |  "set": [
      |    {
      |      "$numberInt": "1"
      |    },
      |    {
      |      "$numberInt": "2"
      |    },
      |    {
      |      "$numberInt": "3"
      |    }
      |  ],
      |  "list": [
      |    {
      |      "$numberLong": "100"
      |    },
      |    {
      |      "$numberLong": "200"
      |    },
      |    {
      |      "$numberLong": "300"
      |    }
      |  ]
      |}
      |""".stripMargin
