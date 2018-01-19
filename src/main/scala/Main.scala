import org.apache.spark.{SparkConf, SparkContext}
import spark_helpers.SparkSessionHelper
//import org.elasticsearch.spark._
import org.elasticsearch.spark.sql._

object Main {
  def main(args: Array[String]) {
    val spark = SparkSessionHelper.buildSession()
    // write to elasticsearch local (docker)
    //    val df2 = spark.read.format("json").load("data/people.json")
    //    df2.show()
    //    df2.saveToEs("people/person")

    val options =Map(
      "es.read.field.include" -> "",
      "es.index.auto.create" -> "true",
      "pushdown" -> "true",
      "es.nodes"-> "",
      "es.port" -> "443",
      "es.net.http.auth.user" -> "",
      "es.net.http.auth.pass" -> "",
      "es.net.ssl" -> "true",
      "es.nodes.discovery" -> "false",
      "es.nodes.resolve.hostname" -> "false",
      "es.nodes.wan.only" -> "true"
    )

    // read from elasticsearch local
    val df = spark.read
      .format("es")
      .options(options)
      .load("")

    df.show()
    spark.close()
  }
}