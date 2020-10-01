package com.tesco.demo.infrastructure.repository;

import com.tesco.demo.model.Product;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository
        extends ReactiveCouchbaseRepository<Product, String> {

    @Query("#{#n1ql.selectEntity} where #{#n1ql.filter} AND gtin = $1 ")
    Flux<Product> findByGtin(String gtin);
}
