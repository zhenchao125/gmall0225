package com.atguigu.gmall0225.realtime.app

import com.atguigu.gmall0225.common.util.GmallConstant
import com.atguigu.gmall0225.realtime.util.MyKafkaUtil
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Author lzc
  * Date 2019-07-23 08:50
  */
object AlertApp {
    def main(args: Array[String]): Unit = {
        // 1. 从kafka消费数据(事件日志)
        val conf: SparkConf = new SparkConf().setAppName("DAUApp").setMaster("local[1]")
        val ssc = new StreamingContext(conf, Seconds(5))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaStream(ssc, GmallConstant.TOPIC_EVENT)
        sourceDStream.print
        // 2. 数据调整成样例类结构, 方便后续使用
        
        
        // 3. window 窗口
        
        // 4. 按照 uid 分组
        
        ssc.start()
        ssc.awaitTermination()
    }
}

