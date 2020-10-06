package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.helper.PriceHelper;
import com.tesco.demo.model.Price;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;


@RunWith(MockitoJUnitRunner.class)
public class KafkaMessageProducerTest {

    @InjectMocks
    private KafkaMessageProducer kafkaMessageProducer;

    @Mock
    private KafkaProducerConfig kafkaProducerConfig;

    @Mock
    private KafkaSender<String, String> kafkaSender;

    private Price price;
    private Flux<SenderResult<String>> senderResult;

    @Before
    public void setUp(){
        price = PriceHelper.priceBulder();

        RecordMetadata metadata = new RecordMetadata(new TopicPartition("prices", 0), 100L, 100L, 100L, 100L,
                512, 512);
        SenderResult result = new SenderResult() {
            @Override
            public RecordMetadata recordMetadata() {
                return metadata;
            }

            @Override
            public Exception exception() {
                return null;
            }

            @Override
            public Object correlationMetadata() {
                return "kafkatestminimumprice";
            }
        };
        senderResult = Flux.just(result);

    }

    @Test
    public void publisherTest(){
        Mockito.when(kafkaSender.send(Mockito.any(Mono.class))).thenReturn(senderResult);
        Mono<Price> publisherResponse = kafkaMessageProducer.publisher(price);
        StepVerifier.create(publisherResponse)
                .expectNextMatches(response -> {
                    Assert.assertNotNull(publisherResponse);
                    return true;
                }).verifyComplete();

    }


}
