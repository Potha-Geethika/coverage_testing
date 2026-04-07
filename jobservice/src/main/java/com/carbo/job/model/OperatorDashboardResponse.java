package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperatorDashboardResponse {

    private Long date;
    private String organizationId;
    private String jobId;
    private String jobNumber;
    private String fleet;
    private String operator;
    private Integer targetStagePerDay;
    private Integer actualStagePerDay;
    private Integer targetHoursPerDay;
    private Float actualHoursPerDay;
    private Float nptHours;
    private Float scheduledHours;
    private String sharedOrganizationId;
    private String pad;
    private String padId;

}