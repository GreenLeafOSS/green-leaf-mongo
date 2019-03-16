package com.github.lashchenko.sjmq

import org.scalatest.{Matchers, WordSpec}
import spray.json._

class BigDecimalJsonAndBsonFormatTest extends WordSpec with Matchers {

  "BigDecimalJsonFormat" should {

    "write as JsNumber in JSON" in {
      import ScalaSprayJsonProtocol._
      BigDecimal("3.1415").toJson shouldBe JsNumber("3.1415")
    }

    "read value from JsNumber in JSON" in {
      import ScalaSprayJsonProtocol._
      "3.1415".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("3.1415")
    }

    "write value as $numberDecimal in BSON" in {
      import ScalaSprayBsonProtocol._
      BigDecimal("3.1415").toJson shouldBe JsObject("$numberDecimal" -> JsString("3.1415"))
    }

    "read value from $numberDecimal in BSON" in {
      import ScalaSprayBsonProtocol._
      """{"$numberDecimal": "3.1415"}""".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("3.1415")
    }

  }

}
