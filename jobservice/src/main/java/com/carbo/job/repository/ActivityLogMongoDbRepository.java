package com.carbo.job.repository;

import com.carbo.job.model.ActivityLogEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ActivityLogMongoDbRepository extends MongoRepository<ActivityLogEntry, String> {
    List<ActivityLogEntry> findByOrganizationId(String organizationId);
    List<ActivityLogEntry> findByOrganizationIdAndWellAndStage(String organizationId, String well, Float stage);
    List<ActivityLogEntry> findByOrganizationIdAndJobId(String organizationId, String jobId);
    List<ActivityLogEntry> findByJobIdAndDay(String jobId, Integer day);
    List<ActivityLogEntry> findByOrganizationIdAndModifiedBetween(String organizationId, Long start, Long end, Sort sort);
    List<ActivityLogEntry> findByJobIdAndModifiedBetween(String jobId, Long start, Long end);
    List<ActivityLogEntry> findByJobIdAndWellAndStageAndComplete(String jobId, String well, Float stage, Boolean complete);

    List<ActivityLogEntry> findByOrganizationIdAndJobIdAndDay(String organizationId, String jobId, int day);
    List<ActivityLogEntry> findByJobIdAndOpsActivity(String jobId,String opsActivity);

    List<ActivityLogEntry> findByCompleteAndJobIdIn(boolean b, List<String> jobIds);

    List<ActivityLogEntry> findByJobIdIn(List<String> jobIds);

    List<ActivityLogEntry> findByOrganizationIdAndJobIdIn(String organizationId, Set<String> jobIds);

    List<ActivityLogEntry> findByOrganizationIdAndJobIdAndWell(String organizationId, String jobId, String wellName);
    List<ActivityLogEntry> findByOrganizationIdAndJobIdAndWellIn(String organizationId, String jobId, Set<String> wellName);

}
