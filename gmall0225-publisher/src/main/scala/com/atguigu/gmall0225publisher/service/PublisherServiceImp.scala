package com.atguigu.gmall0225publisher.service

import java.util

import com.atguigu.gmall0225.common.util.GmallConstant
import io.searchbox.client.JestClient
import io.searchbox.core.search.aggregation.TermsAggregation
import io.searchbox.core.{Search, SearchResult}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable

@Service
class PublisherServiceImp extends PublisherService {
    // 1. 连接到es的客户端
    @Autowired
    var esClinet: JestClient = _
    
    /**
      * 返回指定日期的日活数据
      *
      * @param date
      * @return
      */
    override def getDauTotal(date: String): Long = {
        
        // 2. source
        val dsl =
            s"""
               |{
               |  "query": {
               |    "bool": {
               |      "filter": {
               |        "term": {
               |          "logDate": "$date"
               |        }
               |      }
               |    }
               |  }
               |}
             """.stripMargin
        
        // 3. 创建 Search
        val search: Search = new Search.Builder(dsl)
            .addIndex(GmallConstant.DAU_INDEX)
            .addType("_doc")
            .build()
        
        // 4. 执行
        val result: SearchResult = esClinet.execute(search)
        
        // 5. 获取日活, 并返回
        result.getTotal.toLong
    }
    
    /**
      * 返回具体的小时日活
      *
      * Map[11->100, 12->200,....]
      *
      * @param date
      * @return
      */
    override def getDauHour(date: String): Map[String, Long] = {
        val dsl =
            s"""
               |{
               |  "query": {
               |    "bool": {
               |      "filter": {
               |        "term": {
               |          "logDate": "$date"
               |        }
               |      }
               |    }
               |  },
               |  "aggs": {
               |    "groupby_hour": {
               |      "terms": {
               |        "field": "logHour",
               |        "size": 24
               |      }
               |    }
               |  }
               |}
             """.stripMargin
        
        val search: Search = new Search.Builder(dsl)
            .addIndex(GmallConstant.DAU_INDEX)
            .addType("_doc")
            .build()
        
        val result: SearchResult = esClinet.execute(search)
        
        // 得到聚合结果
        val buckets: util.List[TermsAggregation#Entry] = result.
            getAggregations.getTermsAggregation("groupby_hour").getBuckets
        
        val map = mutable.Map[String, Long]()
        for (i <- 0 until buckets.size) {
            val bucket = buckets.get(i)
            map += bucket.getKey -> bucket.getCount
        }
        map.toMap
    }
    
    /**
      * 返回指定日期的订单的总额
      *
      * @param date
      * @return
      */
    override def getOrderTotal(date: String): Double = {
        val dsl =
            s"""
               |{
               |  "query": {
               |    "bool": {
               |      "filter": {
               |        "term": {
               |          "createDate": "$date"
               |        }
               |      }
               |    }
               |  },
               |  "aggs": {
               |    "sum_totalAmount": {
               |      "sum": {
               |        "field": "totalAmount"
               |      }
               |    }
               |  }
               |}
             """.stripMargin
    
        val search: Search = new Search.Builder(dsl)
            .addIndex(GmallConstant.ORDER_INDEX)
            .addType("_doc")
            .build()
        
        val result: SearchResult = esClinet.execute(search)
        result.getAggregations.getSumAggregation("sum_totalAmount").getSum
    }
    
    /**
      * 返回指定日期每小时的订单额度
      *
      * @param date
      * @return
      */
    override def getOrderHourTotal(date: String): mutable.Map[String, Double] = {
        val dsl =
            s"""
               |{
               |  "query": {
               |    "bool": {
               |      "filter": {
               |        "term": {
               |          "createDate": "$date"
               |        }
               |      }
               |    }
               |  },
               |  "aggs": {
               |    "groupby_createHour": {
               |      "terms": {
               |        "field": "createHour",
               |        "size": 24
               |      },
               |      "aggs": {
               |        "sum_hour_total": {
               |          "sum": {
               |            "field": "totalAmount"
               |          }
               |        }
               |      }
               |    }
               |  }
               |}
             """.stripMargin
    
        val search: Search = new Search.Builder(dsl)
            .addIndex(GmallConstant.ORDER_INDEX)
            .addType("_doc")
            .build()
        val result: SearchResult = esClinet.execute(search)
        val buckets: util.List[TermsAggregation#Entry] =
            result.getAggregations.getTermsAggregation("groupby_createHour").getBuckets
        
        val map = mutable.Map[String, Double]()
        import scala.collection.JavaConversions._
        for(bucket <- buckets){
            map += bucket.getKey -> bucket.getSumAggregation("sum_hour_total").getSum
        }
        map
    }
}
