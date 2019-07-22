package com.atguigu.gmall0225logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall0225.common.util.GmallConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lzc
 * @Date 2019-07-22 11:37
 */
@RestController
public class LoggerController {
    // http://localhost:8080/log
    // log="{...}"
    @PostMapping("/log")
    public String doLog(@RequestParam("log") String log) {  //
        // 1. 给日志添加时间戳
        String logWithTS = addTS(log);
        // 2. 落盘(flume)
        saveLog(logWithTS);
        // 3. 写到kafka中
        sendToKafka(logWithTS);

        return "success";
    }

    @Autowired  // 会自动注入对象
    KafkaTemplate<String, String> template;
    /**
     * 把日志记录写入到kafka中
     * @param logWithTS
     */
    private void sendToKafka(String logWithTS) {
        JSONObject obj = JSON.parseObject(logWithTS);
        // 启动日志   事件日志
        String topic = GmallConstant.TOPIC_STARTUP();
        if(obj.getString("type").equals("event")){
            topic = GmallConstant.TOPIC_EVENT();
        }
        template.send(topic, logWithTS);
    }

    // 创建Loger对象
    private Logger logger = LoggerFactory.getLogger(LoggerController.class);
    /**
     * 落盘log
     * @param logWithTS
     */
    private void saveLog(String logWithTS) {
        logger.info(logWithTS);  // 直接写出字符串
    }


    /**
     * 给日志记录添加时间戳
     * @param log
     * @return
     */
    public String addTS(String log){
        JSONObject obj = JSON.parseObject(log);
        obj.put("ts", System.currentTimeMillis());

        return obj.toString();
    }
}
/*
200 ok
304 使用缓存
404 页面路径不对
500 服务器内部问题
 */
