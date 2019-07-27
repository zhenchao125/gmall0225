package com.atguigu.gmall0225publisher.bean

/**
  * 封装饼图中的数据
  * 代表一张饼图
  */
case class Stat(title: String, options: List[Opt])

/**
  * 表示结果中的一个选项
  * 男  0.1 ...
  * 女  0.9
  * -------
  * *
  * 20-20岁  0.2
  */
case class Opt(name: String, value: String)

/**
  * 封装返回给前端的所有数据
  */
case class SaleInfo(total: Int, stats: List[Stat], detail: List[Map[String, Any]])
