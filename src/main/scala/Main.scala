import org.apache.spark.{SparkConf, SparkContext}
import spark_helpers.SparkSessionHelper
//import org.elasticsearch.spark._
import org.elasticsearch.spark.sql._

object Main {
  def main(args: Array[String]) {
    val spark = SparkSessionHelper.buildSession()
    // write to elasticsearch local (docker)
    val df2 = spark.read.format("json").load("data/people.json")
    df2.show()
    df2.saveToEs("people/person")

    // read from elasticsearch local
    val df = spark.read
      .format("es")
      .load("people/person")
    df.show()
    spark.close()
  }
}