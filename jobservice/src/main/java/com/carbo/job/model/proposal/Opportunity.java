package com.carbo.job.model.proposal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {

    @Field("companyName")
    private String companyName;

    @Field("opportunityName")
    private String opportunityName;

    @Field("date")
    private String date;

    @Field("leadId")
    private String leadId;
}
