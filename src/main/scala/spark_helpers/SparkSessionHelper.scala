package spark_helpers

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object SparkSessionHelper {
  def buildSession(): SparkSession = {

    val sparkMaster = ConfigFactory.load().getString("spark.conf.master.value")

    println("Spark Master: ", sparkMaster)

    val spark: SparkSession = SparkSession.builder
      .appName("multivac-elastic")
      .master(sparkMaster)
      .enableHiveSupport()
      .config("es.index.auto.create", "true")
      .config("pushdown", "true")
      .config("es.nodes", "127.0.0.1")
      .config("es.port", "9200")
      .config("es.nodes.discovery", "false")
      .config("es.nodes.data.only", "false")
      .getOrCreate

//    spark.conf.set("es.index.auto.create", "true")
//    spark.conf.set("pushdown", "true")
//    spark.conf.set("es.nodes", "192.168.1.137")
//    spark.conf.set("es.nodes.discovery", "false")
//    spark.conf.set("es.port", "9200")

    spark.sparkContext.setLogLevel("WARN")

    spark
  }

  def getSparkSession(): SparkSession ={
    SparkSession.builder().getOrCreate()
  }
}