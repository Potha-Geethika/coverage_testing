package com.carbo.job.repository;

import com.carbo.job.model.Fleet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FleetMongoDbRepository extends MongoRepository<Fleet, String> {
    List<Fleet> findByOrganizationId(String organizationId);
    Optional<Fleet> findDistinctByOrganizationIdAndName(String organizationId, String name);
    Optional<Fleet> findDistinctById(String id);
    Fleet findByOrganizationIdAndName(String organizationId, String name);
}
