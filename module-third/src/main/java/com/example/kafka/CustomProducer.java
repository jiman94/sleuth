package com.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomProducer {

    private final KafkaConfig config;
    private KafkaProducer<String, String> producer = null;

    @PostConstruct
    public void build(){
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getProducer().getKeySerializer());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getProducer().getValueSerializer());
        producer = new KafkaProducer<>(properties);
    }

    public void send(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(config.getTemplate().getDefaultTopic(), message);

        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                log.info("publish message: {}", message);
                if (exception!=null){
                    log.info(exception.getMessage());
                }
            }
        });
    }

}
