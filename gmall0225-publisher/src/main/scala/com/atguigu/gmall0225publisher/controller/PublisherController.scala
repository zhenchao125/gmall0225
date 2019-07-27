package com.atguigu.gmall0225publisher.controller

import java.text.DecimalFormat
import java.time.LocalDate

import com.atguigu.gmall0225publisher.bean.{Opt, SaleInfo, Stat}
import com.atguigu.gmall0225publisher.service.PublisherService
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, RequestParam, RestController}

import scala.collection.mutable

@RestController
class PublisherController {
    @Autowired
    var service: PublisherService = _
    
    @GetMapping(Array("/realtime-total"))
    def getRealtimeDAUTotal(@RequestParam("date") date: String): String = {
        val dauTotal: Long = service.getDauTotal(date)
        val orderTotal = service.getOrderTotal(date)
        val result =
            s"""
               |[
               |  {"id":"dau","name":"新增日活","value":$dauTotal},
               |  {"id":"new_mid","name":"新增用户","value":233},
               |  {"id":"order_amount","name":"新增交易额度","value":$orderTotal},
               |]
             """.stripMargin
        result
    }
    
    @GetMapping(Array("/realtime-hour"))
    def getRealtimeHour(@RequestParam("id") id: String, @RequestParam("date") date: String) = {
        if (id == "dau") {
            val todayMap: Map[String, Long] = service.getDauHour(date)
            val yesterdayMap: Map[String, Long] = service.getDauHour(calcYesterday(date))
            
            /*
            {
               "yesterday":{"11":383,"12":123,"17":88,"19":200 },
               "today":{"12":38,"13":1233,"17":123,"19":688 }
            }

             */
            
            var resultMap = Map[String, Map[String, Long]]()
            resultMap += "today" -> todayMap
            resultMap += "yesterday" -> yesterdayMap
            
            import org.json4s.JsonDSL._
            JsonMethods.compact(JsonMethods.render(resultMap))
        } else if (id == "order_amount") {
            // 订单的明细
            val todayHourOrder: mutable.Map[String, Double] = service.getOrderHourTotal(date)
            val yesterdayHourOrder: mutable.Map[String, Double] = service.getOrderHourTotal(calcYesterday(date))
            
            var resultMap = Map[String, mutable.Map[String, Double]]()
            resultMap += "today" -> todayHourOrder
            resultMap += "yesterday" -> yesterdayHourOrder
            
            import org.json4s.JsonDSL._
            JsonMethods.compact(JsonMethods.render(resultMap))
            
        } else {
            null
        }
    }
    
    // http://localhost:8070/sale_detail?date=2019-05-20&&startpage=1&&size=5&&keyword=手机小米
    @GetMapping(Array("/sale_detail"))
    def daleDetail(@RequestParam("date") date: String,
                   @RequestParam("startpage") startPage: Int,
                   @RequestParam("size") size: Int,
                   @RequestParam("keyword") keyword: String) = {
        val formatter = new DecimalFormat(".00")
        // 1. 先安装性别查
        val genderMap: Map[String, Any] = service.getSaleDetalAndAggregateResultByField(date, keyword, startPage, size, "user_gender", 2)
        val total: Int = genderMap("total").asInstanceOf[Int]
        val aggMap: Map[String, Long] = genderMap.getOrElse("aggMap", Map[String, Long]()).asInstanceOf[Map[String, Long]]
        val maleCount = aggMap.getOrElse("M", 0L)
        val femaleCount = aggMap.getOrElse("F", 0L)
        
        val firstOptions = List[Opt](
            Opt("男", formatter.format(maleCount.toDouble / total)),
            Opt("女", formatter.format(femaleCount.toDouble / total))
        )
        val firtStat = Stat("男女比例数据分析", firstOptions)// (第一个饼图)
        // 2. 在按照年龄查
        val ageMap: Map[String, Any] =
            service.getSaleDetalAndAggregateResultByField(date, keyword, startPage, size, "user_age", 100)
        // 2.1 获取存储年龄分布的那个map
        val ageCountMap = ageMap.getOrElse("aggMap", Map[String, Long]()).asInstanceOf[Map[String, Long]]
        val secondOptions = ageCountMap.groupBy {
            case (age, count) => {
                if (age.toInt < 20) "20以下"
                else if (age.toInt >= 20 && age.toInt <= 30) "20到30"
                else "30以上"
            }
        }.map{
            case (ageRange, map) => (ageRange, map.toList.foldLeft(0L)(_ + _._2))
        }.map{
            case (ageRange, count) => Opt(ageRange, formatter.format(count.toDouble / total))
        }.toList
        
        val secondStat = Stat("年龄分布", secondOptions) // (第二个饼图)
        
        
        // 3. 详情 从前面哪个查询结果中获取都可以
        val detailList = genderMap.getOrElse("detail", Nil).asInstanceOf[List[Map[String, Any]]]
        
        // 4. 组合成具体的数据,返回给前段
        val info = SaleInfo(total, firtStat:: secondStat::Nil, detailList)
        
        // 5.info 转变成 json字符串就ok了
        import org.json4s.jackson.Serialization.write
        implicit val formats = DefaultFormats
        write(info)
    }
    
    /**
      * 根据今天计算昨天
      *
      * @param date
      */
    def calcYesterday(date: String): String = {
        LocalDate.parse(date).minusDays(1).toString
    }
    
}
