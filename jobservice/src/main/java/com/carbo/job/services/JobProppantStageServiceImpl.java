package com.carbo.job.services;

import static com.carbo.job.utils.ControllerUtil.getOrganizationId;
import java.util.Map;
import com.carbo.job.model.Job;
import com.carbo.job.model.JobInProgressProppantStagePatchDto;
import com.carbo.job.repository.JobProppantStageRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobProppantStageServiceImpl implements JobProppantStageService {

    private final JobProppantStageRepository jobProppantStageRepository;

    @Override
    public ResponseEntity<?> updateInProgressProppantStage(HttpServletRequest request, String jobId, JobInProgressProppantStagePatchDto patchDto) {
        log.info("Updating inProgressProppantStage for job: {}, well: {}", jobId, patchDto.getWellId());

        try {
            // Validate using pattern matching
            validateRequest(jobId, patchDto);

            String organizationId = getOrganizationId(request);

            // Check if job exists
            Job job = jobProppantStageRepository.getJobByIdAndOrganizationId(jobId, organizationId);
            if (job == null) {
                log.error("Job not found with id: {} for organization: {}", jobId, organizationId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of(
                                             "error", "Job not found",
                                             "jobId", jobId,
                                             "timestamp", System.currentTimeMillis()
                                     ));
            }

            // Set timestamps
            var now = System.currentTimeMillis();
            var stage = patchDto.getInProgressProppantStage();
            if (stage.getModified() == null) {
                stage.setModified(now);
            }
            if (stage.getCreated() == null) {
                stage.setCreated(now);
            }

            // Update and check result
            jobProppantStageRepository.updateInProgressProppantStage(
                    jobId,
                    patchDto.getWellId(),
                    stage,
                    patchDto.getLastModifiedBy(),
                    organizationId
            );

            log.info("Successfully updated inProgressProppantStage for job: {}", jobId);

            // ONLY fetch and return job if update was successful
            Job updatedJob = jobProppantStageRepository.getJobByIdAndOrganizationId(jobId, organizationId);
            if(!ObjectUtils.isEmpty(updatedJob)) {
                return ResponseEntity.ok(updatedJob);
            } else {
                log.error("Error updating inProgressProppantStage for job: {}, well: {}",
                        jobId, patchDto.getWellId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(Map.of(
                                             "error", "Internal server error",
                                             "jobId", jobId,
                                             "wellId", patchDto.getWellId(),
                                             "timestamp", System.currentTimeMillis()
                                     ));
            }

        } catch (IllegalArgumentException e) {
            log.error("Validation error for job: {}, well: {}, error: {}",
                    jobId, patchDto.getWellId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of(
                                         "error", "Validation failed",
                                         "message", e.getMessage(),
                                         "jobId", jobId,
                                         "wellId", patchDto.getWellId(),
                                         "timestamp", System.currentTimeMillis()
                                 ));

        } catch (NullPointerException e) {
            log.error("Null pointer exception for job: {}, well: {}", jobId, patchDto.getWellId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of(
                                         "error", "Missing required field",
                                         "message", "Required field is null: " + e.getMessage(),
                                         "jobId", jobId,
                                         "timestamp", System.currentTimeMillis()
                                 ));

        } catch (Exception e) {
            log.error("Error updating inProgressProppantStage for job: {}, well: {}",
                    jobId, patchDto.getWellId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of(
                                         "error", "Internal server error",
                                         "message", e.getMessage(),
                                         "jobId", jobId,
                                         "wellId", patchDto.getWellId(),
                                         "timestamp", System.currentTimeMillis()
                                 ));
        }
    }

    /**
     * Validate request
     * */
    private void validateRequest(String jobId, JobInProgressProppantStagePatchDto patchDto) {
        // Check job exists
        if (!jobProppantStageRepository.existsByJobId(jobId)) {
            throw new RuntimeException("Job not found with ID: " + jobId);
        }

        // Validate required fields using pattern matching
        switch (patchDto) {
            case JobInProgressProppantStagePatchDto dto when dto.getWellId() == null ->
                    throw new RuntimeException("Well ID is required");
            case JobInProgressProppantStagePatchDto dto when dto.getInProgressProppantStage() == null ->
                    throw new RuntimeException("InProgressProppantStage data is required");
            default -> {} // Valid
        }
    }
}