import com.alibaba.google.common.base.CaseFormat

/**
  * Author lzc
  * Date 2019-07-26 09:28
  */
object Test1 {
    def main(args: Array[String]): Unit = {
        val name = "order_info_info"  // aB
    
        println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name))
        
        
    }
}
