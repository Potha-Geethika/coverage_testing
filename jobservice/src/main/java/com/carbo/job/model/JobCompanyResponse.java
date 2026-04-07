package com.carbo.job.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobCompanyResponse {
    private String jobNumber;
    private String fleet;

    private String operator;
    private String Pad;

    private String location;
    private Boolean zipper;

    private Integer targetStagesPerDay;

    private String status;

    private String proppantSchematicType;
}
