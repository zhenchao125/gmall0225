package com.atguigu.gmall0225.canal.myutil

import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.google.common.base.CaseFormat
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.EventType

/**
  * Author lzc
  * Date 2019-07-26 09:12
  */
object CanalHandler2 {
    def handle(tableName: String, rowDataList: util.List[CanalEntry.RowData], eventType: EventType) = {
        
        if (tableName == "order_info" && eventType == EventType.INSERT && !rowDataList.isEmpty) {
            sendToKafka(rowDataList, "gmall_order_info")
        }else if(tableName == "order_detail" && eventType == EventType.INSERT && !rowDataList.isEmpty){
            sendToKafka(rowDataList, "gmall_order_detail")
        }else if(tableName == "user_info" && (eventType == EventType.INSERT || eventType == EventType.UPDATE) && !rowDataList.isEmpty){
            sendToKafka(rowDataList, "gmall_user_info")
        }
    }
    
    /**
      * 发送数据到kafka
      * @param rowDataList
      * @param topic
      */
    private def sendToKafka(rowDataList: util.List[CanalEntry.RowData], topic: String): Unit = {
        import scala.collection.JavaConversions._
        for (rowData <- rowDataList) { // 一个rowData表示一行数据
            // 取出每行的每列, 写成json格式的字符串存入kafka  {name: "李四", age: 20,....}
            val obj = new JSONObject()
            val columns: util.List[CanalEntry.Column] = rowData.getAfterColumnsList
            for (column <- columns) {
                val key: String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.getName) // 列名
                val value: String = column.getValue
                obj.put(key, value)
                
            }
            // 写入到kafka
            MyKafkaSenderUtil.send(topic, obj.toJSONString)
        }
    }
}
