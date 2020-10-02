package com.tesco.demo.application.controller;


import com.tesco.demo.application.constants.EndPointConstant;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaMessageReceiver;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaPublisher;
import com.tesco.demo.model.Price;
import com.tesco.demo.infrastructure.repository.PriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(EndPointConstant.MINIMUM_PRICE)
public class MinimumPriceController {

    @Autowired
    private PriceRepository repository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private KafkaMessageReceiver kafkaConsumer;

    @GetMapping(EndPointConstant.DOCUMENT_ID)
    public Mono<ResponseEntity<String>> getProduct(@PathVariable String documentId) {
        return repository.findById(documentId)
                .map(price -> ResponseEntity.accepted().body(price.toString()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> saveProduct(@RequestBody Price price) {
        price.setDocumentId(UUID.randomUUID().toString());
        return repository.save(price).doOnNext(response -> kafkaPublisher.sendMessages(response))
                .doOnError(error -> {log.error("error found {}", error);
                    ResponseEntity.badRequest().body(error);})
                .map(response -> {
                    kafkaConsumer.consumeMessage();
                    return ResponseEntity.accepted().body("location:" +EndPointConstant.MINIMUM_PRICE + "/" + response.getDocumentId());});
    }

    @PutMapping(EndPointConstant.DOCUMENT_ID)
    public Mono<ResponseEntity<String>> updateProduct(@PathVariable String documentId,
                                                       @RequestBody Price price) {
        return repository.findById(documentId)
                .flatMap(existingPrice -> {
                    existingPrice.setCountry(price.getCountry());
                    existingPrice.setMinimumPrice(price.getMinimumPrice());
                    existingPrice.setCurrency(price.getCurrency());
                    existingPrice.setEffectiveDateTime(price.getEffectiveDateTime());
                    existingPrice.setEffectiveDateTimeOffset(price.getEffectiveDateTimeOffset());
                    existingPrice.setEnrichedEventId(price.getEnrichedEventId());
                    existingPrice.setGtin(price.getGtin());
                    existingPrice.setReason(price.getReason());
                    return repository.save(existingPrice);
                }).doOnNext(response -> kafkaPublisher.sendMessages(response))
                .map(updatePrice -> {
                    kafkaConsumer.consumeMessage();
                    return ResponseEntity.accepted().body("location:" +EndPointConstant.MINIMUM_PRICE + "/"+ updatePrice.getDocumentId());})
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux getProductByGtin(@RequestParam String gtin){
        return repository.findByGtin(gtin).doOnNext(response -> log.info("the response is {}", response))
                .doOnError(error -> {
                    log.error("the error is {}", error);
                ResponseEntity.notFound().build();})
                .map(response -> ResponseEntity.accepted().body(response.toString()));
    }

}


