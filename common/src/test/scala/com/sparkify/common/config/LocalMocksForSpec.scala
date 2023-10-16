package com.sparkify.common.config

import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.ssm.SsmClient

import java.util.UUID

trait LocalMocksForSpec {

  val localMockEndpointUri = new java.net.URI("http://localhost:9999") //run moto server for mocking aws services
  val localMockAccessKey: String = UUID.randomUUID().toString
  val localMockSecretKey: String = UUID.randomUUID().toString

  def buildMockSsmClient(): SsmClient = {
    SsmClient.builder()
      .region(Region.US_EAST_1)
      .endpointOverride(localMockEndpointUri)
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(localMockAccessKey, localMockSecretKey)
        )
      ).build()
  }

  def buildMockSecretManagerClient(): SecretsManagerClient = {
    SecretsManagerClient.builder()
      .region(Region.US_EAST_1)
      .endpointOverride(localMockEndpointUri)
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(localMockAccessKey, localMockSecretKey)
        )
      ).build()
  }

}
