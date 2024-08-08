<img align="right" src="https://visitor-badge.laobi.icu/badge?page_id=wensespl.final-macrodatos&format=true" />
# final-macrodatos

Proyecto final del curso de Analisis de Macrodatos. Se realiza la extraccion y analisis de documentos de contratos libres de Osinergmin con la ayuda de Kafka, Sprak streaming con Scala y Superset. Los archivos necesarios para realizar las consultas se encuentran dentro de la carpeta app asi como para realizar el envio de los datos con el archivo kafka_producer.py

## Start spark cluster

Start cluster containers

```bash
docker-compose up -d
```

Configure superset

> [!IMPORTANT]
> Setup your local admin account `docker exec -it superset superset fab create-admin --username admin --firstname Superset --lastname Admin --email admin@superset.com --password admin` .
> Migrate local DB to latest `docker exec -it superset superset db upgrade` .
> Setup roles `docker exec -it superset superset init`

## Run jobs

Get into spark-client container shell

```bash
sudo docker exec -it spark-client bash
```

Go to spark bin folder

```bash
cd ./spark/bin
```

Start a spark-shell session

```bash
spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1
```

Run file in spark-shell

```bash
./spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 -i ./app/streaming.scala
```

Run file in spark-shell with postgres driver

```bash
./spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 --driver-class-path ./app/jars/postgresql-42.7.3.jar --jars ./app/jars/postgresql-42.7.3.jar -i ./app/streaming_to_postgres.scala
```

Run producer

```bash
python .\kafka_producer.py
```

> [!IMPORTANT]
> Create python virtual environment and install `confluent_kafka` with command `pip install confluent_kafka`

Import the dashboard in Superset

We go to <http://localhost:8088/login> and enter with the credentials> `user: admin` and `password: admin`.
In the `Dashboards` tab, click on the down arrow, and in the new window select the compressed file.
To view it, it is necessary to change the IP address of the HOST (Postgres container) in Settings.

## Optional streaming with Python

Run file in spark-submit

```bash
./spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.1 ./app/streaming.py
```

> [!IMPORTANT]
> Install pyspark in `spark-client` container with the command `pip install pyspark`

## Shared folder to upload files

./app

## Useful routes

- spark-master ui <http://localhost:8080>
- spark-worker-1 ui <http://localhost:8081>
- spark-worker-2 ui <http://localhost:8082>
- spark-client ui <http://localhost:4040>
- superset ui <http://localhost:8088/login>

## Useful commands

- start containers `docker compose up -d`
- Stop containers `docker compose stop`
- Delete containers `docker compose down`
