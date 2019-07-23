package com.atguigu.gmall0225.realtime.util

import java.io.InputStream
import java.util.Properties

/**
  * Author lzc
  * Date 2019/5/15 11:31 AM
  */
object PropertiesUtil {
    private val is: InputStream = ClassLoader.getSystemResourceAsStream("config.properties")
    private val properties = new Properties()
    properties.load(is)
    // 传入属性名, 返回属性值
    def getProperty(propertyName: String): String = properties.getProperty(propertyName)
    
    def main(args: Array[String]): Unit = {
        println(getProperty("kafka.broker.list"))
    }
}

