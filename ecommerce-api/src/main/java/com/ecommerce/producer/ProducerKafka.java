package com.ecommerce.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Component
public class ProducerKafka {
    Producer producer;
    @PostConstruct
    public void init(){
        Properties config=new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,new StringSerializer().getClass().getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,new StringSerializer().getClass().getName());

        producer= new KafkaProducer<String,String >(config);
    }
    public void send(String term){
        ProducerRecord<String,String> rec=new ProducerRecord<String, String>("search-data",term);
        producer.send(rec); //Search term sent to the topic "search-data"
    }
    public void close(){
        producer.close();
    }
}
