package com.carbo.job.repository;

import com.carbo.job.model.ServiceAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAccountMongoDbRepository extends MongoRepository<ServiceAccount, String> {
    List<ServiceAccount> findByOrganizationId(String organizationId);
}
