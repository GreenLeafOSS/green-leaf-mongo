package io.github.greenleafoss.mongo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

class JsValueWithoutNullTest extends AnyWordSpec with Matchers {

  case class TestNulls(
      i: Option[Int],
      l: Option[Long],
      s: Option[String],
      d: Option[Double],
      n: Option[BigDecimal],
      b: Option[Boolean],
      a: Option[Seq[Option[Int]]] = None)

  object TestNullsBsonProtocol extends GreenLeafBsonProtocol {
    implicit val testNullsFormat: RootJsonFormat[TestNulls] = jsonFormat(TestNulls.apply, "i", "l", "s", "d", "n", "b", "a")
  }

  import GreenLeafMongoDsl.JsValueWithoutNull
  import TestNullsBsonProtocol._

  "JsValueWithoutNull" should {

    "not remove JsNull values from JSON if skipNull(false)" in {
      TestNulls(Some(1), Some(0x123456789L), Some("a"), Some(2.7), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":true,"d":2.7,"i":1,"l":{"$numberLong":"4886718345"},"n":{"$numberDecimal":"3.14"},"s":"a"}"""

      TestNulls(None, Some(0x123456789L), Some("a"), Some(2.7), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":true,"d":2.7,"i":null,"l":{"$numberLong":"4886718345"},"n":{"$numberDecimal":"3.14"},"s":"a"}"""

      TestNulls(None, None, Some("a"), Some(2.7), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":true,"d":2.7,"i":null,"l":null,"n":{"$numberDecimal":"3.14"},"s":"a"}"""

      TestNulls(None, None, None, Some(2.7), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":true,"d":2.7,"i":null,"l":null,"n":{"$numberDecimal":"3.14"},"s":null}"""

      TestNulls(None, None, None, None, Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":true,"d":null,"i":null,"l":null,"n":{"$numberDecimal":"3.14"},"s":null}"""

      TestNulls(None, None, None, None, None, Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":true,"d":null,"i":null,"l":null,"n":null,"s":null}"""

      TestNulls(None, None, None, None, None, None).toJson.skipNull(false).compactPrint shouldBe
        """{"a":null,"b":null,"d":null,"i":null,"l":null,"n":null,"s":null}"""

      TestNulls(None, None, None, None, None, None, Some(Seq(Some(1), None, Some(3)))).toJson.skipNull(false).compactPrint shouldBe
        """{"a":[1,null,3],"b":null,"d":null,"i":null,"l":null,"n":null,"s":null}"""
    }

    "remove JsNull values from JSON if skipNull(true)" in {
      TestNulls(Some(1), Some(0x123456789L), Some("a"), Some(2.7), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"s":"a","n":{"$numberDecimal":"3.14"},"i":1,"b":true,"l":{"$numberLong":"4886718345"},"d":2.7}"""

      TestNulls(None, Some(0x123456789L), Some("a"), Some(2.7), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"s":"a","n":{"$numberDecimal":"3.14"},"b":true,"l":{"$numberLong":"4886718345"},"d":2.7}"""

      TestNulls(None, None, Some("a"), Some(2.7), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true,"d":2.7,"n":{"$numberDecimal":"3.14"},"s":"a"}"""

      TestNulls(None, None, None, Some(2.7), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true,"d":2.7,"n":{"$numberDecimal":"3.14"}}"""

      TestNulls(None, None, None, None, Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true,"n":{"$numberDecimal":"3.14"}}"""

      TestNulls(None, None, None, None, None, Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true}"""

      TestNulls(None, None, None, None, None, None).toJson.skipNull().compactPrint shouldBe
        """{}"""

      TestNulls(None, None, None, None, None, None, Some(Seq(Some(1), None, Some(3)))).toJson.skipNull().compactPrint shouldBe
        """{"a":[1,3]}"""
    }

  }

}
