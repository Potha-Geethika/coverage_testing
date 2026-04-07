package com.carbo.job.repository;

import com.carbo.job.model.widget.MaterialNeeded;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MaterialNeededRepository extends MongoRepository<MaterialNeeded, String> {
MaterialNeeded findByJobIdAndOrganizationId(String jobId, String organizationId);
}
