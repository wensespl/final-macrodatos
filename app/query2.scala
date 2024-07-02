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

// Convertir los datos de bytes a String
val df2 = df.selectExpr("CAST(value AS STRING) as value")

// Parsear las líneas del CSV y seleccionar campos específicos
val parsedDF = df2.withColumn("value", split(col("value"), ","))
    .select(
        col("value").getItem(1).as("nombre_comercial"),
        col("value").getItem(3).cast("int").as("plazo_contrato"),
    )

// Calcular el mínimo, máximo y promedio
val aggDF = parsedDF
    .groupBy()
    .agg(
        min("plazo_contrato").as("min_plazo_contrato"),
        max("plazo_contrato").as("max_plazo_contrato"),
        avg("plazo_contrato").as("avg_plazo_contrato")
    )

val query = aggDF.writeStream
    .outputMode("complete") // Solo las nuevas filas se agregan al resultado
    .format("console")
    .option("truncate", "false")
    .trigger(Trigger.ProcessingTime("10 seconds")) // Configura el intervalo del batch a 10 segundos
    .start()

query.awaitTermination()