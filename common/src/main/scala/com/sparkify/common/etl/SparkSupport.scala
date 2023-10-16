package com.sparkify.common.etl

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

class SparkSupport extends Logging {

  def getSparkSession(sparkConf: SparkConf): SparkSession = {
    SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()
  }
}
