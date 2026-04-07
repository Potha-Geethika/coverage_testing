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
public class RpfInformation {

    @Field("dueDate")
    private Long dueDate;

    @Builder.Default
    @Field("validDate")
    private long validDate = 1714501800L;

    @Field("company")
    private String company;

    @Field("companyId")
    private String companyId;

    @Field("formation")
    private String formation;

    @Field("state")
    private String state;

    @Field("districtId")
    private String districtId;

    @Field("districtName")
    private String districtName;

    @Field("county")
    private String county;

    @Field("padName")
    private String padName;

    @Field("proposalTitle")
    private String proposalTitle;

    @Field("opportunity")
    private Opportunity opportunity;

}
