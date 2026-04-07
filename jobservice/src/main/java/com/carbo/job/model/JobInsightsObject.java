package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobInsightsObject {
    private String id;
    private String jobNumber;
    private String serviceCompany;
    private String districtId;
    private String fleet;
    private String organizationId;
    private String operator;
}
