package com.atguigu.gmall0225.hive2s

import com.atguigu.gmall0225.common.util.{GmallConstant, MyESUtil}
import com.atguigu.gmall0225.hive2s.bean.SaleDetailDayCount
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Author lzc
  * Date 2019-07-26 16:04
  */
object SaleDetailApp {
    def main(args: Array[String]): Unit = {
        
        //1. 从hive读: spark-sql
        val spark: SparkSession = SparkSession.builder()
            .master("local[2]")
            .appName("SaleDetailApp")
            .enableHiveSupport()
            .getOrCreate()
        
        val sql =
            s"""
               |select
               |    user_id,
               |    sku_id,
               |    user_gender,
               |    cast(user_age as int) user_age,
               |    user_level,
               |    cast(order_price as double) order_price,
               |    sku_name,
               |    sku_tm_id,
               |    sku_category3_id,
               |    sku_category2_id,
               |    sku_category1_id,
               |    sku_category3_name,
               |    sku_category2_name,
               |    sku_category1_name,
               |    spu_id,
               |    sku_num,
               |    cast(order_count as bigint) order_count,
               |    cast(order_amount as double) order_amount,
               |    dt
               |from dws_sale_detail_daycount
               |where dt='2019-05-20'
             """.stripMargin
        import spark.implicits._
        spark.sql("use gmall")
        val ds: Dataset[SaleDetailDayCount] = spark.sql(sql).as[SaleDetailDayCount]
        
        // 2. 写到es
        ds.foreachPartition(it => {
            MyESUtil.insertBulk(GmallConstant.SALE_DETAIL_INDEX, it.toIterable)
        })
    }
}
