package io.github.greenleafoss.mongo

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

class NumbersJsonFormatTest extends AnyWordSpec with Matchers {

  import GreenLeafJsonProtocol._

  "NumbersJsonFormats" should {

    "read Int value as JsNumber in JSON" in {
      "1024".parseJson.convertTo[Int] shouldBe 1024
    }

    "read Int value as JsString in JSON" in {
      "\"1024\"".parseJson.convertTo[Int] shouldBe 1024
      "\"\"".parseJson.convertTo[Int] shouldBe 0
    }


    "read Long value as JsNumber in JSON" in {
      "4886718345".parseJson.convertTo[Long] shouldBe 0x123456789L
    }

    "read Long value as JsString in JSON" in {
      "\"4886718345\"".parseJson.convertTo[Long] shouldBe 0x123456789L
      "\"0\"".parseJson.convertTo[Long] shouldBe 0L
    }


    "read Float value as JsNumber in JSON" in {
      "3.1415".parseJson.convertTo[Float] shouldBe 3.1415f
    }

    "read Float value as JsString in JSON" in {
      "\"3.1415\"".parseJson.convertTo[Float] shouldBe 3.1415f
      "\"\"".parseJson.convertTo[Float] shouldBe 0f
    }


    "read Double value as JsNumber in JSON" in {
      "3.1415926535".parseJson.convertTo[Double] shouldBe 3.1415926535d
    }

    "read Double value as JsString in JSON" in {
      "\"3.1415926535\"".parseJson.convertTo[Double] shouldBe 3.1415926535d
      "\"\"".parseJson.convertTo[Double] shouldBe 0d
    }


    "read Byte value as JsNumber in JSON" in {
      "0".parseJson.convertTo[Byte] shouldBe 0.toByte
      "1".parseJson.convertTo[Byte] shouldBe 1.toByte
    }

    "read Byte value as JsString in JSON" in {
      "\"0\"".parseJson.convertTo[Byte] shouldBe 0.toByte
      "\"1\"".parseJson.convertTo[Byte] shouldBe 1.toByte
      "\"\"".parseJson.convertTo[Byte] shouldBe 0.toByte
    }


    "read Short value as JsNumber in JSON" in {
      "0".parseJson.convertTo[Short] shouldBe 0.toShort
      "1".parseJson.convertTo[Short] shouldBe 1.toShort
    }

    "read Short value as JsString in JSON" in {
      "\"0\"".parseJson.convertTo[Short] shouldBe 0.toShort
      "\"1\"".parseJson.convertTo[Short] shouldBe 1.toShort
      "\"\"".parseJson.convertTo[Short] shouldBe 0.toShort
    }


    "read BigDecimal value as JsNumber in JSON" in {
      "3.141592653589793238462643383279".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("3.141592653589793238462643383279")
      "2.718281828459045235360287471352".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("2.718281828459045235360287471352")
    }

    "read BigDecimal value as JsString in JSON" in {
      "\"3.141592653589793238462643383279\"".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("3.141592653589793238462643383279")
      "\"2.718281828459045235360287471352\"".parseJson.convertTo[BigDecimal] shouldBe BigDecimal("2.718281828459045235360287471352")
      "\"\"".parseJson.convertTo[BigDecimal] shouldBe BigDecimal(0)
    }


    "read BigInt value as JsNumber in JSON" in {
      "3316923598096294713661".parseJson.convertTo[BigInt] shouldBe BigInt("3316923598096294713661")
      "1000000000000066600000000000001".parseJson.convertTo[BigInt] shouldBe BigInt("1000000000000066600000000000001")
    }

    "read BigInt value as JsString in JSON" in {
      "\"3316923598096294713661\"".parseJson.convertTo[BigInt] shouldBe BigInt("3316923598096294713661")
      "\"1000000000000066600000000000001\"".parseJson.convertTo[BigInt] shouldBe BigInt("1000000000000066600000000000001")
      "\"\"".parseJson.convertTo[BigInt] shouldBe BigInt(0)
    }

  }

}
