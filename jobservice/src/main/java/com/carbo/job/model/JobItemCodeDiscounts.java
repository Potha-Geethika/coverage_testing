package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "job-itemCode-discounts")
@CompoundIndex(def = "{'jobId': 1, 'organizationId': 1}", name = "jobId_organizationId_index", unique = true)
public class JobItemCodeDiscounts {

    @Id
    private String id;

    @Field
    private String jobId;

    @Field
    private String organizationId;

    @Field
    private ItemCodeDetails itemDetailsCurrent;

    @Field
    private List<ItemCodeDetails> itemDetailsHistorical;

}
