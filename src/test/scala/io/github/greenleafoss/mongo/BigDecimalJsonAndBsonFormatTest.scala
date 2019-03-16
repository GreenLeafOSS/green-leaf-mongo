package io.github.greenleafoss.mongo

import org.scalatest.{Matchers, WordSpec}
import spray.json._

class BigDecimalJsonAndBsonFormatTest extends WordSpec with Matchers {

  "BigDecimalJsonFormat" should {

    "write as JsNumber in JSON" in {
      import GreenLeafJsonProtocol._
      BigDecimal("3.1415").toJson shouldBe JsNumber("3.1415")
    }

    "read value from JsNumber in JSON" in {
      import GreenLeafJsonProtocol._
      "3.1415".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("3.1415")
    }

    "write value as $numberDecimal in BSON" in {
      import GreenLeafBsonProtocol._
      BigDecimal("3.1415").toJson shouldBe JsObject("$numberDecimal" -> JsString("3.1415"))
    }

    "read value from $numberDecimal in BSON" in {
      import GreenLeafBsonProtocol._
      """{"$numberDecimal": "3.1415"}""".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("3.1415")
    }

  }

}
