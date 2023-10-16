package com.sparkify.common.config

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.PutParameterRequest

class SsmSupportSpec extends AnyWordSpec
  with SsmSupport
  with Matchers
  with BeforeAndAfterAll
  with LocalMocksForSpec {

  override val awsSsmClient: SsmClient = buildMockSsmClient()

  private val paramName = "test-parameter"

  override def beforeAll(): Unit = {
    awsSsmClient.putParameter(
      PutParameterRequest.builder()
        .name(paramName)
        .value("test-value")
        .`type`("String")
        .overwrite(true)
        .build()
    )
    awsSsmClient.putParameter(
      PutParameterRequest.builder()
        .name("test-parameter-with-one-key-value-pair")
        .value("""{"test-key":"test-value"}""")
        .`type`("String")
        .overwrite(true)
        .build()
    )
    awsSsmClient.putParameter(
      PutParameterRequest.builder()
        .name("test-parameter-with-multiple-key-value-pairs")
        .value("""{"test-key-1":"test-value-1","test-key-2":"test-value-2"}""")
        .`type`("String")
        .overwrite(true)
        .build()
    )
  }

  //test cases for getParameterStoreValue
  "A call to getParameterStoreValue" should {
    "return a Left if the parameter does not exist or ssm client error" in {
      val result = getParameterStoreValue("non-existent-parameter")
      result.isLeft shouldBe true
    }

    "return a Left with the correct error message if the parameter does not exist" in {
      val result = getParameterStoreValue("non-existent-parameter")
      result.left.get.getMessage should startWith("Parameter non-existent-parameter not found")
    }

    "return a Left with the correct error type if the parameter does not exist" in {
      val result = getParameterStoreValue("non-existent-parameter")
      result.left.get.getClass.getSimpleName shouldBe "ParameterNotFoundException"
    }
  }

  "A call to getParameterStoreValue" should {
    "return a Right if the parameter exists" in {
      val result = getParameterStoreValue(paramName)
      result.isRight shouldBe true
    }

    "return a Right with the correct value if the parameter exists" in {
      val result = getParameterStoreValue(paramName)
      result.right.get shouldBe "test-value"
    }

  }

  //test cases for getParameterStoreJsonValueAsMap
  "A call to getParameterStoreJsonValueAsMap" should {
    "return a Left if the parameter does not exist or ssm client error" in {
      val result = getParameterStoreJsonValueAsMap("non-existent-parameter")
      result.isLeft shouldBe true
    }

    "return a Left with the correct error message if the parameter does not exist" in {
      val result = getParameterStoreJsonValueAsMap("non-existent-parameter")
      result.left.get.getMessage should startWith("Parameter non-existent-parameter not found")
    }

    "return a Left with the correct error type if the parameter does not exist" in {
      val result = getParameterStoreJsonValueAsMap("non-existent-parameter")
      result.left.get.getClass.getSimpleName shouldBe "ParameterNotFoundException"
    }
  }

  "A call to getParameterStoreJsonValueAsMap" should {
    "return a Right if the parameter exists" in {
      val result = getParameterStoreJsonValueAsMap("test-parameter-with-multiple-key-value-pairs")
      result.isRight shouldBe true
    }

    "return a Right with the correct value if the parameter exists" in {
      val result = getParameterStoreJsonValueAsMap("test-parameter-with-one-key-value-pair")
      result.right.get shouldBe Map("test-key" -> "test-value")
    }

    "return a Right with the correct value if the parameter exists and has multiple key value pairs" in {
      val result = getParameterStoreJsonValueAsMap("test-parameter-with-multiple-key-value-pairs")
      result.right.get shouldBe Map("test-key-1" -> "test-value-1", "test-key-2" -> "test-value-2")
    }
  }
}
