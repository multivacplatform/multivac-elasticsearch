import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.functions.{date_format, struct}
import org.apache.spark.sql.types.TimestampType
import org.apache.spark.sql.{DataFrame, SparkSession}
import spark_helpers.SparkSessionHelper

import java.time._
import java.time.format._
import java.time.temporal.ChronoUnit
import java.util.Locale

object RetweetGraph {
  val appConf = ConfigFactory.load()

  def main(args: Array[String]) {
    val spark = SparkSessionHelper.buildSession()

    println(appConf.getString("spark.elastic.nodes.wanOnly.value"))
    println(appConf.getString("spark.elastic.nodes.url.value"))
    println(appConf.getString("spark.elastic.nodes.port.value"))
    println(appConf.getString("spark.elastic.proxy.authUser.value"))
    println(appConf.getString("spark.elastic.proxy.authPass.value"))

    val esOptions = Map(
      "es.index.auto.create" -> appConf.getString("spark.elastic.nodes.autoCreate.value"),
      "pushdown" -> appConf.getString("spark.elastic.index.pushdown.value"),
      "es.net.ssl" -> appConf.getString("spark.elastic.proxy.ssl.value"),
      "es.nodes.discovery" -> appConf.getString("spark.elastic.nodes.discovery.value"),
      "es.nodes.resolve.hostname" -> appConf.getString(
        "spark.elastic.nodes.resolveHostname.value"),
      "es.nodes.wan.only" -> appConf.getString("spark.elastic.nodes.wanOnly.value"),
      "es.nodes" -> appConf.getString("spark.elastic.nodes.url.value"),
      "es.port" -> appConf.getString("spark.elastic.nodes.port.value"),
      "es.net.http.auth.user" -> appConf.getString("spark.elastic.proxy.authUser.value"),
      "es.net.http.auth.pass" -> appConf.getString("spark.elastic.proxy.authPass.value"))

    val indexOptions = Map(
      "es.read.field.include" -> appConf.getString("spark.elastic.index.readFieldInclude.value"),
      "es.read.field.exclude" -> appConf.getString("spark.elastic.index.readFieldExclude.value"),
      "es.scroll.size" -> appConf.getString("spark.elastic.index.scrollSize.value"),
      "es.index.max_result_window" -> appConf.getString(
        "spark.elastic.index.max_result_window.value"),
      "es.input.json" -> appConf.getString("spark.elastic.index.inputJson.value"))

    val (until, since) = rangeBuilder(2, 1)
    println(s"since $since until $until")

    val q =
      s""" {"query": {"bool": {"must": [{"exists": {"field": "retweeted_status"}},{"range": {"ca": {"gte": "$since", "lte": "$until"}}}]}}}""".stripMargin

    println(q)
    val startTime = System.nanoTime()
    println(s"==========")
    println(s"Start")

    val df = retrieveData(spark, esOptions, indexOptions, q)
    saveAsJson(spark, df)

    val elapsed = (System.nanoTime() - startTime) / 1e9
    println(s"Finished")
    println(s"Time (sec)\t$elapsed")
    println(s"==========")

    spark.close()
  }

  private def retrieveData(
      spark: SparkSession,
      option1: Map[String, String],
      option2: Map[String, String],
      q: String): DataFrame = {

    import spark.implicits._

    val df1 = spark.read
      .format("es")
      .options(option1)
      .options(option2)
      .option("es.query", { q })
      .load(appConf.getString("spark.elastic.index.name.value"))

    df1
      .withColumn(
        "ca_tmp",
        date_format($"ca".cast(TimestampType), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
      .withColumn("retweeted_status_tmp", struct($"retweeted_status.usr").as("usr"))
      .drop("ca")
      .drop("retweeted_status")
      .withColumnRenamed("ca_tmp", "ca")
      .withColumnRenamed("retweeted_status_tmp", "retweeted_status")

  }

  private def saveAsJson(spark: SparkSession, df: DataFrame): Unit = {

    df.write.mode("Overwrite").parquet(appConf.getString("spark.conf.parquetOutputPath.value"))
    val loadedDF = spark.read.parquet(appConf.getString("spark.conf.parquetOutputPath.value"))
    loadedDF
      .coalesce(1)
      .write
      .mode("overwrite")
      .json(appConf.getString("spark.conf.jsonOutputPath.value"))
  }

  def rangeBuilder(numberOfDays: Int, nowMinusDays: Int): (String, String) = {
    val beginHour = "00:00:00.000'Z'"
    val endbeginHour = "23:59:59.999'Z'"

    val startFormatter = DateTimeFormatter
      .ofPattern(s"yyyy-MM-dd'T'$beginHour")
      .withLocale(Locale.FRANCE)
      .withZone(ZoneId.systemDefault())

    val endFormatter = DateTimeFormatter
      .ofPattern(s"yyyy-MM-dd'T'$endbeginHour")
      .withLocale(Locale.FRANCE)
      .withZone(ZoneId.systemDefault())

    val now = Instant.now()
    val runDayMinus1 = now.minus(nowMinusDays, ChronoUnit.DAYS)
    val runDayMinus17 = now.minus(numberOfDays, ChronoUnit.DAYS)

    (endFormatter.format(runDayMinus1), startFormatter.format(runDayMinus17))
  }
}
