package com.carbo.job.repository;

import com.carbo.job.model.ChemicalStageNoIndex;
import com.carbo.job.model.Job;

public interface JobChemicalStageRepository {
    
    /**
     * Update inProgressChemicalStage for a specific well in a job
     */
    void updateInProgressChemicalStage(String jobId, String wellId, ChemicalStageNoIndex stageDto, String modifiedBy, String organizationId);
    
    /**
     * Check if job exists
     */
    boolean existsByJobId(String jobId);


    Job getJobByIdAndOrganizationId(String jobId, String organizationId);
}