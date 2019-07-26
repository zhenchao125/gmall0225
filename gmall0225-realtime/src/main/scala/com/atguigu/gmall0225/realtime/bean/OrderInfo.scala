package com.atguigu.gmall0225.realtime.bean

import java.text.SimpleDateFormat

case class OrderInfo(area: String,
                     var consignee: String, // 收件人
                     orderComment: String,
                     var consigneeTel: String,
                     operateTime: String,
                     orderStatus: String,
                     paymentWay: String,
                     userId: String,
                     imgUrl: String,
                     totalAmount: Double,
                     expireTime: String,
                     deliveryAddress: String,
                     createTime: String,
                     trackingNo: String,
                     parentOrderId: String,
                     outTradeNo: String,
                     id: String,
                     tradeBody: String) {
    
    private val date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createTime)
    val createDate: String = new SimpleDateFormat("yyyy-MM-dd").format(date)
    val createHour: String = new SimpleDateFormat("HH").format(date)
    val createHourMinute: String = new SimpleDateFormat("HH:mm").format(date)
}

