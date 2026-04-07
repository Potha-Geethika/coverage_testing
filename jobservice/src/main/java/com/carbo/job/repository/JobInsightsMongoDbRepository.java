package com.carbo.job.repository;

import com.carbo.job.model.JobInsights;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobInsightsMongoDbRepository extends MongoRepository<JobInsights, String> {

    Optional<JobInsights> findTopByJobIdAndOrganizationIdOrderByModifiedDesc(String jobId, String organizationId);


    List<JobInsights> findAllByOrganizationId(String organizationId);

}
