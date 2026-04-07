package com.carbo.job.model.proposal.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentWisePrice {

    @Field("stagePrice")
    private double stagePrice = 0.0;

    @Field("scenarioPrice")
    private double scenarioPrice = 0.0;
}
