package com.carbo.job.model.v2.dto;

import lombok.Data;

@Data
public class BucketStatusOptionResponseDto {
    private String id;
    private String statusName;
    private String organizationId;
    private boolean isDefault;
    private Long createdTime;
    private String createdBy;
}
