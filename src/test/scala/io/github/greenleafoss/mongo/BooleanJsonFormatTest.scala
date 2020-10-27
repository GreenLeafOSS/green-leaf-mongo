package io.github.greenleafoss.mongo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

class BooleanJsonFormatTest extends AnyWordSpec with Matchers {

  import GreenLeafJsonProtocol._

  "BooleanJsonFormat" should {

    "read Boolean value as JsBoolean in JSON" in {
      "true".parseJson.convertTo[Boolean] shouldBe true
      "false".parseJson.convertTo[Boolean] shouldBe false
    }

    "read Boolean value as JsString in JSON" in {
      "\"true\"".parseJson.convertTo[Boolean] shouldBe true
      "\"TRUE\"".parseJson.convertTo[Boolean] shouldBe true
      "\"false\"".parseJson.convertTo[Boolean] shouldBe false
      "\"FALSE\"".parseJson.convertTo[Boolean] shouldBe false
    }

  }

}