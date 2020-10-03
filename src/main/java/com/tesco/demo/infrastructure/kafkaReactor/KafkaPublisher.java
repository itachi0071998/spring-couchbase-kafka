package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.application.constants.ApplicationConstants;
import com.tesco.demo.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KafkaPublisher {

    private final KafkaSender<String, String> sender;

    public KafkaPublisher() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        sender = KafkaSender.create(senderOptions);
    }

    public void sendMessages(Price commandMessage) {
        log.info("MSG=Publishing Message to topic={}, DocumentId={}",
                ApplicationConstants.TOPIC, commandMessage.getDocumentId());
        Flux.defer(() -> sender.send(Mono.just(createSenderRecord(commandMessage))))
                .doOnNext(resultData -> log.info("MSG=Completed publishing message on to topic={} DocumentId={} topicPartitionDetails={}",
                        ApplicationConstants.TOPIC, commandMessage.getDocumentId(),resultData.recordMetadata().toString()))
                .concatMap(resultData -> processSenderResult(resultData, commandMessage))
                .doOnError(error -> log.error("MSG=Error in posting Message to topic={}, documentId={}, error={}", ApplicationConstants.TOPIC, commandMessage.getDocumentId(), error))
        .subscribe();
    }

    private SenderRecord<String, String, String> createSenderRecord(Price commandMessage) {
        ProducerRecord<String, String> producerRecord =   new ProducerRecord<String, String>(ApplicationConstants.TOPIC,
                commandMessage.getDocumentId(),
                commandMessage.toString());
        return SenderRecord.create(producerRecord, commandMessage.getDocumentId());
    }

    private Mono<String> processSenderResult(SenderResult senderResult, Price commandMessage) {
        if (null != senderResult.exception()) {
            throw new RuntimeException(senderResult.exception());
        }
        log.debug("MSG=Completed publishing message to topic={}, correlationMetadata={}, " +
                        "commandStatusId={}, priceIntentId={}",
                ApplicationConstants.TOPIC, senderResult.correlationMetadata(), commandMessage.getDocumentId(),
                commandMessage.getGtin());
        return Mono.just(senderResult.correlationMetadata().toString());
    }

}
