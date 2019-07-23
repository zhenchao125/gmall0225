import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Test {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("Practice").setMaster("local[2]")
        val sc = new SparkContext(conf)
        val rdd: RDD[User] = sc.parallelize(Seq(User(10, "lisi"), User(10, "lisi")))
        rdd.distinct().collect.foreach(println)
        sc.stop()
        
        
    }
}

case class User(age: Int, name: String){
    override def hashCode(): Int = {
        println("hash...")
        age
    }
    
    override def equals(obj: scala.Any): Boolean = {
        println("equals....")
        val o: User = obj.asInstanceOf[User]
        this.age == o.age
    }
}