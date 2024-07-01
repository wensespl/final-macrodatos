from confluent_kafka import Producer

# Kafka broker configuration
bootstrap_servers = 'localhost:9092'

# Create a Kafka producer
producer = Producer({'bootstrap.servers': bootstrap_servers})

# Example topic and message
topic = 'streaming_topic'
message = 'erghejstrfjseh'

# Delivery report callback
def delivery_report(err, msg):
    if err is not None:
        print(f'Failed to deliver message: {err}')
    else:
        print(f'Message delivered to {msg.topic()} [{msg.partition()}]')

# Produce a message asynchronously
producer.produce(topic, message.encode('utf-8'), callback=delivery_report)

# Wait for all messages to be delivered
producer.flush()
