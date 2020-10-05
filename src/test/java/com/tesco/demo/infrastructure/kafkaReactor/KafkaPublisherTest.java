package com.tesco.demo.infrastructure.kafkaReactor;

import com.tesco.demo.model.Price;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class KafkaPublisherTest {

    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    private Price price;

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
    }

    @Test
    public void publisherTest(){
        Mono<Price> publisherResponse = kafkaPublisher.publisher(price);
        StepVerifier.create(publisherResponse)
                .expectNextMatches(response -> {
                    Assert.assertThat(response.toString(), is(price.toString()));
                    return true;
                }).verifyComplete();

    }


}
