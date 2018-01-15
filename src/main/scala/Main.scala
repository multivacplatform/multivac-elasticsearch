import org.apache.spark.{SparkConf, SparkContext}
import spark_helpers.SparkSessionHelper
//import org.elasticsearch.spark._
import org.elasticsearch.spark.sql._

object Main {
  def main(args: Array[String]) {
    val spark = SparkSessionHelper.buildSession()

    val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
    val airports = Map("OTP" -> "Otopeni", "SFO" -> "San Fran")

    //    sc.makeRDD(Seq(numbers, airports)).saveToEs("spark/docs")
    //    val df = spark.read.format("json").load("data/people.json")
    //    df.show()
    //    df.saveToEs("spark/people")
    val df = spark.read
      .format("es")
      .load("twitter/tweet")

    df.show()
    spark.close()
  }
}