import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import spark.implicits._

sc.setLogLevel("ERROR")  // Set the log level to ERROR to avoid warnings

val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "kafka:9093")
    .option("subscribe", "streaming_topic")
    .option("startingOffsets", "latest")
    .load()

// val df2 = df.as[String]
//     .flatMap(_.split(","))
//     .groupBy("value")
//     .count()

// val df2 = df.withWatermark("timestamp", "2 seconds")
//     .dropDuplicates("key", "timestamp")

val df3 = df.selectExpr("CAST(value AS STRING) as value")
    .withColumn("value", split(col("value"), ","))
    .select(
        // col("value").getItem(0).as("nombre_pdf"),
        col("value").getItem(1).as("nombre_comercial"),
        col("value").getItem(3).as("plazo_contrato").as[Int],
    )
    // .agg(
    //     avg("plazo_contrato").as("promedio_plazo_contrato"),
    // )

// val query = df3.writeStream
//     // .outputMode("update")
//     .format("csv")
//     .option("checkpointLocation", "/app/tmp")
//     .option("path", "/app/tmp")
//     .trigger(Trigger.ProcessingTime("2 seconds"))
//     .start()

val query = df3.writeStream
    .outputMode("complete")
    .format("console")
    .option("truncate", "false")
    .trigger(Trigger.ProcessingTime("2 seconds"))
    .start()

query.awaitTermination()

// df.writeStream
//     .outputMode("append")
//     .format("console")
//     .option("truncate", "false")
//     .start()
//     .awaitTermination()