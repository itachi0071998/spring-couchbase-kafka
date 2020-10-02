package com.tesco.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Price {

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
        return "{" +
                "  \ngtin='" + gtin + '\'' +
                ", \nname='" + gtin + '\'' +
                ", \nprice=" + minimumPrice +
                ", \ncountry='" + country +"'"+
                ", \neffectiveDateTime='" + effectiveDateTime +"'"+
                ", \neffectiveDateTimeOffset='" + effectiveDateTimeOffset +"'"+
                ", \nreason='" + reason +"'"+
                ", \nenrichedEventId='" + enrichedEventId +"'\n"+
                '}';
    }

}

