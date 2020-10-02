package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.model.Price;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class KafkaReactorProducer {



    private static final Logger logger = Loggers.getLogger(KafkaReactorProducer.class);
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "prices";

    private KafkaSender<String, String> sender;
    private SimpleDateFormat dateFormat;

    public KafkaReactorProducer() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, String> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    public Flux<String> sendMessages(Price commandMessage) {

        logger.info("MSG=Publishing CommandMessage to CommandBus, topic={}, DocumentId={}",
                TOPIC, commandMessage.getDocumentId());

        return Flux.defer(() -> sender.send(Mono.just(createSenderRecord(commandMessage))))
                .doOnNext(resultData -> logger.info("MSG=Completed publishing message on to CommandBus topic={} DocumentId={} topicPartitionDetails={}",
                        TOPIC, commandMessage.getDocumentId(),resultData.recordMetadata().toString()))
                .concatMap(resultData -> processSenderResult(resultData, commandMessage))
                .doOnError(error -> logger.error("MSG=Error in posting Message to topic={}, documentId={}, error={}", TOPIC, commandMessage.getDocumentId(), error));
    }

    private SenderRecord<String, String, String> createSenderRecord(Price commandMessage) {
        ProducerRecord<String, String> producerRecord =   new ProducerRecord<String, String>(TOPIC,
                commandMessage.getDocumentId(),
                commandMessage.toString());
        return SenderRecord.create(producerRecord, commandMessage.getDocumentId());
    }

    private Mono<String> processSenderResult(SenderResult senderResult, Price commandMessage) {

        if (null != senderResult.exception()) {
            throw new RuntimeException(senderResult.exception());
        }
        logger.debug("MSG=Completed publishing message to CommandBus, topic={}, correlationMetadata={}, " +
                        "commandStatusId={}, priceIntentId={}",
                TOPIC, senderResult.correlationMetadata(), commandMessage.getDocumentId(),
                commandMessage.getGtin());
        return Mono.just(senderResult.correlationMetadata().toString());
    }


}
