package com.carbo.job.repository;

import com.carbo.ws.model.ProppantStage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProppantStageMongoDbRepository extends MongoRepository<ProppantStage, String> {
    Optional<ProppantStage> findByOrganizationIdAndJobIdAndWellAndStage(String organizationId, String jobId, String well, Float stage);

    List<ProppantStage> findByOrganizationIdAndJobId(String organizationId, String jobId);
}
