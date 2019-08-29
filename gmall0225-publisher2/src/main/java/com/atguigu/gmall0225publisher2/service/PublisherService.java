package com.atguigu.gmall0225publisher2.service;

import java.util.List;
import java.util.Map;

public interface PublisherService {
    /*
    查询总数
     */
    long getDauTotal(String date);
    /*
    查询小时明细

    相比数据层, 我们把数据结构做下调整, 更方便使用
     */
    Map getDauHour(String date);

    /**
     * 销售额总数
     * @param date
     * @return
     */
    double getOrderAmountTotal(String date);

    /**
     * 获取销售额小时明细
     * @param date
     * @return
     */
    Map getOrderAmountHour(String date);
}
