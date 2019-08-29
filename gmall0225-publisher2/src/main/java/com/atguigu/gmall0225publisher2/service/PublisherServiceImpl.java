package com.atguigu.gmall0225publisher2.service;

import com.atguigu.gmall0225publisher2.mapper.DauMapper;
import com.atguigu.gmall0225publisher2.mapper.OrderMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lzc
 * @Date 2019/8/27 4:03 PM
 */
@Service  // 必须添加 Service 注解
public class PublisherServiceImpl implements PublisherService {
    /*自动注入 DauMapper 对象*/
    @Autowired
    DauMapper dauMapper;


    @Override
    public long getDauTotal(String date) {
        return dauMapper.getDauTotal(date);
    }

    @Override
    public Map getDauHour(String date) {
        List<Map> dauHourList = dauMapper.getDauHour(date);

        Map dauHourMap = new HashedMap();
        for (Map map : dauHourList) {
            String hour = (String)map.get("LOGHOUR");
            Long count = (Long) map.get("COUNT");
            dauHourMap.put(hour, count);
        }

        return dauHourMap;
    }

    @Autowired
    OrderMapper orderMapper;
    @Override
    public double getOrderAmountTotal(String date) {
        return orderMapper.getOrderAmountTotal(date);
    }

    @Override
    public Map getOrderAmountHour(String date) {
        List<Map> orderAmountHour = orderMapper.getOrderAmountHour(date);

        Map<String, BigDecimal> orderHourAmountMap = new HashMap<>();
        for (Map map : orderAmountHour) {
            String hour = (String) map.get("CREATE_HOUR");
            BigDecimal amount = (BigDecimal)map.get("SUM");
            orderHourAmountMap.put(hour, amount);
        }

        return orderHourAmountMap;
    }
}
