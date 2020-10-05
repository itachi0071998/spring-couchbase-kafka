package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.application.constants.ApplicationConstants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaConsumerConfig {
    private final ReceiverOptions<String, String> receiverOptions;


    public KafkaConsumerConfig(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.<String, String>create(props).subscription(Collections.singleton(ApplicationConstants.TOPIC));
    }


    public KafkaReceiver<String, String> receiver(){
        return KafkaReceiver.create(receiverOptions);
    }
}
