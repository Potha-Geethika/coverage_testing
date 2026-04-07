package com.carbo.job.repository;

import com.carbo.job.model.ChemicalStage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalStageMongoDbRepository extends MongoRepository<ChemicalStage, String> {
    Optional<ChemicalStage> findByOrganizationIdAndJobIdAndWellAndStage(String organizationId, String jobId, String well, Float stage);
    List<ChemicalStage> findByOrganizationIdAndJobId(String organizationId, String jobId);
    List<ChemicalStage> findByOrganizationIdAndJobIdAndWell(String organizationId, String jobId, String well);
}
