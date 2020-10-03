package com.tesco.demo.application.controller;


import com.tesco.demo.application.MinimumPriceController;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaMessageReceiver;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaPublisher;
import com.tesco.demo.infrastructure.repository.PriceRepository;
import com.tesco.demo.model.Price;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MinimumPriceControllerTest {

    @InjectMocks
    private MinimumPriceController priceController;

    @Mock
    private PriceRepository repository;
    @Mock
    private KafkaPublisher kafkaPublisher;
    @Mock
    private KafkaMessageReceiver kafkaMessageReceiver;

    private Price price;

    private String documentId;

    private String gtin;

    @Before
    public void setUp(){
        price = Price.builder().minimumPrice(5.6).country("India")
                .currency("INR")
                .documentId("testminimumprice")
                .effectiveDateTimeOffset("dssad")
                .enrichedEventId("idsd")
                .gtin("dsajdhaskji21oi29")
                .reason("testing purpose")
                .effectiveDateTime(231231231).build();

        documentId = "testminimumprice";
        gtin = "dsajdhaskji21oi29";
    }



    @Test
    public void createMinimumPriceTest(){
        Mockito.when(repository.save(price)).thenReturn(Mono.just(price));
        Mono<ResponseEntity<String>> createMinimumPriceResponse = priceController.createMinimumPrice(documentId, price);
        StepVerifier.create(createMinimumPriceResponse)
                .expectNextMatches(responseEntity -> {
                    Assert.assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
                    Assert.assertNotNull(responseEntity.getBody());
                    return true;
                }).verifyComplete();
    }

    @Test
    public void getMinimumPricetest(){
        Mockito.when(repository.findById(documentId)).thenReturn(Mono.just(price));
        Mono<ResponseEntity<String>> getMinimumPriceResponse = priceController.getMinimumPrice(documentId);
        StepVerifier.create(getMinimumPriceResponse)
                .expectNextMatches(responseEntity -> {
                    Assert.assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
                    Assert.assertThat(responseEntity.getBody(), is(price.toString()));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void updateMinimumPriceTest(){
        Mockito.when(repository.findById(documentId)).thenReturn(Mono.just(price));
        Mockito.when(repository.save(price)).thenReturn(Mono.just(price));
        Mono<ResponseEntity<String>> updateMinimumPriceResponse = priceController.updateMinimumPrice(documentId, price);
        StepVerifier.create(updateMinimumPriceResponse)
                .expectNextMatches(responseEntity -> {
                    Assert.assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
                    Assert.assertNotNull(responseEntity.getBody());
                    return true;
                }).verifyComplete();
    }

    @Test
    public void getMinimumPriceByGtinTest(){
        Mockito.when(repository.findByGtin(gtin)).thenReturn(Flux.just(price));
        Flux getMinimumPriceByGtinResponse = priceController.getMinimumPriceByGtin(gtin);
        StepVerifier.create(getMinimumPriceByGtinResponse)
                .expectNextMatches(responseEntity -> {
                    Assert.assertThat(responseEntity, is(price));
                    return true;
                }).verifyComplete();
    }

}
