package com.atguigu.gmall0225publisher.controller

import java.time.LocalDate

import com.atguigu.gmall0225publisher.service.PublisherService
import org.json4s.jackson.JsonMethods
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, RequestParam, RestController}

@RestController
class PublisherController {
    @Autowired
    var service: PublisherService = _
    
    @GetMapping(Array("/realtime-total"))
    def getRealtimeDAUTotal(@RequestParam("date") date: String): String = {
        val dauTotal: Long = service.getDauTotal(date)
        val result =
            s"""
               |[
               |  {"id":"dau","name":"新增日活","value":$dauTotal},
               |  {"id":"new_mid","name":"新增用户","value":233}
               |]
             """.stripMargin
        result
    }
    
    @GetMapping(Array("/realtime-hour"))
    def getRealtimeHour(@RequestParam("id") id: String, @RequestParam("date") date: String) ={
        if(id == "dau"){
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
        }else {
        
        }
    }
    
    /**
      * 根据今天计算昨天
      * @param date
      */
    def calcYesterday(date: String): String ={
        LocalDate.parse(date).minusDays(1).toString
    }
    
}
