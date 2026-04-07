package com.carbo.job.repository;

import com.carbo.job.model.JobDashboardWellInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JobDashboardWellInfoRepository extends MongoRepository<JobDashboardWellInfo, String> {
    Optional<JobDashboardWellInfo> findByOrganizationIdAndJobIdAndWellId(String organizationId, String jobId, String wellName);
}
