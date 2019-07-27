package com.atguigu.gmall0225publisher.service

import scala.collection.mutable

trait PublisherService {
    /**
      * 返回指定日期的日活数据
      *
      * @param date
      * @return
      */
    def getDauTotal(date: String): Long
    
    /**
      * 返回具体的小时日活
      *
      * @param date
      * @return
      */
    def getDauHour(date: String): Map[String, Long]
    
    /**
      * 返回指定日期的订单的总额
      *
      * @param date
      * @return
      */
    def getOrderTotal(date: String): Double
    
    /**
      * 返回指定日期每小时的订单额度
      *
      * @param date
      * @return
      */
    def getOrderHourTotal(date: String): mutable.Map[String, Double]
    
    /*
    http://localhost:8070/sale_detail?date=2019-05-20&&startpage=1&&size=5&&keyword=手机小米
     */
    def getSaleDetalAndAggregateResultByField(date: String,
                                              keyWord: String,
                                              startPage: Int,
                                              size: Int,
                                              aggField: String,
                                              aggSize: Int): Map[String, Any]
    
}
