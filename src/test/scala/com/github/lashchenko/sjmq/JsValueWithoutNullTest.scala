package com.github.lashchenko.sjmq

import org.scalatest.{Matchers, WordSpec}
import spray.json._

class JsValueWithoutNullTest extends WordSpec with Matchers {

  case class TestNulls(i: Option[Int], l: Option[Long], s: Option[String], n: Option[BigDecimal], b: Option[Boolean])

  object TestNullsBsonProtocol extends ScalaSprayJsonProtocol with ScalaSprayBsonProtocol {
    implicit val testNullsFormat: RootJsonFormat[TestNulls] = jsonFormat5(TestNulls)
  }

  import ScalaSprayMongoQueryDsl.JsValueWithoutNull
  import TestNullsBsonProtocol._

  "JsValueWithoutNull" should {

    "not remove JsNull values from JSON if skipNull(false)" in {
      TestNulls(Some(1), Some(2L), Some("a"), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"b":true,"i":1,"l":{"$numberLong":"2"},"n":3.14,"s":"a"}"""

      TestNulls(None, Some(2L), Some("a"), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"b":true,"i":null,"l":{"$numberLong":"2"},"n":3.14,"s":"a"}"""

      TestNulls(None, None, Some("a"), Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"b":true,"i":null,"l":null,"n":3.14,"s":"a"}"""

      TestNulls(None, None, None, Some(3.14), Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"b":true,"i":null,"l":null,"n":3.14,"s":null}"""

      TestNulls(None, None, None, None, Some(true)).toJson.skipNull(false).compactPrint shouldBe
        """{"b":true,"i":null,"l":null,"n":null,"s":null}"""

      TestNulls(None, None, None, None, None).toJson.skipNull(false).compactPrint shouldBe
        """{"b":null,"i":null,"l":null,"n":null,"s":null}"""
    }

    "remove JsNull values from JSON if skipNull(true)" in {
      TestNulls(Some(1), Some(2L), Some("a"), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"s":"a","n":3.14,"i":1,"b":true,"l":{"$numberLong":"2"}}"""

      TestNulls(None, Some(2L), Some("a"), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true,"l":{"$numberLong":"2"},"n":3.14,"s":"a"}"""

      TestNulls(None, None, Some("a"), Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true,"n":3.14,"s":"a"}"""

      TestNulls(None, None, None, Some(3.14), Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true,"n":3.14}"""

      TestNulls(None, None, None, None, Some(true)).toJson.skipNull().compactPrint shouldBe
        """{"b":true}"""

      TestNulls(None, None, None, None, None).toJson.skipNull().compactPrint shouldBe
        """{}"""
    }

  }

}
