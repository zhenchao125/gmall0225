package com.atguigu.gmall0225.canal

import java.net.InetSocketAddress
import java.util

import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.atguigu.gmall0225.canal.myutil.CanalHandler
import com.google.protobuf.ByteString

/**
  * Author lzc
  * Date 2019-07-26 08:57
  */
object MyCanalClient {
    def main(args: Array[String]): Unit = {
        val address = new InetSocketAddress("hadoop201", 11111)
        // 1. 从canal读数据
        val connector: CanalConnector = CanalConnectors.newSingleConnector(address, "example", "", "")
        connector.connect()
        
        connector.subscribe("gmall.order_info")  // 订阅具体的
        while (true) {
            val msg: Message = connector.get(100)
            val entries: util.List[CanalEntry.Entry] = msg.getEntries
            import scala.collection.JavaConversions._
            if(entries.size() > 0){
                for(entry <- entries){
                    if (entry.getEntryType == EntryType.ROWDATA) {
                        // 获取具体数据
                        val value: ByteString = entry.getStoreValue
                        val rowChange: RowChange = RowChange.parseFrom(value)
                        val rowDataList: util.List[CanalEntry.RowData] = rowChange.getRowDatasList
                        // 具体处理  参数1: 读取的数据的表名  参数2: 具体的数据  参数3: 这次读到数据是因为什么事件引起
                        CanalHandler.handle(entry.getHeader.getTableName, rowDataList, rowChange.getEventType)
                    }
                }
                
            }
        }
        
        // 2. 上kafka写数据
    }
}
