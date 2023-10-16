package com.sparkify.common.config

import org.json4s.jackson.JsonMethods
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParameterRequest

import scala.util.Try

/**
 * Provides support to fetch configuration parameters from AWS SSM
 */
trait SsmSupport {

  val ssmPrefix: String = SsmClient.SERVICE_NAME + "://"

  val awsSsmClient: SsmClient = SsmClient.builder().region(Region.US_EAST_1).build()

  def getParameterStoreValue(paramName: String): Either[Throwable, String] = {
    Try {
      awsSsmClient.getParameter(
        GetParameterRequest.builder().name(paramName).withDecryption(true).build()
      ).parameter().value()
    }.toEither
  }

  def getParameterStoreJsonValueAsMap(paramName: String): Either[Throwable, Map[String, String]] = {
    getParameterStoreValue(paramName).map { v =>
      JsonMethods.parse(v).values.asInstanceOf[Map[String, String]]
    }
  }
}
