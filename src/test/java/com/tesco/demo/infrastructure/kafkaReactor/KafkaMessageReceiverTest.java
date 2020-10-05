package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.model.Price;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;


@RunWith(MockitoJUnitRunner.class)
public class KafkaMessageReceiverTest {

    @InjectMocks
    private KafkaMessageReceiver kafkaMessageReceiver;

    @Mock
    private KafkaReceiver<String, String> kafkaReceiver;

    @Mock
    private KafkaConsumerConfig kafkaConsumerConfig;


    private Price price;
    private ReceiverRecord<String, String> receiverRecord;

    @Before
    public void setUp(){
        price = Price.builder().minimumPrice(5.6).country("India")
                .currency("INR")
                .documentId("kafkatestminimumprice")
                .effectiveDateTimeOffset("dssad")
                .enrichedEventId("idsd")
                .gtin("dsajdhaskji21oi29")
                .reason("testing purpose")
                .effectiveDateTime(231231231).build();
        ConsumerRecord<String, String> consumerRecord =
                new ConsumerRecord<String, String>("prices", 0, 1, "consumer", price.toString());
        receiverRecord = new ReceiverRecord<String, String>(consumerRecord, new ReceiverOffset() {
            @Override
            public TopicPartition topicPartition() {
                return null;
            }

            @Override
            public long offset() {
                return 0;
            }

            @Override
            public void acknowledge() {

            }

            @Override
            public Mono<Void> commit() {
                return Mono.empty();
            }
        });
    }

    @Test
    public void consumeMessageTest(){
        Flux<ReceiverRecord<String, String>> fluxRecord = Flux.just(receiverRecord);
        Mockito.when(kafkaConsumerConfig.receiver()).thenReturn(kafkaReceiver);
        Mockito.when(kafkaReceiver.receive()).thenReturn(fluxRecord);
        Flux<Disposable> receiverResponse = kafkaMessageReceiver.consumeMessage();
        StepVerifier.create(receiverResponse)
                .expectNextMatches(response -> {
                    Assert.assertNotNull(response);
                    return true;
                })
                .verifyComplete();
    }
}
