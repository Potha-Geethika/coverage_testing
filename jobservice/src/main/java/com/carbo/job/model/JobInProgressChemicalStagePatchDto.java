package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInProgressChemicalStagePatchDto {
    
    private String id;
    private String wellId;
    private ChemicalStageNoIndex inProgressChemicalStage;
    private String lastModifiedBy;
}