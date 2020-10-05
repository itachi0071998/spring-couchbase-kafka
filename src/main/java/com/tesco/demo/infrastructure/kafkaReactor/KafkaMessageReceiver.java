package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.application.constants.ApplicationConstants;
import com.tesco.demo.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KafkaMessageReceiver {

    private final ReceiverOptions<String, String> receiverOptions;

    public KafkaMessageReceiver(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.<String, String>create(props).subscription(Collections.singleton(ApplicationConstants.TOPIC));
    }

    public Flux<Disposable> consumeMessage(){
        return Flux.just(kafkaConsumer().subscribe(r -> {
            log.info("Message is recieved from topic={}, message is {}", ApplicationConstants.TOPIC,r);
        }));
    }

    private Flux<ReceiverRecord<String, String>> kafkaConsumer(){
        log.info("message is consuming from the topic={}", ApplicationConstants.TOPIC);
        Flux<ReceiverRecord<String, String>> messages =
                Flux.defer(() -> KafkaReceiver.create(receiverOptions)
                        .receive());
        return messages.doOnNext(response -> {
            response.receiverOffset().acknowledge();
        });
    }

}
