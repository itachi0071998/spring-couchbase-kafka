package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.application.constants.ApplicationConstants;
import com.tesco.demo.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Component
public class KafkaMessageProducer {

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    @Qualifier("producer")
    private KafkaSender<String, String> sender;

    public Mono<Price> publisher(Price price){
        return sendMessages(price).single().map(response -> price);
    }

    private Flux<String> sendMessages(Price price) {
        log.info("MSG=Publishing Message to topic={}, DocumentId={}",
                ApplicationConstants.TOPIC, price.getDocumentId());
        return Flux.defer(() -> sender.send(Mono.just(createSenderRecord(price))))
                .doOnNext(resultData -> log.info("MSG=Completed publishing message on to topic={} DocumentId={} topicPartitionDetails={}",
                        ApplicationConstants.TOPIC, price.getDocumentId(),resultData.recordMetadata().toString()))
                .map(resultData -> processSenderResult(resultData, price))
                .doOnError(error -> log.error("MSG=Error in posting Message to topic={}, documentId={}, error={}", ApplicationConstants.TOPIC, price.getDocumentId(), error));
    }

    private SenderRecord<String, String, String> createSenderRecord(Price price) {
        ProducerRecord<String, String> producerRecord =   new ProducerRecord<String, String>(ApplicationConstants.TOPIC,
                price.getDocumentId(),
                price.toString());
        return SenderRecord.create(producerRecord, price.getDocumentId());
    }

    private String processSenderResult(SenderResult<String> senderResult, Price price) {
        if (null != senderResult.exception()) {
            throw new RuntimeException(senderResult.exception());
        }
        log.debug("MSG=Completed publishing message to topic={}, correlationMetadata={}, " +
                        "commandStatusId={}, priceIntentId={}",
                ApplicationConstants.TOPIC, senderResult.correlationMetadata(), price.getDocumentId(),
                price.getGtin());
        return senderResult.correlationMetadata().toString();
    }

}
