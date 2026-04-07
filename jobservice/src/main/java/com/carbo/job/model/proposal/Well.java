package com.carbo.job.model.proposal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@CompoundIndex (name = "unique_organizationid_proposalId_index", def = "{'organizationId': 1,'proposalId': 1}", unique = true)
@Document(collection = "proposal-well-v2")
public class Well {
    @Id
    private String id;

    @Field ("proposalId")
    private String proposalId;

    //    @DBRef
    //    @Field ("priceBook")
    //    private PriceBook priceBook;

    @Field ("wells")
    private LinkedHashMap<String, LinkedHashMap<String, Double>> wells;

//    @Field ("wellAdditionalInfo")
//    private LinkedHashMap<String, WellAdditionalInfo> wellAdditionalInfo;

    @Field ("hhpCalculations")
    private HHPCalculations hhpCalculations;
//
//    @Field ("templateCalculations")
//    private TemplateCalculations templateCalculations;

    @Field ("padPrice")
    private double padPrice;

    @Field ("auditDetails")
    private AuditDetails auditDetails;

    @Field ("organizationId")
    private String organizationId;

//
//    @Field ("proppants")
//    private List<Proppant> proppants = new ArrayList<>();
//
//    @Field ("additionalChemicalTypes")
//    private Map<String, List<Chemical>> additionalChemicalTypes = new HashMap<>();

    @Field ("allWellCasing")
    private boolean allWellCasing;

}