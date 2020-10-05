package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.application.constants.ApplicationConstants;
import com.tesco.demo.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    private KafkaReceiver<String, String> kafkaReceiver;

    public Flux<Disposable> consumeMessage(){
        return Flux.just(kafkaConsumer().subscribe(r -> {
            log.info("Message is recieved from topic={}, message is {}", ApplicationConstants.TOPIC,r);
        }));
    }

    private Flux<ReceiverRecord<String, String>> kafkaConsumer(){
        log.info("message is consuming from the topic={}", ApplicationConstants.TOPIC);
        kafkaReceiver = kafkaConsumerConfig.receiver();
        Flux<ReceiverRecord<String, String>> messages =
                Flux.defer(() -> kafkaReceiver
                        .receive());
        return messages.doOnNext(response -> {
            response.receiverOffset().acknowledge();
        });
    }

}
