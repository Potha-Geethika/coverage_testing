package com.carbo.job.model.v2.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BucketTestRequestDto {
    private String unitNumber;
    private LocalDate date;
    private String fleet;
    private List<String> personnel;
    private List<PumpStatusDto> pumpStatuses;
    private String notes;
    private String jobId;
}
