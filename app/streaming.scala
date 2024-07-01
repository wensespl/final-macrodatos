import org.apache.spark.sql.functions._

val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "kafka:9093")
    .option("subscribe", "streaming_topic")
    .option("startingOffsets", "earliest")
    .load()

val df2 = df.selectExpr("CAST(value AS STRING)").as[String]

val query = df2.writeStream
    .outputMode("append")
    .format("console")
    .start()

query.awaitTermination()

// df.writeStream
//     .outputMode("append")
//     .format("console")
//     .option("truncate", "false")
//     .start()
//     .awaitTermination()