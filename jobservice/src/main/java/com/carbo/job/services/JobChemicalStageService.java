package com.carbo.job.services;

import org.springframework.http.ResponseEntity;
import com.carbo.job.model.Job;
import com.carbo.job.model.JobInProgressChemicalStagePatchDto;
import jakarta.servlet.http.HttpServletRequest;

public interface JobChemicalStageService {

    /**
     * Update inProgressChemicalStage for a specific well in a job
     */
    ResponseEntity<?> updateInProgressChemicalStage(HttpServletRequest request, String jobId, JobInProgressChemicalStagePatchDto patchDto);
}