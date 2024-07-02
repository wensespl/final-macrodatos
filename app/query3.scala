import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger

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
        col("value").getItem(9).cast("double").as("potencia_hp"),
    )

// Calcular el mínimo, máximo y promedio
val aggDF = parsedDF
    .groupBy()
    .agg(
        min("potencia_hp").as("min_potencia_hp"),
        max("potencia_hp").as("max_potencia_hp"),
        avg("potencia_hp").as("avg_potencia_hp")
    )

val query = aggDF.writeStream
    .outputMode("complete") // Solo las nuevas filas se agregan al resultado
    .format("console")
    .option("truncate", "false")
    .trigger(Trigger.ProcessingTime("10 seconds")) // Configura el intervalo del batch a 10 segundos
    .start()

query.awaitTermination()

// ./spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 -i ./app/query3.scala