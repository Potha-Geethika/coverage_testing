package com.carbo.job.repository;

import com.carbo.job.model.Job;
import com.carbo.job.model.ProppantStageNoIndex;

public interface JobProppantStageRepository {

    /**
     * Update inProgressProppantStage for a specific well in a job
     */
    void updateInProgressProppantStage(String jobId, String wellId, ProppantStageNoIndex stageDto, String modifiedBy, String organizationId);

    /**
     * Check if job exists
     */
    boolean existsByJobId(String jobId);


    Job getJobByIdAndOrganizationId(String jobId, String organizationId);
}