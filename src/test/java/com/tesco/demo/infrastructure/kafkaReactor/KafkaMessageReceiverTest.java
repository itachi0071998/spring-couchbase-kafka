package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.model.Price;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;


@RunWith(MockitoJUnitRunner.class)
public class KafkaMessageReceiverTest {

    @InjectMocks
    private KafkaMessageReceiver kafkaMessageReceiver;

    @Mock
    private KafkaReceiver<String, String> kafkaReceiver;


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
        receiverRecord = new ReceiverRecord<String, String>(consumerRecord, null);
    }

    @Test
    public void consumeMessageTest(){
        Flux<ReceiverRecord<String, String>> fluxRecord = Flux.just(receiverRecord);
        Mockito.when(kafkaReceiver.receive()).thenReturn(fluxRecord);
        Flux<Disposable> consumeMessageResponse = kafkaMessageReceiver.consumeMessage();
        StepVerifier.create(consumeMessageResponse)
                .expectNextMatches(r -> {
                    Assert.assertNotNull(r);
                    return true;
                }).verifyComplete();
//        Mockito.verify(kafkaReceiver, Mockito.times(1)).receive();
    }
}
