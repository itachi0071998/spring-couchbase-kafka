package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.application.constants.ApplicationConstants;
import com.tesco.demo.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class KafkaMessageProducer {

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

//    @Qualifier("producer")
    private KafkaSender<String, String> sender;

    public Mono<Price> publisher(Price price){
        return sendMessages(price).single().map(response -> price);
    }

    private Flux<String> sendMessages(Price commandMessage) {
        log.info("MSG=Publishing Message to topic={}, DocumentId={}",
                ApplicationConstants.TOPIC, commandMessage.getDocumentId());
        sender = kafkaProducerConfig.sender();
        return Flux.defer(() -> sender.send(Mono.just(createSenderRecord(commandMessage))))
                .doOnNext(resultData -> log.info("MSG=Completed publishing message on to topic={} DocumentId={} topicPartitionDetails={}",
                        ApplicationConstants.TOPIC, commandMessage.getDocumentId(),resultData.recordMetadata().toString()))
                .map(resultData -> processSenderResult(resultData, commandMessage))
                .doOnError(error -> log.error("MSG=Error in posting Message to topic={}, documentId={}, error={}", ApplicationConstants.TOPIC, commandMessage.getDocumentId(), error));
    }

    private SenderRecord<String, String, String> createSenderRecord(Price commandMessage) {
        ProducerRecord<String, String> producerRecord =   new ProducerRecord<String, String>(ApplicationConstants.TOPIC,
                commandMessage.getDocumentId(),
                commandMessage.toString());
        return SenderRecord.create(producerRecord, commandMessage.getDocumentId());
    }

    private String processSenderResult(SenderResult<String> senderResult, Price commandMessage) {
        if (null != senderResult.exception()) {
            throw new RuntimeException(senderResult.exception());
        }
        log.debug("MSG=Completed publishing message to topic={}, correlationMetadata={}, " +
                        "commandStatusId={}, priceIntentId={}",
                ApplicationConstants.TOPIC, senderResult.correlationMetadata(), commandMessage.getDocumentId(),
                commandMessage.getGtin());
        return senderResult.correlationMetadata().toString();
    }

}
