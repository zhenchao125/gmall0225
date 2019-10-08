package com.atguigu.gmall0225.canal

import java.net.InetSocketAddress
import java.util

import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.atguigu.gmall0225.canal.myutil.{CanalHandler, CanalHandler2}
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
        
//        connector.subscribe("gmall.order_info") // 订阅 gmall数据库下所有的表
        connector.subscribe("gmall.*") // 订阅 gmall数据库下所有的表
        while (true) {  // 连接上之后需要持续不断的从Canal取数据
            val msg: Message = connector.get(100)  // 获取数据
            val entries: util.List[CanalEntry.Entry] = msg.getEntries
            import scala.collection.JavaConversions._
            if (entries.size() > 0) {
                for (entry <- entries) {
                    if (entry.getEntryType == EntryType.ROWDATA) {
                        // 获取具体数据
                        val value: ByteString = entry.getStoreValue
                        val rowChange: RowChange = RowChange.parseFrom(value)
                        val rowDataList: util.List[CanalEntry.RowData] = rowChange.getRowDatasList
                        // 具体处理  参数1: 读取的数据的表名  参数2: 具体的数据  参数3: 这次读到数据是因为什么事件引起
                        CanalHandler2.handle(entry.getHeader.getTableName, rowDataList, rowChange.getEventType)
                    }
                }
            } else {
                Thread.sleep(2000)
            }
        }
        
    }
}

/*
Message  一个获取一个Message, 包含多条sql语句执行的结果
Entry    一个 Message 中包含多个 Entry, 一个 Entry 表示 1 条sql语句执行的结果
StoreValue  一个Entry包含一个storeValue (存储的序列化的数据)
RowChange  从 StoreValue中可以解析出来的数据对象
RowData    一个RowChange中包含多个RowData, 一个RowData可以表示一行数据
Column    列, 一个RowData中包含多列数据
 */
