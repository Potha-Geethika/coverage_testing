package com.carbo.job.repository;

import com.carbo.job.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationMongoDbRepository extends MongoRepository<Organization, String> {
}
