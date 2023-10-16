package com.sparkify.common.config

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest

import java.util.UUID

class SecretSupportSpec extends AnyWordSpec
  with SecretSupport
  with LocalMocksForSpec
  with Matchers
  with BeforeAndAfterAll {

  override val secretsManagerClient: SecretsManagerClient = buildMockSecretManagerClient()

  private val testSecretName = UUID.randomUUID().toString
  private val testSecretValue = UUID.randomUUID().toString
  private val testJsonSecretName = UUID.randomUUID().toString

  override def beforeAll(): Unit = {
    secretsManagerClient.createSecret(
      CreateSecretRequest.builder()
        .name(testSecretName)
        .secretString(testSecretValue)
        .description("test-secret-description")
        .forceOverwriteReplicaSecret(true)
        .build()
    )

    secretsManagerClient.createSecret(
      CreateSecretRequest.builder()
        .name(testJsonSecretName)
        .secretString("""{"test-key":"test-value"}""")
        .description("test-secret-description")
        .forceOverwriteReplicaSecret(true)
        .build()
    )
  }

  //test cases for getSecretValue
  "A call to getSecretValue" should {
    "return a Left if the secret does not exist or ssm client error" in {
      val result = getSecretValue("non-existent-secret")
      result.isLeft shouldBe true
    }

    "return a Left with the correct error message if the secret does not exist" in {
      val result = getSecretValue("non-existent-secret")
      result.left.get.getMessage should startWith("Secrets Manager can't find the specified secret")
    }

    "return a Left with the correct error type if the secret does not exist" in {
      val result = getSecretValue("non-existent-secret")
      result.left.get.getClass.getSimpleName shouldBe "ResourceNotFoundException"
    }
  }

  "A call to getSecretValue" should {
    "return a Right if the secret exists" in {
      val result = getSecretValue(testSecretName)
      result.isRight shouldBe true
      result.right.get shouldBe testSecretValue
    }
  }

  //test cases for getSecretJsonValueAsMap
  "A call to getSecretJsonValueAsMap" should {
    "return a Left if the secret does not exist or ssm client error" in {
      val result = getSecretJsonValueAsMap("non-existent-secret")
      result.isLeft shouldBe true
    }

    "return a Left with the correct error message if the secret does not exist" in {
      val result = getSecretJsonValueAsMap("non-existent-secret")
      result.left.get.getMessage should startWith("Secrets Manager can't find the specified secret")
      result.left.get.getClass.getSimpleName shouldBe "ResourceNotFoundException"
    }
  }

  "A call to getSecretJsonValueAsMap" should {
    "return a Right if the secret exists" in {
      val result = getSecretJsonValueAsMap(testJsonSecretName)
      result.isRight shouldBe true
      result.right.get shouldBe Map("test-key" -> "test-value")
    }
  }
}
