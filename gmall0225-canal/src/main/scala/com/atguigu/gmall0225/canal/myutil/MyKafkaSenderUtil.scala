package com.atguigu.gmall0225.canal.myutil

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object MyKafkaSenderUtil {
    val props = new Properties()
    props.put("bootstrap.servers", "hadoop201:9092,hadoop202:9092,hadoop203:9092")
    // key序列化
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // value序列化
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    
    val producer = new KafkaProducer[String, String](props)
    
    def send(topic: String, content: String) = {
        producer.send(new ProducerRecord[String, String](topic, content))
    }
}
