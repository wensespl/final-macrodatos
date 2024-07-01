# final-macrodatos

## Iniciar spark cluster

start cluster containers

```bash
docker-compose up -d
```

get into spark-client container shell

```bash
sudo docker exec -it spark-client bash
```

go to spark bin folder

```bash
cd ./spark/bin
```

start a spark-shell session

```bash
spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1
```

run file in spark-shell

```bash
./spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 -i ./app/streaming.scala
```

run file in spark-submit

```bash
./spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 -i ./app/streaming.py
```

> [!IMPORTANT]
> instalar pyspark en contenedor con el comando `pip install pyspark`

run producer

```bash
python .\kafka_producer.py
```

> [!IMPORTANT]
> crear entorno virtual e instalar confluent_kafka con el comando `pip install confluent_kafka`

## Carpeta compartida para subir los archivos

./app

## Rutas utiles

- spark-master ui <http://localhost:8080>
- spark-worker-1 ui <http://localhost:8081>
- spark-worker-2 ui <http://localhost:8082>
- spark-client ui <http://localhost:4040>
