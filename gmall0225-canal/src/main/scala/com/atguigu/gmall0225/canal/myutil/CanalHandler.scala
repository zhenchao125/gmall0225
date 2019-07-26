package com.atguigu.gmall0225.canal.myutil

import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.google.common.base.CaseFormat
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.EventType
import com.atguigu.gmall0225.common.util.GmallConstant

/**
  * Author lzc
  * Date 2019-07-26 09:12
  */
object CanalHandler {
    def handle(tableName: String, rowDataList: util.List[CanalEntry.RowData], eventType: EventType) = {
        import scala.collection.JavaConversions._
        if(tableName == "order_info" && eventType == EventType.INSERT && !rowDataList.isEmpty){
            for(rowData <- rowDataList){  // 一个rowData表示一行数据
                // 取出每行的每列, 写成json格式的字符串存入kafka  {name: "李四", age: 20,....}
                val obj = new JSONObject()
                val columns: util.List[CanalEntry.Column] = rowData.getAfterColumnsList
                for(column <- columns){
                    val key: String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.getName)  // 列名
                    val value: String = column.getValue
                    obj.put(key, value)
                    
                }
                // 写入到kafka
                MyKafkaSenderUtil.send(GmallConstant.TOPIC_ORDER, obj.toJSONString)
            }
        }
    }
}
