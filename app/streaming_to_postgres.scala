import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.spark.sql.DataFrame
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
        col("value").getItem(2).as("ruc"),
        col("value").getItem(3).cast("int").as("plazo_contrato"),
        col("value").getItem(4).cast("boolean").as("vigencia_hoy"),
        col("value").getItem(5).cast("int").as("ren_automatica"),
        col("value").getItem(6).as("fecha_firma"),
        col("value").getItem(7).as("inicio_suministro"),
        col("value").getItem(8).as("fin_suministro"),
        col("value").getItem(9).cast("int").as("potencia_hp"),
        col("value").getItem(10).cast("int").as("potencia_hfp"),
        col("value").getItem(11).cast("double").as("precio_potencia"),
        col("value").getItem(13).cast("double").as("precio_energia")
    )

val factor_actualizacion = 0.5 * (252.526 / 248.02) + 0.5 * (3.5535 * 3.8474 / 3.8524) // 2.2835
val updatedDF = parsedDF.withColumn("costo_potencia", col("precio_potencia") * factor_actualizacion)
    .withColumn("costo_energia", col("precio_energia") * factor_actualizacion)

val url = "jdbc:postgresql://host.docker.internal:5432/postgres"
val table = "atria_contratos"
val properties = new java.util.Properties()
properties.setProperty("driver", "org.postgresql.Driver")
properties.setProperty("user", "postgres")
properties.setProperty("password", "postgres")

val query = updatedDF.writeStream
    .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        batchDF.write
        .mode("append")
        .jdbc(url, table, properties)
    }
    .outputMode("update") // Solo las nuevas filas se agregan al resultado
    .trigger(Trigger.ProcessingTime("2 seconds")) // Configura el intervalo del batch a 10 segundos
    .start()

query.awaitTermination()

spark.stop()