package com.tesco.demo.controller;


import com.tesco.demo.infrastructure.kafkaReactor.KafkaReactorConsumer;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaReactorProducer;
import com.tesco.demo.model.Price;
import com.tesco.demo.infrastructure.repository.PriceRepository;
import lombok.extern.slf4j.Slf4j;
//import com.tesco.demo.infrastructure.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/minprice")
public class MinimumPriceController {

//    @Autowired
//    private KafkaProducer producer;
    @Autowired
    private PriceRepository repository;
    @Autowired
    private KafkaReactorProducer kafkaPublisher;

    @Autowired
    private KafkaReactorConsumer kafkaConsumer;

    @GetMapping("{documentId}")
    public Mono<ResponseEntity<Price>> getProduct(@PathVariable String documentId) {
        return repository.findById(documentId)
                .map(price -> ResponseEntity.accepted().body(price))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> saveProduct(@RequestHeader(value = "documentId") String documentId,
            @RequestBody Price price) {
        price.setDocumentId(documentId);
        return repository.save(price).doOnNext(response -> kafkaPublisher.sendMessages(response))
                .doOnError(error -> {log.error("error found {}", error);
                    ResponseEntity.badRequest().body(error);})
                .map(response -> {
                    kafkaConsumer.consumeMessage();
                    return ResponseEntity.accepted().body("location: /minprice/"+response.getDocumentId());});
    }

    @PutMapping("{documentId}")
    public Mono<ResponseEntity<String>> updateProduct(@PathVariable String documentId,
                                                       @RequestBody Price price) {
//        producer.sendMessage(price.toString());
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
                    return ResponseEntity.accepted().body("location: /minprice/"+ updatePrice.getDocumentId());})
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux getProductByGtin(@RequestParam String gtin){
        return repository.findByGtin(gtin).doOnNext(response -> log.info("the response is {}", response))
                .doOnError(error -> {
                    log.error("the error is {}", error);
                ResponseEntity.notFound().build();})
                .map(response -> ResponseEntity.accepted().body(response));
    }
}


