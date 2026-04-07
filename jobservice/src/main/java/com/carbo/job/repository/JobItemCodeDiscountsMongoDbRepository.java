package com.carbo.job.repository;

import com.carbo.job.model.JobItemCodeDiscounts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobItemCodeDiscountsMongoDbRepository extends MongoRepository<JobItemCodeDiscounts, String> {

    JobItemCodeDiscounts findByOrganizationIdAndJobId(String organizationId, String jobId);

}
