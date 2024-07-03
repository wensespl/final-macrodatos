from confluent_kafka import Producer
import time
import csv

# Kafka broker configuration
bootstrap_servers = 'localhost:9092'
topic = 'streaming_topic'

# Create a Kafka producer
producer = Producer({'bootstrap.servers': bootstrap_servers})

# Delivery report callback
def delivery_report(err, msg):
    if err is not None:
        print(f'Failed to deliver message: {err}')
    else:
        print(f'Message delivered to {msg.topic()} [{msg.partition()}]')

# Read data from CSV and send to Kafka
with open('datos_atria_final.csv', 'r', encoding='utf-8') as csvfile:
    csvreader = csv.reader(csvfile)
    next(csvreader) # Ignoar la primera fila
    for row in csvreader:
        message = ','.join(row)
        producer.produce(topic, message.encode('utf-8'), callback=delivery_report)
        time.sleep(1)

# Wait for all messages to be delivered
producer.flush()
