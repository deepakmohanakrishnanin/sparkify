package com.sparkify.common.config

import org.json4s.jackson.JsonMethods
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

import scala.util.Try

trait SecretSupport {

  val secretsManagerClient: SecretsManagerClient = SecretsManagerClient.builder().region(Region.US_EAST_1).build()

  def getSecretValue(secretName: String): Either[Throwable, String] = {
    Try {
      secretsManagerClient.getSecretValue(
        GetSecretValueRequest.builder().secretId(secretName).build()
      ).secretString()
    }.toEither
  }

  def getSecretJsonValueAsMap(secretName: String): Either[Throwable, Map[String, String]] = {
    getSecretValue(secretName).map { v =>
      JsonMethods.parse(v).values.asInstanceOf[Map[String, String]]
    }
  }
}
