package com.tesco.demo.helper;

import com.tesco.demo.model.Price;

public class PriceHelper {

    public static Price priceBulder(){
        return Price.builder().minimumPrice(5.6).country("India")
                .currency("INR")
                .documentId("testminimumprice")
                .effectiveDateTimeOffset("random")
                .enrichedEventId("random")
                .gtin("1234567891234")
                .reason("testing purpose")
                .effectiveDateTime(231231231).build();
    }
}
