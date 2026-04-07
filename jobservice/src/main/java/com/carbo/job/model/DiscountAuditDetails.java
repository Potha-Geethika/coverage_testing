package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountAuditDetails {

    @Field("createdBy")
    String createdBy;

    @Field("createdTime")
    Long createdTime;

    @Field("stageDiscountFor")
    String stageDiscountFor;

}
