# kafka-producer-java-hello-world-async-with-keys
A simple Java Kafka async producer with keys.

## Dependencies (`pom.xml`)

- [kafka-clients](https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients/2.8.0)
- [slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple/1.7.30)

```xml
        <!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.8.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-simple -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.30</version>
            <!-- <scope>test</scope> -->
        </dependency>
```

## Code

```java
Logger logger = LoggerFactory.getLogger(App.class);

String server = "<you.server.ip.address>:9092";
String topic = "first_topic";
String message = "hello world!";

Properties properties = new Properties();
properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

for (int i = 0; i < 10; i++) {
    String key = "id_#_" + Integer.toString(i);
    String value = message + " #" + Integer.toString(i);

    ProducerRecord<String, String> record = new ProducerRecord<String,String>(topic, key, value);

    producer.send(record, new Callback() {
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e == null) {
                logger.info(
                    "---- Received metadata ----" +
                    "\nTopic: " + recordMetadata.topic() +
                    "\nPartition: " + recordMetadata.partition() + 
                    "\nOffset: " + recordMetadata.offset() +
                    "\nTimestamp: " + recordMetadata.timestamp()
                );
            } else {
                logger.error("Error while producing", e);
            }
        }
    });
}

producer.close();
```

## Test

Start a consumer, like:

```
bin/kafka-console-consumer.sh --bootstrap-server <you.server.ip.address>:9092 --topic first_topic
```

Run this app.