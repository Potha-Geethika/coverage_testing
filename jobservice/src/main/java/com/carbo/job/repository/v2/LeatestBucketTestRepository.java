package com.carbo.job.repository.v2;

import com.carbo.job.model.v2.LeatestBucketTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeatestBucketTestRepository extends MongoRepository<LeatestBucketTest, String> {

    List<LeatestBucketTest> findByJobIdAndOrganizationId(String jobId, String organizationId);

    Optional<LeatestBucketTest> findByIdAndOrganizationId(String id, String organizationId);

    void deleteByIdAndOrganizationId(String id, String organizationId);
}
