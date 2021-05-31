package com.example;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Kafka Simple Producer");

        // Add logger
        Logger logger = LoggerFactory.getLogger(App.class);

        // create Producer properties
        // https://kafka.apache.org/documentation/#producerconfigs

        String server = "172.25.145.150:9092";
        String topic = "first_topic";
        String message = "hello world!";

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // produce some records
        for (int i = 0; i < 10; i++) {
            String key = "id_#_" + Integer.toString(i);
            String value = message + " #" + Integer.toString(i);

            // create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<String,String>(topic, key, value);

            // send data async
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        // success
                        logger.info(
                            "---- Received metadata ----" +
                            "\nTopic: " + recordMetadata.topic() +
                            "\nPartition: " + recordMetadata.partition() + 
                            "\nOffset: " + recordMetadata.offset() +
                            "\nTimestamp: " + recordMetadata.timestamp()
                        );
                    } else {
                        // error
                        logger.error("Error while producing", e);
                    }
                }
            });
        }

        // producer.flush(); // just flush, or
        producer.close(); // flush and close
    }
}
