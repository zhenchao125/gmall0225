package com.atguigu.gmall0225.common.util

import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, Index}

/**
  * Author lzc
  * Date 2019-07-24 09:50
  */
object MyESUtil {
    val esUrl = "http://hadoop201:9200"
    val factory = new JestClientFactory
    val conf: HttpClientConfig = new HttpClientConfig.Builder(esUrl)
        .multiThreaded(true)
        .maxTotalConnection(20)
        .connTimeout(10000)
        .readTimeout(10000)
        .build()
    factory.setHttpClientConfig(conf)
    
    // 获取客户端
    def getESClient = factory.getObject
    
    // 插入单条数据
    def insertSingle(indexName: String, source: Any) = {
        val client: JestClient = getESClient
        val index: Index = new Index.Builder(source)
            .`type`("_doc")
            .index(indexName)
            .build()
        client.execute(index)
        client.close()
    }
    
    // 插入多条数据
    def insertBulk(indexName: String, sources: Iterable[Any]):Unit ={
        if (sources.isEmpty) return
        
        val client: JestClient = getESClient
        val bulkBuilder = new Bulk.Builder()
            .defaultIndex(indexName)
            .defaultType("_doc")
        sources.foreach(source => {  // 把所有的source变成action添加buck中
            bulkBuilder.addAction(new Index.Builder(source).build())
        })
        
        client.execute(bulkBuilder.build())
        closeClient(client)
        
    }
    
    def main(args: Array[String]): Unit = {
//        insertSingle("user", User("a", 20))
        insertBulk("user", Iterable(User("aa", 20), User("bb", 30)))
        
    }
    
    /**
      * 关闭客户端
      *
      * @param client
      */
    def closeClient(client: JestClient) = {
        if (client != null) {
            try {
                client.shutdownClient()
            } catch {
                case e => e.printStackTrace()
            }
        }
    }
    
}

case class User(name: String, age: Int)