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
cd $SPARK_HOME/bin
```

start a spark-shell session

```bash
spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 
```

run file in spark-shell

```bash
spark-shell --conf spark.executor.memory=2G --conf spark.executor.cores=1 --master spark://spark-master:7077 -i spark_apps/file.scala
```

## Rutas utiles

- spark-master ui <http://localhost:8080>
- spark-worker-1 ui <http://localhost:8081>
- spark-worker-2 ui <http://localhost:8082>
- spark-client ui <http://localhost:4040>
