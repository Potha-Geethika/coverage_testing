package com.carbo.job.repository;

import com.carbo.job.model.EmailType;
import com.carbo.job.model.EndStageEmail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EndStageEmailMongoDbRepository extends MongoRepository<EndStageEmail, String> {
    List<EndStageEmail> findByOrganizationIdAndJobIdAndTypeAndWellAndStage(String organizationId, String jobId, EmailType type, String well, String stage);
    List<EndStageEmail> findByOrganizationIdAndTypeAndWellAndStage(String organizationId, EmailType type, String well, String stage);

    List<EndStageEmail> findByTypeAndJobId(EmailType emailType, String jobId);
    List<EndStageEmail> findByOrganizationIdAndJobIdAndTypeAndWell(String organizationId, String jobId, EmailType type, String well);
    List<EndStageEmail> findByOrganizationIdAndTypeAndJobId(String organizationId, EmailType type, String jobId);
}
