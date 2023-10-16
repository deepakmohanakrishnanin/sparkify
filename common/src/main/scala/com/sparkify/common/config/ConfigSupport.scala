package com.sparkify.common.config

import com.typesafe.config.ConfigFactory
import pureconfig.{ConfigObjectSource, ConfigReader, ConfigSource}
import pureconfig.error.ConfigReaderFailures

import scala.collection.JavaConverters.mapAsJavaMapConverter

trait ConfigSupport[T] extends SsmSupport {

  private def configSource(filePath: String): Either[Throwable, ConfigObjectSource] = {
    if(filePath.startsWith(ssmPrefix)) {
      val ssmPath = filePath.replace(ssmPrefix, "")
      getParameterStoreValue(ssmPath).map(ConfigSource.string)
    } else {
      Right(ConfigSource.file(filePath))
    }
  }

  def parse(configFilePath: String, fallBackConfigMap: Map[String, String] = Map.empty)(implicit r: ConfigReader[T]): Either[Throwable, T] = {

    for {
      configSource <- configSource(configFilePath)
      combineConfigSource = configSource.withFallback(ConfigSource.fromConfig(
        ConfigFactory.parseMap(fallBackConfigMap.asJava)
      ))
      config <- combineConfigSource.load[T] match {
        case Left(failures: ConfigReaderFailures) =>
          println(failures.prettyPrint(1))
          Left(new Exception(s"Failed to parse config file $configFilePath: ${failures.toList.mkString(", ")}"))
        case Right(config) =>
          Right(config)
      }
    } yield {
      config
    }
  }
}
