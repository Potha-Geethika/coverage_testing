package com.carbo.job.services;

import org.springframework.http.ResponseEntity;
import com.carbo.job.model.Job;
import com.carbo.job.model.JobInProgressProppantStagePatchDto;
import jakarta.servlet.http.HttpServletRequest;

public interface JobProppantStageService {

    /**
     * Update inProgressProppantStage for a specific well in a job
     */
    ResponseEntity<?> updateInProgressProppantStage(HttpServletRequest request, String jobId, JobInProgressProppantStagePatchDto patchDto);
}