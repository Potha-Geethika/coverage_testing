package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "unique_organizationid_pricebookname_index", def = "{'organizationId': 1, 'priceBookName': 1}", unique = true)
@Document(collection = "pricebook-v2")
public class PriceBook {
    @Id
    private String id;

    @NotEmpty(message = "priceBookName can not be empty")
    @Field("priceBookName")
    private String priceBookName;

    @Field("organizationId")
    private String organizationId;

    @Field("clonePricebookId")
    private String clonePricebookId;

    @Field("pricebookFinalized")
    private boolean pricebookFinalized;

}