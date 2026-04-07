package com.carbo.job.model.v2.dto;

import lombok.Data;

@Data
public class BucketStatusOptionRequestDto {
    private String statusName;

    private boolean isDefault;
}
