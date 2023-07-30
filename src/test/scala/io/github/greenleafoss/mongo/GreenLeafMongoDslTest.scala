package io.github.greenleafoss.mongo

import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import spray.json._

class GreenLeafMongoDslTest
  extends AsyncWordSpec
  with Matchers {

  import GreenLeafBsonProtocol._
  import GreenLeafMongoDsl._

  implicit class JsValueToBson(jsValue: JsValue) {
    def asBson: Bson = Document(jsValue.compactPrint)
  }

  implicit class JsonStringToBson(str: String) {
    def asBson: Bson = Document(str)
  }

  // TODO add tests for custom case classes

  "GreenLeafMongoDsl" should {

    "$eq" in {
      // https://docs.mongodb.com/manual/reference/operator/query/eq/

      ("qty" $eq 20).asBson shouldBe """{ qty: { $eq: 20 } }""".asBson
      ("qty" $eq 20L).asBson shouldBe """{ qty: { $eq: 20 } }""".asBson
      ("qty" $eq 0x123456789L).asBson shouldBe """{ qty: { $eq: { $numberLong: "4886718345" } } }""".asBson
      ("qty" $eq 20.0f).asBson shouldBe """{ qty: { $eq: 20.0 } }""".asBson
      ("qty" $eq 20.0d).asBson shouldBe """{ qty: { $eq: 20.0 } }""".asBson
      ("qty" $eq BigDecimal(20.0)).asBson shouldBe """{ qty: { $eq: { $numberDecimal: "20.0" } } }""".asBson
      ("item.name" $eq "ab").asBson shouldBe """{ "item.name": { $eq: "ab" } }""".asBson
      ("tags" $eq "B").asBson shouldBe """{ tags: { $eq: "B" } }""".asBson
      ("tags" $eq ("A", "B")).asBson shouldBe """{ tags: { $eq: [ "A", "B" ] } }""".asBson
    }

    "$is ($eq operator alias)" in {
      // https://docs.mongodb.com/manual/reference/operator/query/eq/

      ("qty" $is 20).asBson shouldBe """{ qty: { $eq: 20 } }""".asBson
      ("qty" $is 20L).asBson shouldBe """{ qty: { $eq: 20 } }""".asBson
      ("qty" $is 0x123456789L).asBson shouldBe """{ qty: { $eq: { $numberLong: "4886718345" } } }""".asBson
      ("qty" $is 20.0f).asBson shouldBe """{ qty: { $eq: 20.0 } }""".asBson
      ("qty" $is 20.0d).asBson shouldBe """{ qty: { $eq: 20.0 } }""".asBson
      ("qty" $is BigDecimal(20.0)).asBson shouldBe """{ qty: { $eq: { $numberDecimal: "20.0" } } }""".asBson
      ("item.name" $is "ab").asBson shouldBe """{ "item.name": { $eq: "ab" } }""".asBson
      ("tags" $is "B").asBson shouldBe """{ tags: { $eq: "B" } }""".asBson
      ("tags" $is ("A", "B")).asBson shouldBe """{ tags: { $eq: [ "A", "B" ] } }""".asBson
    }

    "$gt" in {
      // https://docs.mongodb.com/manual/reference/operator/query/gt/

      ("qty" $gt 20).asBson shouldBe """{ qty: { $gt: 20 } }""".asBson
      ("carrier.fee" $gt 2).asBson shouldBe """{ "carrier.fee": { $gt: 2 } }""".asBson
    }

    "$gte" in {
      // https://docs.mongodb.com/manual/reference/operator/query/gte/

      ("qty" $gte 20).asBson shouldBe """{ qty: { $gte: 20 } }""".asBson
      ("carrier.fee" $gte 2).asBson shouldBe """{ "carrier.fee": { $gte: 2 } }""".asBson
    }

    "$in" in {
      // https://docs.mongodb.com/manual/reference/operator/query/in/

      ("qty" $in Seq(5, 15)).asBson shouldBe """{ qty: { $in: [ 5, 15 ] } }""".asBson
      ("qty" $in Seq(2.7, 3.1415)).asBson shouldBe """{ qty: { $in: [ 2.7, 3.1415] } }""".asBson
      ("qty" $in Seq(0x123456789L, 128, 256, 512)).asBson shouldBe """{ qty: { $in: [ { $numberLong: "4886718345" }, 128, 256, 512 ] } }""".asBson
      ("tags" $in Seq("appliances", "school")).asBson shouldBe """{ tags: { $in: ["appliances", "school"] } }""".asBson
      // Impossible too use $regex inside $in query https://jira.mongodb.org/browse/SERVER-14595
      ("tags" $in Seq("^be".r, "^st".r)).asBson shouldBe """{ tags: { "$in" : [ { "$regex": "^be" }, { "$regex": "^st" }] } }""".asBson
    }

    "$lt" in {
      // https://docs.mongodb.com/manual/reference/operator/query/lt/

      ("qty" $lt 20).asBson shouldBe """{ qty: { $lt: 20 } }""".asBson
      ("carrier.fee" $lt 20).asBson shouldBe """{ "carrier.fee": { $lt: 20 } }""".asBson
    }

    "$lte" in {
      // https://docs.mongodb.com/manual/reference/operator/query/lte/

      ("qty" $lte 20).asBson shouldBe """{ qty: { $lte: 20 } }""".asBson
      ("carrier.fee" $lte 5).asBson shouldBe """{ "carrier.fee": { $lte: 5 } }""".asBson
    }

    "$ne" in {
      // https://docs.mongodb.com/manual/reference/operator/query/ne/

      ("qty" $ne 20).asBson shouldBe """{ qty: { $ne: 20 } }""".asBson
      ("carrier.state" $ne "NY").asBson shouldBe """{ "carrier.state": { $ne: "NY" } }""".asBson
    }

    "$nin" in {
      // https://docs.mongodb.com/manual/reference/operator/query/nin/

      ("qty" $nin Seq(5, 15)).asBson shouldBe """{ qty: { $nin: [ 5, 15 ] } }""".asBson
      ("tags" $nin Seq("appliances", "school")).asBson shouldBe """{ tags: { $nin: [ "appliances", "school" ] } }""".asBson
    }

    "$and" in {
      // https://docs.mongodb.com/manual/reference/operator/query/and/

      $and(Seq("price" $ne 1.99, "price" $exists true)).asBson shouldBe
        """{$and: [{price: {$ne: 1.99}}, {price: {$exists :true}} ]}""".asBson

      $and(
        Seq(
          $or(Seq("price" $eq 0.99, "price" $eq 1.99)),
          $or(Seq("sale" $eq true, "qty" $lt 20))
        )
      ).asBson shouldBe
        """
          |{
          |  $and: [
          |    { $or: [ { price: { $eq: 0.99 } }, { price: { $eq: 1.99 } } ] },
          |    { $or: [ { sale: {$eq: true } }, { qty: { $lt : 20 } } ] }
          |  ]
          |}
        """.stripMargin.asBson
    }

    "$not" in {
      // https://docs.mongodb.com/manual/reference/operator/query/not/

      ("price" $not { _ $gt 1.99 }).asBson shouldBe """{ price: { $not: { $gt: 1.99 } } }""".asBson

      ("item" $not { _ $regex "^p.*".r }).asBson shouldBe
        """{ item: { "$not" : { "$regex" : "^p.*", "$options" : "" } } }""".asBson
    }

    "$nor" in {
      // https://docs.mongodb.com/manual/reference/operator/query/nor/

      $nor(Seq("price" $eq 1.99, "sale" $eq true)).asBson shouldBe
        """{ $nor: [ { price: { $eq: 1.99 } }, { sale: { $eq: true } } ]  }""".asBson

      $nor(Seq("price" $eq 1.99, "qty" $lt 20, "sale" $eq true)).asBson shouldBe
        """{ $nor: [ { price: { $eq: 1.99 } }, { qty: { $lt: 20 } }, { sale: { $eq: true } } ] }""".asBson

      $nor(
        Seq(
          "price" $eq 1.99,
          "price" $exists false,
          "sale" $eq true,
          "sale" $exists false
        )
      ).asBson shouldBe
        """
          |{
          |  $nor: [
          |    { price: { $eq: 1.99 } },
          |    { price: { $exists: false } },
          |    { sale: { $eq: true } },
          |    { sale: { $exists: false } }
          |  ]
          |}
        """.stripMargin.asBson
    }

    "$or" in {
      // https://docs.mongodb.com/manual/reference/operator/query/or/

      $or(Seq("quantity" $lt 20, "price" $eq 10)).asBson shouldBe
        """{ $or: [ { quantity: { $lt: 20 } }, { price: { $eq: 10 } } ] }""".asBson
    }

    "$exists" in {
      // https://docs.mongodb.com/manual/reference/operator/query/exists/

      $and(Seq("qty" $exists true, "qty" $nin Seq(5, 15))).asBson shouldBe
        """{ $and: [ { qty: {$exists: true} }, { qty: { $nin: [5,15] } } ] }""".asBson
    }

    "$type" in {
      // https://docs.mongodb.com/manual/reference/operator/query/type/
      // TODO add support of this operator
      "$type" shouldBe "$type"
    }

    "$expr" in {
      // https://docs.mongodb.com/manual/reference/operator/query/expr/
      // TODO add support of this operator
      "$expr" shouldBe "$expr"
    }

    "$jsonSchema" in {
      // https://docs.mongodb.com/manual/reference/operator/query/jsonSchema/
      // TODO add support of this operator
      "$jsonSchema" shouldBe "$jsonSchema"
    }

    "$mod" in {
      // https://docs.mongodb.com/manual/reference/operator/query/mod/
      // TODO add support of this operator
      "$mod" shouldBe "$mod"
    }

    "$regex" in {
      // https://docs.mongodb.com/manual/reference/operator/query/regex/

      $and(Seq("name" $regex "acme.*corp", "name" $nin Seq("acmeblahcorp"))).asBson shouldBe
        """
          |{
          |  $and: [
          |    { "name" : { "$regex" : "acme.*corp" } },
          |    { "name" : { "$nin" : ["acmeblahcorp"] } }
          |  ]
          |}
        """.stripMargin.asBson

      $and(Seq("name" $regex ("acme.*corp", "i"), "name" $nin Seq("acmeblahcorp"))).asBson shouldBe
        """
          |{
          |  $and: [
          |    { "name" : { "$regex" : "acme.*corp", "$options" : "i" } },
          |    { "name" : { "$nin" : ["acmeblahcorp"] } }
          |  ]
          |}
        """.stripMargin.asBson

      $and(Seq("name" $regex "acme.*corp".r, "name" $nin Seq("acmeblahcorp"))).asBson shouldBe
        """
          |{
          |  $and: [
          |    { "name" : { "$regex" : "acme.*corp", "$options" : "" } },
          |    { "name" : { "$nin" : ["acmeblahcorp"] } }
          |  ]
          |}
        """.stripMargin.asBson

    }

    "$text" in {
      // https://docs.mongodb.com/manual/reference/operator/query/text/
      // TODO add support of this operator
      "$text" shouldBe "$text"
    }

    "$where" in {
      // https://docs.mongodb.com/manual/reference/operator/query/where/
      // TODO add support of this operator
      "$where" shouldBe "$where"
    }

    "Geospatial Query Operators" in {
      // https://docs.mongodb.com/manual/reference/operator/query-geospatial/
      // TODO add support of these operators
      "Geospatial Query Operators" shouldBe "Geospatial Query Operators"
    }

    "$all" in {
      // https://docs.mongodb.com/manual/reference/operator/query/all/

      ("tags" $all Seq("ssl", "security")).asBson shouldBe """{ tags: { $all: [ "ssl" , "security" ] } }""".asBson

      ("qty.num" $all Seq(50)).asBson shouldBe """{ "qty.num": { $all: [ 50 ] } }""".asBson

      (
        "qty" $all Seq(
          $elemMatch ($and(Seq("size" $eq "M", "num" $gt 50))),
          $elemMatch ($and(Seq("num" $eq 100, "color" $eq "green")))
        )
      ).asBson shouldBe
        """
          |{
          |  "qty": {
          |    "$all": [
          |      { "$elemMatch": { "$and": [ { "size": { "$eq": "M" } }, { "num": { "$gt": 50 } } ] } },
          |      { "$elemMatch": { "$and": [ { "num": { "$eq": 100 } }, { "color": { "$eq": "green" } } ] } }
          |    ]
          |  }
          |}
        """.stripMargin.asBson
    }

    "$elemMatch" in {
      // https://docs.mongodb.com/manual/reference/operator/query/elemMatch/

      ("results" $elemMatch JsObject("$gte" -> JsNumber(80), "$lt" -> JsNumber(85))).asBson shouldBe
        """{ results: { $elemMatch: { $gte: 80, $lt: 85 } } }""".asBson

      ("results" $elemMatch Map("$gte" -> 80, "$lt" -> 85)).asBson shouldBe
        """{ results: { $elemMatch: { $gte: 80, $lt: 85 } } }""".asBson

      ("results" $elemMatch $and(Seq("product" $eq "xyz", "score" $gte 8))).asBson shouldBe
        """{ results: { $elemMatch: { $and: [ { product: { $eq: "xyz" } }, { score: { $gte : 8 } }] } } }""".asBson

      ("results" $elemMatch ("product" $eq "xyz")).asBson shouldBe
        """{ results: { $elemMatch: { product: { $eq: "xyz" } } } }""".asBson
    }

    "$size" in {
      // https://docs.mongodb.com/manual/reference/operator/query/size/

      ("field" $size 2).asBson shouldBe """{ field: { $size: 2 } } """.asBson

      ("field" $size 1).asBson shouldBe """{ field: { $size: 1 } } """.asBson
    }

    "Bitwise Query Operators" in {
      // https://docs.mongodb.com/manual/reference/operator/query-bitwise/
      // TODO add support of these operators
      "Bitwise Query Operators" shouldBe "Bitwise Query Operators"
    }

  }

}
