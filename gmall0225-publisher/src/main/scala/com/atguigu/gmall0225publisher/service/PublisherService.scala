package com.atguigu.gmall0225publisher.service

trait PublisherService {
    /**
      * 返回指定日期的日活数据
      * @param date
      * @return
      */
    def getDauTotal(date: String): Long
    
    /**
      * 返回具体的小时日活
      * @param date
      * @return
      */
    def getDauHour(date: String): Map[String, Long]
    
}
