package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobInProgressProppantStagePatchDto {

    private String id;

    private String wellId;

    private ProppantStageNoIndex inProgressProppantStage;

    private String lastModifiedBy;
}