package com.carbo.job.repository;

import com.carbo.job.model.OpsActivityCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpsActivityCodeMongoDbRepository extends MongoRepository<OpsActivityCode, String> {
    List<OpsActivityCode> findByOrganizationId(String organizationId);
}
