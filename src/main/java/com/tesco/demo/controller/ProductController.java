package com.tesco.demo.controller;


import com.tesco.demo.model.Product;
import com.tesco.demo.infrastructure.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import com.tesco.demo.infrastructure.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/minprice")
public class ProductController {

    @Autowired
    private KafkaProducer producer;
    @Autowired
    private ProductRepository repository;


    @GetMapping("{documentId}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable String documentId) {
        return repository.findById(documentId)
                .map(product -> ResponseEntity.accepted().body(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<String>> saveProduct(@RequestHeader(value = "documentId") String documentId,
            @RequestBody Product product) {
        product.setDocumentId(documentId);
        producer.sendMessage(product.toString());
        return repository.save(product)
                .doOnError(error -> {log.error("error found {}", error);
                    ResponseEntity.badRequest().body(error);})
                .map(response -> ResponseEntity.accepted().body("location: /minprice/"+response.getDocumentId()));
    }

    @PutMapping("{documentId}")
    public Mono<ResponseEntity<String>> updateProduct(@PathVariable String id,
                                                       @RequestBody Product product) {
        producer.sendMessage(product.toString());
        return repository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setCountry(product.getCountry());
                    existingProduct.setMinimumPrice(product.getMinimumPrice());
                    existingProduct.setCurrency(product.getCurrency());
                    existingProduct.setEffectiveDateTime(product.getEffectiveDateTime());
                    existingProduct.setEffectiveDateTimeOffset(product.getEffectiveDateTimeOffset());
                    existingProduct.setEnrichedEventId(product.getEnrichedEventId());
                    existingProduct.setGtin(product.getGtin());
                    existingProduct.setReason(product.getReason());
                    return repository.save(existingProduct);
                })
                .map(updateProduct -> ResponseEntity.accepted().body("location: /minprice/"+updateProduct.getDocumentId()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux getProductByGtin(@RequestParam String gtin){
        return repository.findByGtin(gtin)
                .doOnError(error -> {
                    log.error("the error is {}", error);
                ResponseEntity.notFound().build();})
                .map(response -> ResponseEntity.accepted().body(response));
    }
}


