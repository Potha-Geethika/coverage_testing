package com.carbo.job.repository;

import com.carbo.job.model.BucketTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketTestsRepository extends MongoRepository<BucketTest, String> {

    BucketTest findByJobIdAndOrganizationId(String jobId, String organizationId);

}
