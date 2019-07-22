package com.atguigu.gmall0225.mock.util

import java.io.OutputStream
import java.net.{HttpURLConnection, URL}

/**
  * Author lzc
  * Date 2019/5/14 11:25 AM
  */
object LogUploader {
    /*发送日志*/
    def sendLog(log: String): Unit = {
        try {
            // 1. 日志服务器的地址
            val logUrl = new URL("http://hadoop201:8080/log")
            // 2. 得到一个 HttpURLConnection
            val conn: HttpURLConnection = logUrl.openConnection().asInstanceOf[HttpURLConnection]
            // 3. 设置请求方法(上传数据一般使用 post 请求)
            conn.setRequestMethod("POST")
            // 4. 用来供server进行时钟校对的
            conn.setRequestProperty("clientTime", System.currentTimeMillis + "")
            // 5. 允许上传数据  如果是post必须设置为true
            conn.setDoOutput(true)
            // 6. 设置请求的头信息, post 请求必须这样设置
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            // 7. 获取上传用的输出流
            val out: OutputStream = conn.getOutputStream
            // 8. 写出数据
            out.write(("log=" + log).getBytes())  // log={...}
            // 9. flush
            out.flush()
            // 10. 关闭资源
            out.close()
            // 11. 获取响应码.  (或者获取响应信息也行, 否则不会发送请求到服务器)
            val code: Int = conn.getResponseCode
            println(code)
        } catch {
            case e: Exception => e.printStackTrace()
        }
    }
}
/*


 */