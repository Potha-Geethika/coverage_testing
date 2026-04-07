package com.carbo.job.repository;

import com.carbo.job.model.District;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictMongoDbRepository extends MongoRepository<District, String> {
    List<District> findByOrganizationId(String organizationId);
}
