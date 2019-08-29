package com.atguigu.gmall0225publisher2.mapper;

import java.util.List;
import java.util.Map;

public interface OrderMapper {

    /**
     * 获取订单总的销售额
     *
     * @param date
     * @return
     */
    double getOrderAmountTotal(String date);

    /**
     * 获取每小时的销售额明细
     *
     * @param date
     * @return
     */
    List<Map> getOrderAmountHour(String date);

}
