package com.tesco.demo.application;


import com.tesco.demo.application.constants.ApplicationConstants;
import com.tesco.demo.application.constants.EndPointConstant;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaMessageReceiver;
import com.tesco.demo.infrastructure.kafkaReactor.KafkaMessageProducer;
import com.tesco.demo.infrastructure.repository.PriceRepository;
import com.tesco.demo.model.Price;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequestMapping(EndPointConstant.MINIMUM_PRICE)
public class MinimumPriceController {



    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    private PriceRepository repository;
    

    @Autowired
    private KafkaMessageReceiver kafkaConsumer;

    @GetMapping(EndPointConstant.DOCUMENT_ID)
    public Mono<ResponseEntity<String>> getMinimumPrice(@PathVariable String documentId) {
        return repository.findById(documentId)
                .map(price -> ResponseEntity.ok(price.toString()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createMinimumPrice(@RequestHeader String documentId, @RequestBody Price price) {
        price.setDocumentId(documentId);
        return repository.save(price)
                .flatMap(response -> kafkaMessageProducer.publisher(response))
                .doOnNext(response -> log.info("{} published to the topic {}", response.toString(), ApplicationConstants.TOPIC))
                .doOnError(error -> {log.error("error found {}", error);
                    ResponseEntity.badRequest().body(error);})
                .map(response -> {
                    kafkaConsumer.consumeMessage();
                    return ResponseEntity.ok("location:" +EndPointConstant.MINIMUM_PRICE + "/" + response.getDocumentId());});
    }

    @PutMapping(EndPointConstant.DOCUMENT_ID)
    public Mono<ResponseEntity<String>> updateMinimumPrice(@PathVariable String documentId,
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
                    return repository.save(existingPrice); })
                .flatMap(response -> kafkaMessageProducer.publisher(response))
                .doOnNext(response -> log.info("{} published to the topic {}", response.toString(), ApplicationConstants.TOPIC))
                .map(updatePrice -> {
                    kafkaConsumer.consumeMessage();
                    return ResponseEntity.ok("location:" +EndPointConstant.MINIMUM_PRICE + "/"+ updatePrice.getDocumentId());})
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux getMinimumPriceByGtin(@RequestParam String gtin){
        return repository.findByGtin(gtin)
                .doOnNext(response -> log.info("the response is {}", response))
                .doOnError(error -> {
                    log.error("the error is {}", error);
                ResponseEntity.notFound().build();})
                .map(response -> response);
    }

}


