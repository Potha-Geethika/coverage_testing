package com.carbo.job.repository;

import com.carbo.job.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedJobMongoDbRepository extends MongoRepository<Job, String> {
    List<Job> findBySharedWithOrganizationId(String organizationId);
}
