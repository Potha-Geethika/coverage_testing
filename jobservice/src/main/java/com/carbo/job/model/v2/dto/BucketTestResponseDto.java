package com.carbo.job.model.v2.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BucketTestResponseDto {
    private String id;
    private String unitNumber;
    private LocalDate date;
    private String fleet;
    private List<String> personnel;
    private List<PumpStatusDto> pumpStatuses;
    private String notes;
    private String jobId;
    private String organizationId;
    private String createdBy;
    private Long createdTime;
    private String updatedBy;
    private Long updatedTime;
}
