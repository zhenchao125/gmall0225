package com.atguigu.gmall0225.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.atguigu.gmall0225.common.util.{GmallConstant, MyESUtil}
import com.atguigu.gmall0225.realtime.bean.StartupLog
import com.atguigu.gmall0225.realtime.util.MyKafkaUtil
import com.atguigu.gmall0225.realtime.util.com.atguigu.dw.gmall.realtime.util.RedisUtil
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

/**
  * Author lzc
  * Date 2019-07-23 08:50
  */
object DAUApp {
    def main(args: Array[String]): Unit = {
        // 1. 从kafka消费数据(启动日志)
        val conf = new SparkConf().setAppName("DAUApp").setMaster("local[1]")
        val ssc = new StreamingContext(conf, Seconds(5))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaStream(ssc, GmallConstant.TOPIC_STARTUP)
        
        // 2. 使用redis清洗,
        // 2.1 对数据进行封装
        val startupLogDStream: DStream[StartupLog] = sourceDStream.map {
            case (_, json) => JSON.parseObject(json, classOf[StartupLog])
        }
        // 2.2 写入之前先做过滤
        val filteredStartupLogDStream: DStream[StartupLog] = startupLogDStream.transform(rdd => {
            
            val client: Jedis = RedisUtil.getJedisClient
            val uidSet: util.Set[String] = client.smembers(GmallConstant.REDIS_DAU_KEY + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
            val uidSetBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(uidSet)
            client.close()
            // 去玩重再去过滤  缺点: 保留不一定是第一次启动记录( 重写bean的hashcode和equals)
            //            val distinctedRDD: RDD[StartupLog] = rdd.distinct
            
            // 手动去重
            val distinctedRDD: RDD[StartupLog] = rdd
                .map(log => (log.uid, log))
                .groupByKey()
                .flatMap {
                    case (_, logIt) => logIt.toList.sortBy(_.ts).take(1)
                }
            
            distinctedRDD.filter(startupLog => {
                val uids: util.Set[String] = uidSetBC.value
                // 返回没有写过的
                !uids.contains(startupLog.uid)
            })
        })
        
        // 2.3 写入到redis
        filteredStartupLogDStream.foreachRDD(rdd => {
            rdd.foreachPartition(startupLogIt => {
                // redis客户端
                val client: Jedis = RedisUtil.getJedisClient
                val startupLogList = startupLogIt.toList
                startupLogList.foreach(startupLog => {
                    // 写入到redis的set中
                    client.sadd(GmallConstant.REDIS_DAU_KEY + ":" + startupLog.logDate, startupLog.uid)
                })
                client.close()
                // 3. 写入到es中
                // startupLogDStream 写入到es中
                MyESUtil.insertBulk(GmallConstant.DAU_INDEX, startupLogList)
            })
        })
        
        ssc.start()
        ssc.awaitTermination()
    }
}

/*
日活:
    key                                 value(set)
    "dau:" + 2019-07-23                 uid


 */