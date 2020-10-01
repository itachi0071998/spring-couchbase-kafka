package com.tesco.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.Objects;
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    private String documentId;


    private String country;

    private String gtin;


    private long effectiveDateTime;
    private String effectiveDateTimeOffset;
    private String currency;
    private String enrichedEventId;

    private String reason;
    private double minimumPrice;



    @Override
    public String toString() {
        return "Product{" +
                "id='" + documentId + '\'' +
                ", name='" + gtin + '\'' +
                ", price=" + minimumPrice +
                '}';
    }
}

