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
        col("value").getItem(11).cast("double").as("precio_potencia"),
        col("value").getItem(13).cast("double").as("precio_energia")
    )

val factor_actualizacion = 0.5 * (252.526 / 248.02) + 0.5 * (3.5535 * 3.8474 / 3.8524) // 2.2835
val updatedDF = parsedDF.withColumn("costo_potencia", col("precio_potencia") * factor_actualizacion)
    .withColumn("costo_energia", col("precio_energia") * factor_actualizacion)


val query = updatedDF.writeStream
    .outputMode("append") // Solo las nuevas filas se agregan al resultado
    .format("console")
    .option("truncate", "false")
    .trigger(Trigger.ProcessingTime("10 seconds")) // Configura el intervalo del batch a 10 segundos
    .start()

query.awaitTermination()

// precio_potencia = precio_base_potencia * factor_actualizacion
// precio_energia = precio_base_energia * factor_actualizacion

// factor_actualizacion = 0.5 * (ppij / ppio) + 0.5 * (pgnj / pgno)
// ppij = 252.526 //https://fred.stlouisfed.org/series/WPSFD4131
// ppio = 248.02
// pgnj = 3.5535 * TC // https://www.osinergmin.gob.pe/seccion/centro_documental/gart/PreciosReferencia/PrecioGasNatural30122022.pdf
// TC = 	3.8474 //https://www.sbs.gob.pe/app/pp/sistip_portal/paginas/publicacion/tipocambiopromedio.aspx
// pgno = 3.8524

// factor_actualizacion = 0.5 * (252.526 / 248.02) + 0.5 * (3.5535 * 3.8474 / 3.8524)


// ./spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 -i ./app/query1.scala