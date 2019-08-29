import com.alibaba.fastjson.{JSON, JSONObject}

object Test {
    def main(args: Array[String]): Unit = {
        val obj = new JSONObject()
        obj.put("userName", "lisi")
        obj.put("user_id", 10)
        val user: User = JSON.parseObject(obj.toJSONString, classOf[User])
        println(user)
        
        
    }
    
    case class User(user_name: String, userId: Int)
    
}
