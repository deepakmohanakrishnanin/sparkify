package com.sparkify.common.config

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import software.amazon.awssdk.services.ssm.model.PutParameterRequest
import pureconfig.generic.auto._
import software.amazon.awssdk.services.ssm.SsmClient

class ConfigSupportSpec extends AnyWordSpec
  with ConfigSupport[TestAppConfig]
  with LocalMocksForSpec
  with Matchers
  with BeforeAndAfterAll {

  override val awsSsmClient: SsmClient = buildMockSsmClient()

  override def beforeAll(): Unit = {
    awsSsmClient.putParameter(
      PutParameterRequest.builder()
        .name("testAppConfig.conf")
        .value("""name = "sparkify-etl-job"
                 |
                 |spark-config = {
                 |    "spark.app.name": ${name},
                 |    "spark.master": "local[*]",
                 |    "spark.sql.session.timeZone": "UTC",
                 | }
                 |""".stripMargin)
        .`type`("String")
        .overwrite(true)
        .build()
    )
  }

  "A call to parse" should {
    "return a Left if the config file does not exist in SSM" in {
      val result = parse("ssm://non-existent-config-file")
      result.isLeft shouldBe true
      result.left.get.getMessage should startWith("Parameter non-existent-config-file not found")
    }

    "return a Left if the config file does not exist in local file system" in {
      val result = parse("non-existent-config-file")
      result.isLeft shouldBe true
      result.left.get.getMessage should startWith ("Failed to parse config file non-existent-config-file")
    }
  }

  "A call to parse" should {
    "return a Right if the config file exists in AWS SSM" in {
      val result = parse("ssm://testAppConfig.conf")
      println(result)
      result.isRight shouldBe true
      result.right.get.name shouldBe "sparkify-etl-job"
      result.right.get.sparkConfig("spark.master") shouldBe "local[*]"
      result.right.get.sparkConfig("spark.app.name") shouldBe "sparkify-etl-job"
      result.right.get.sparkConfig("spark.sql.session.timeZone") shouldBe "UTC"
    }

    "return a Right with the config from local file system" in {
      val result = parse("src/test/resources/job.local.conf")

      result.isRight shouldBe true
      result.right.get.name shouldBe "sparkify-etl-job"
      result.right.get.sparkConfig("spark.master") shouldBe "local[*]"
      result.right.get.sparkConfig("spark.app.name") shouldBe "sparkify-etl-job"
      result.right.get.sparkConfig("spark.hadoop.fs.s3a.endpoint") shouldBe "http://localhost:9999"
    }

    "return a Right with the config from local file system and overridden param" in {
      val result = parse("src/test/resources/job.local.conf", Map("""spark-config."spark.hadoop.fs.s3a.fast.upload.buffer"""" -> "bytebuffer"))

      println(result)
      result.isRight shouldBe true
      result.right.get.name shouldBe "sparkify-etl-job"
      result.right.get.sparkConfig("spark.master") shouldBe "local[*]"
      result.right.get.sparkConfig("spark.app.name") shouldBe "sparkify-etl-job"
      result.right.get.sparkConfig("spark.hadoop.fs.s3a.endpoint") shouldBe "http://localhost:9999"
      result.right.get.sparkConfig("spark.hadoop.fs.s3a.fast.upload.buffer") shouldBe "bytebuffer"
    }
  }
}

case class TestAppConfig(name: String, sparkConfig: Map[String, String] = Map.empty)
