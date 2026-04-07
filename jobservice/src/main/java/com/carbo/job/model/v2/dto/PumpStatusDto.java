package com.carbo.job.model.v2.dto;

import lombok.Data;

@Data
public class PumpStatusDto {
    private String pumpNumber;
    private Double maxGpm;
    private Double hiScale;
    private String status;
}
