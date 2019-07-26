package com.atguigu.gmall0225.realtime.app

import com.alibaba.fastjson.JSON
import com.atguigu.gmall0225.common.util.{GmallConstant, MyESUtil}
import com.atguigu.gmall0225.realtime.bean.OrderInfo
import com.atguigu.gmall0225.realtime.util.MyKafkaUtil
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object OrderApp {
    def main(args: Array[String]): Unit = {
        
        //1 . 从kafka消费数据
        val conf = new SparkConf().setMaster("local[2]").setAppName("OrderApp")
        val ssc = new StreamingContext(conf, Seconds(2))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaStream(ssc, GmallConstant.TOPIC_ORDER)
        val orerInfoDStream: DStream[OrderInfo] = sourceDStream.map { // 对数据格式做调整
            case (_, value) => {
                val orderInfo = JSON.parseObject(value, classOf[OrderInfo]) // 李小名 => 李**
                orderInfo.consignee = orderInfo.consignee.substring(0, 1) + "**" // 李小名 => 李**
                orderInfo.consigneeTel = orderInfo.consigneeTel.substring(0, 3) +
                    "****" + orderInfo.consigneeTel.substring(7, 11)
                orderInfo
            }
        }
        
        //2. 把数据写入到es
        
        orerInfoDStream.foreachRDD(rdd => {
            rdd.foreachPartition(orderInfoIt => {
                MyESUtil.insertBulk(GmallConstant.ORDER_INDEX, orderInfoIt.toIterable)
            })
        })
        
        ssc.start()
        ssc.awaitTermination()
        
    }
}
