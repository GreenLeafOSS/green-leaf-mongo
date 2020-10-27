package io.github.greenleafoss.mongo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

class LongJsonAndBsonFormatTest extends AnyWordSpec with Matchers {

  "LongJsonFormat" should {

    "write small (int) value as JsNumber in JSON" in {
      import GreenLeafJsonProtocol._
      1L.toJson shouldBe JsNumber(1)
      1024L.toJson shouldBe JsNumber(1024)
    }

    "write large (long) value as JsNumber in JSON" in {
      import GreenLeafJsonProtocol._
      0x123456789L.toJson shouldBe JsNumber(4886718345L)
    }

    "write small (int) value as number in BSON" in {
      import GreenLeafBsonProtocol._
      1L.toJson shouldBe JsNumber(1)
      1024L.toJson shouldBe JsNumber(1024)
    }

    "write large (long) value as $numberLong in BSON" in {
      import GreenLeafBsonProtocol._
      0x123456789L.toJson shouldBe JsObject("$numberLong" -> JsString("4886718345"))
    }

  }

}
