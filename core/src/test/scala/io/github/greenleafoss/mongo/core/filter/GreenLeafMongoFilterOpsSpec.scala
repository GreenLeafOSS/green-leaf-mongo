package io.github.greenleafoss.mongo.core.filter

import io.github.greenleafoss.mongo.core.json.GreenLeafMongoJsonBasicFormats
import io.github.greenleafoss.mongo.core.util.GreenLeafJsonBsonOps

import org.mongodb.scala.bson.BsonValue

import scala.language.implicitConversions

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

trait GreenLeafMongoFilterOpsSpec extends AnyWordSpec with Matchers with GreenLeafMongoFilterOps:

  this: GreenLeafJsonBsonOps with GreenLeafMongoJsonBasicFormats =>

  "FilterOps" when:
    "$eq" in:
      // https://mongodb.com/docs/manual/reference/operator/query/eq/

      ("qty" $eq 20) shouldBe """{ qty: { $eq: { $numberInt: "20" } } }""".parseBson
      ("qty" $eq 20L) shouldBe """{ qty: { $eq: { $numberLong: "20" } } }""".parseBson
      ("qty" $eq 0x123456789L) shouldBe """{ qty: { $eq: { $numberLong: "4886718345" } } }""".parseBson
      ("qty" $eq 20.0f) shouldBe """{ qty: { $eq: { $numberDouble: "20.0" } } }""".parseBson
      ("qty" $eq 20.0d) shouldBe """{ qty: { $eq: { $numberDouble: "20.0" } } }""".parseBson
      ("qty" $eq BigDecimal(20.0)) shouldBe """{ qty: { $eq: { $numberDecimal: "20.0" } } }""".parseBson
      ("item.name" $eq "ab") shouldBe """{ "item.name": { $eq: "ab" } }""".parseBson
      ("tags" $eq "B") shouldBe """{ tags: { $eq: "B" } }""".parseBson
      ("tags" $eq Seq("A", "B")) shouldBe """{ tags: { $eq: [ "A", "B" ] } }""".parseBson
      ("qty" $eq Seq(1, 2)) shouldBe """{ qty: { $eq: [ { $numberInt: "1" }, { $numberInt: "2" } ] } }""".parseBson

    "$is ($eq operator alias)" in:
      // https://mongodb.com/docs/manual/reference/operator/query/eq/

      ("qty" $is 20) shouldBe """{ qty: { $eq: { $numberInt: "20" } } }""".parseBson
      ("qty" $is 20L) shouldBe """{ qty: { $eq: { $numberLong: "20" } } }""".parseBson
      ("qty" $is 0x123456789L) shouldBe """{ qty: { $eq: { $numberLong: "4886718345" } } }""".parseBson
      ("qty" $is 20.0f) shouldBe """{ qty: { $eq: { $numberDouble: "20.0" } } }""".parseBson
      ("qty" $is 20.0d) shouldBe """{ qty: { $eq: { $numberDouble: "20.0" } } }""".parseBson
      ("qty" $is BigDecimal(20.0)) shouldBe """{ qty: { $eq: { $numberDecimal: "20.0" } } }""".parseBson
      ("item.name" $is "ab") shouldBe """{ "item.name": { $eq: "ab" } }""".parseBson
      ("tags" $is "B") shouldBe """{ tags: { $eq: "B" } }""".parseBson
      ("tags" $is Seq("A", "B")) shouldBe """{ tags: { $eq: [ "A", "B" ] } }""".parseBson

    "$gt" in:
      // https://mongodb.com/docs/manual/reference/operator/query/gt/

      ("qty" $gt 20) shouldBe """{ qty: { $gt: { $numberInt: "20" } } }""".parseBson
      ("carrier.fee" $gt 2) shouldBe """{ "carrier.fee": { $gt: { $numberInt: "2" } } }""".parseBson

    "$gte" in:
      // https://mongodb.com/docs/manual/reference/operator/query/gte/

      ("qty" $gte 20) shouldBe """{ qty: { $gte: { $numberInt: "20" } } }""".parseBson
      ("carrier.fee" $gte 2) shouldBe """{ "carrier.fee": { $gte: { $numberInt: "2" } } }""".parseBson

    "$in" in:
      // https://mongodb.com/docs/manual/reference/operator/query/in/

      ("qty" $in Seq(5, 15)) shouldBe """{ qty: { $in: [ 5, 15 ] } }""".parseBson
      // {"qty"={"$in"=BsonArray{values=[BsonDouble{value=2.700000047683716}, BsonDouble{value=3.1414999961853027}]}}}
      // ("qty" $in Seq(2.7d, 3.1415d)) shouldBe """{ qty: { $in: [ 2.7, 3.1415] } }""".parseBson
      ("qty" $in Seq(
        BigDecimal("2.7"),
        BigDecimal("3.1415")
      )) shouldBe """{ qty: { $in: [ { $numberDecimal: "2.7" }, { $numberDecimal: "3.1415" }] } }""".parseBson
      ("qty" $in Seq(
        0x123456789L,
        128,
        256,
        512
      )) shouldBe """{ qty: { $in: [ { $numberLong: "4886718345" }, 128, 256, 512 ] } }""".parseBson
      ("tags" $in Seq("appliances", "school")) shouldBe """{ tags: { $in: ["appliances", "school"] } }""".parseBson

    // Impossible too use $regex inside $in query https://jira.mongodb.org/browse/SERVER-14595
    // ("tags" $in Seq(
    //  "^be".r,
    //  "^st".r
    // )) shouldBe """{ tags: { "$in" : [ {"$regularExpression": {"pattern": "^be", "options": ""}}, {"$regularExpression": {"pattern": "^st", "options": ""}}] } }""".parseBson

    "$lt" in:
      // https://mongodb.com/docs/manual/reference/operator/query/lt/

      ("qty" $lt 20) shouldBe """{ qty: { $lt: { $numberInt: "20" } } }""".parseBson
      ("carrier.fee" $lt 20) shouldBe """{ "carrier.fee": { $lt: { $numberInt: "20" } } }""".parseBson

    "$lte" in:
      // https://mongodb.com/docs/manual/reference/operator/query/lte/

      ("qty" $lte 20) shouldBe """{ qty: { $lte: { $numberInt: "20" } } }""".parseBson
      ("carrier.fee" $lte 5) shouldBe """{ "carrier.fee": { $lte: { $numberInt: "5" } } }""".parseBson

    "$ne" in:
      // https://mongodb.com/docs/manual/reference/operator/query/ne/

      ("qty" $ne 20) shouldBe """{ qty: { $ne: { $numberInt: "20" } } }""".parseBson
      ("carrier.state" $ne "NY") shouldBe """{ "carrier.state": { $ne: "NY" } }""".parseBson

    "$nin" in:
      // https://mongodb.com/docs/manual/reference/operator/query/nin/

      ("qty" $nin Seq(5, 15)) shouldBe """{ qty: { $nin: [ { $numberInt: "5" }, { $numberInt: "15" } ] } }""".parseBson
      ("tags" $nin Seq("appliances", "school")) shouldBe """{ tags: { $nin: [ "appliances", "school" ] } }""".parseBson

    "$and" in:
      // https://mongodb.com/docs/manual/reference/operator/query/and/

      $and("price" $ne BigDecimal("1.99"), "price" $exists true) shouldBe
        """{$and: [{price: {$ne: { $numberDecimal: "1.99" }}}, {price: {$exists :true}} ]}""".parseBson

      $and(
        $or("price" $eq BigDecimal("0.99"), "price" $eq BigDecimal("1.99")),
        $or("sale" $eq true, "qty" $lt 20)
      ) shouldBe
        """
          |{
          |  $and: [
          |    { $or: [ { price: { $eq: { $numberDecimal: "0.99" } } }, { price: { $eq: { $numberDecimal: "1.99" } } } ] },
          |    { $or: [ { sale: {$eq: true } }, { qty: { $lt : { $numberInt: "20" } } } ] }
          |  ]
          |}
        """.stripMargin.parseBson

    "$not" in:
      // https://mongodb.com/docs/manual/reference/operator/query/not/

      ("price" $not {
        $gt {
          BigDecimal(1.99)
        }
      }) shouldBe """{ price: { $not: { $gt: { $numberDecimal: "1.99" } } } }""".parseBson

      ("item" $not {
        $regex {
          "^p.*".r
        }
      }) shouldBe """{ item: { "$not" : {"$regularExpression": {"pattern": "^p.*", "options": ""}} } }""".parseBson

    "$nor" in:
      // https://mongodb.com/docs/manual/reference/operator/query/nor/

      $nor("price" $eq BigDecimal(1.99), "sale" $eq true) shouldBe
        """{ $nor: [ { price: { $eq: { $numberDecimal: "1.99" } } }, { sale: { $eq: true } } ]  }""".parseBson

      $nor("price" $eq BigDecimal(1.99), "qty" $lt 20, "sale" $eq true) shouldBe
        """{ $nor: [ { price: { $eq: { $numberDecimal: "1.99" } } }, { qty: { $lt: 20 } }, { sale: { $eq: true } } ] }""".parseBson

      $nor(
        "price" $eq BigDecimal(1.99),
        "price" $exists false,
        "sale" $eq true,
        "sale" $exists false
      ) shouldBe
        """
          |{
          |  $nor: [
          |    { price: { $eq: { $numberDecimal: "1.99" } } },
          |    { price: { $exists: false } },
          |    { sale: { $eq: true } },
          |    { sale: { $exists: false } }
          |  ]
          |}
        """.stripMargin.parseBson

    "$or" in:
      // https://mongodb.com/docs/manual/reference/operator/query/or/

      $or("quantity" $lt 20, "price" $eq 10) shouldBe
        """{ $or: [ { quantity: { $lt: 20 } }, { price: { $eq: 10 } } ] }""".parseBson

    "$exists" in:
      // https://mongodb.com/docs/manual/reference/operator/query/exists/

      $and("qty" $exists true, "qty" $nin Seq(5, 15)) shouldBe
        """{ $and: [ { qty: {$exists: true} }, { qty: { $nin: [5,15] } } ] }""".parseBson

    "$type" in:
      // https://mongodb.com/docs/manual/reference/operator/query/type/
      // TODO add support of this operator
      "$type" shouldBe "$type"

    "$expr" in:
      // https://mongodb.com/docs/manual/reference/operator/query/expr/
      // TODO add support of this operator
      "$expr" shouldBe "$expr"

    "$jsonSchema" in:
      // https://mongodb.com/docs/manual/reference/operator/query/jsonSchema/
      // TODO add support of this operator
      "$jsonSchema" shouldBe "$jsonSchema"

    "$mod" in:
      // https://mongodb.com/docs/manual/reference/operator/query/mod/
      // TODO add support of this operator
      "$mod" shouldBe "$mod"

    "$regex" in:
      // https://mongodb.com/docs/manual/reference/operator/query/regex/

      $and("name" $regex "acme.*corp".r, "name" $nin Seq("acmeblahcorp")) shouldBe
        """
          |{
          |  $and: [
          |    { "name" : { "$regularExpression" : { "pattern": "acme.*corp", "options" : "" } } },
          |    { "name" : { "$nin" : ["acmeblahcorp"] } }
          |  ]
          |}
        """.stripMargin.parseBson

      // https://github.com/lampepfl/dotty/issues/15287
      // we can't use overloaded extensions like `def $regex(r: Regex)` and `def $regex(s: String)`
      $and("name" $regex (pattern = "acme.*corp", options = "i"), "name" $nin Seq("acmeblahcorp")) shouldBe
        """
          |{
          |  $and: [
          |    { "name" : { "$regularExpression" : { "pattern": "acme.*corp", "options" : "i" } } },
          |    { "name" : { "$nin" : ["acmeblahcorp"] } }
          |  ]
          |}
        """.stripMargin.parseBson

      $and("name" $regex "acme.*corp".r, "name" $nin Seq("acmeblahcorp")) shouldBe
        """
          |{
          |  $and: [
          |    { "name" : { "$regularExpression" : { "pattern": "acme.*corp", "options" : "" } } },
          |    { "name" : { "$nin" : ["acmeblahcorp"] } }
          |  ]
          |}
        """.stripMargin.parseBson

    "$text" in:
      // https://mongodb.com/docs/manual/reference/operator/query/text/
      // TODO add support of this operator
      "$text" shouldBe "$text"

    "$where" in:
      // https://mongodb.com/docs/manual/reference/operator/query/where/
      // TODO add support of this operator
      "$where" shouldBe "$where"

    "Geospatial Query Operators" in:
      // https://mongodb.com/docs/manual/reference/operator/query-geospatial/
      // TODO add support of these operators
      "Geospatial Query Operators" shouldBe "Geospatial Query Operators"

    "$all" in:
      // https://mongodb.com/docs/manual/reference/operator/query/all/

      ("tags" $all Seq("ssl", "security")) shouldBe """{ tags: { $all: [ "ssl" , "security" ] } }""".parseBson

      ("qty.num" $all Seq(50)) shouldBe """{ "qty.num": { $all: [ 50 ] } }""".parseBson

      (
        "qty" $all Seq(
          $elemMatch($and("size" $eq "M", "num" $gt 50)),
          $elemMatch($and("num" $eq 100, "color" $eq "green"))
        )
      ) shouldBe
        """
          |{
          |  "qty": {
          |    "$all": [
          |      { "$elemMatch": { "$and": [ { "size": { "$eq": "M" } }, { "num": { "$gt": 50 } } ] } },
          |      { "$elemMatch": { "$and": [ { "num": { "$eq": 100 } }, { "color": { "$eq": "green" } } ] } }
          |    ]
          |  }
          |}
        """.stripMargin.parseBson

    "$elemMatch" in:
      // https://mongodb.com/docs/manual/reference/operator/query/elemMatch/

      // { results: { $elemMatch: { product: "xyz", score: { $gte: 8 } } } }
      ("results" $elemMatch $and("product" $eq "xyz", "score" $gte 8)) shouldBe
        """{ results: { $elemMatch: { $and: [ { product: { $eq: "xyz" } }, { score: { $gte : 8 } }] } } }""".parseBson

      ("results" $elemMatch ("product" $eq "xyz")) shouldBe
        """{ results: { $elemMatch: { product: { $eq: "xyz" } } } }""".parseBson

    "$size" in:
      // https://mongodb.com/docs/manual/reference/operator/query/size/

      ("field" $size 2) shouldBe """{ field: { $size: 2 } } """.parseBson

      ("field" $size 1) shouldBe """{ field: { $size: 1 } } """.parseBson

    "Bitwise Query Operators" in:
      // https://mongodb.com/docs/manual/reference/operator/query-bitwise/
      // TODO add support of these operators
      "Bitwise Query Operators" shouldBe "Bitwise Query Operators"
