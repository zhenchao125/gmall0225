package com.atguigu.gmall0225.realtime

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Author lzc
  * Date 2019-08-30 16:51
  */
object SaleDetailApp1 {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("SaleDetailApp")
            .getOrCreate()
        spark.sparkContext.setLogLevel("error")
        
        val orderInfoDF: DataFrame = spark.readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", "hadoop201:9092,hadoop202:9092,hadoop203:9092")
            .option("subscribe", "gmall_order_info") // 也可以订阅多个主题:   "topic1,topic2"
            .load
            .selectExpr("CAST(value AS STRING)", "timestamp")
            .withWatermark("timestamp", "2 minutes")
        
        val orderDetailDF: DataFrame = spark.readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", "hadoop201:9092,hadoop202:9092,hadoop203:9092")
            .option("subscribe", "gmall_order_detail")
            .load
            .selectExpr("CAST(value AS STRING)", "timestamp")
            .withWatermark("timestamp", "2 minutes")
        
        
        
        //        orderInfoDF.writeStream
        //            .outputMode("update")
        //            .format("console")
        //            .option("truncate", false)
        //            .start
        //            .awaitTermination
        
        
    }
}
