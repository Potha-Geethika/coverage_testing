package com.carbo.job.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.carbo.job.model.Well;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.carbo.job.model.well.Wells;

@Repository
public interface WellMongoDbRepository extends MongoRepository<Wells, String> {
    Optional<Wells> findByApi(String api);

    List<Well> findByOrganizationId(String organizationId);

    List<Well> findByOrganizationIdAndIdIn(String organizationId, List<String> wellIds);
    List<Well> findByIdIn(List<String> wellIds);

    Well findByOrganizationIdAndName(String organizationId, String well);

    Well findByOrganizationIdAndId(String organizationId, String wellId);

    List<Well> findByPadIdAndOrganizationId(String padId, String organizationId);
}



