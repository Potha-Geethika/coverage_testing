package com.carbo.job.repository.v2;

import com.carbo.job.model.v2.BucketStatusOption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BucketStatusOptionRepository extends MongoRepository<BucketStatusOption, String> {

    List<BucketStatusOption> findByOrganizationId(String organizationId);

    Optional<BucketStatusOption> findByStatusNameAndOrganizationId(String statusName, String organizationId);

    void deleteByIdAndOrganizationId(String id, String organizationId);
}
