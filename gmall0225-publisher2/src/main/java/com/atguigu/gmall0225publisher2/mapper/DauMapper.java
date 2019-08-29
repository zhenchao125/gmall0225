package com.atguigu.gmall0225publisher2.mapper;

import java.util.List;
import java.util.Map;

/*
从数据库查询数据的接口
 */
public interface DauMapper {
    // 查询日活总数
    long getDauTotal(String date);
    // 查询小时明细
    List<Map> getDauHour(String date);
}
