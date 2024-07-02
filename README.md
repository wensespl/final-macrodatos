# final-macrodatos

## Start spark cluster

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

run file in spark-shell with postgres driver

```bash
./spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --driver-class-path ./app/jars/postgresql-42.7.3.jar --jars ./app/jars/postgresql-42.7.3.jar -i ./app/streaming.scala
```

run file in spark-submit

```bash
./spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 ./app/streaming.py
```

> [!IMPORTANT]
> Install pyspark in `spark-client` container with the command `pip install pyspark`

run producer

```bash
python .\kafka_producer.py
```

> [!IMPORTANT]
> Create python virtual environment and install `confluent_kafka` with command `pip install confluent_kafka`

## Shared folder to upload files

./app

## Useful routes

- spark-master ui <http://localhost:8080>
- spark-worker-1 ui <http://localhost:8081>
- spark-worker-2 ui <http://localhost:8082>
- spark-client ui <http://localhost:4040>
