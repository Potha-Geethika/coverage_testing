package com.carbo.job.repository;

import com.carbo.job.model.SimplifiedJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalJobMongoDbRepository extends MongoRepository<SimplifiedJob, String> {
    @Query(value = "{ 'backupDate' : null, 'backupDate': { $exists: false }, organizationId: ?0 }", fields = "{'id': 1, 'ts': 1, 'jobNumber': 1, 'operator': 1, 'pad': 1, 'fleet': 1, " +
            "'startDate': 1, 'targetStagesPerDay' : 1, 'targetDailyPumpTime': 1, 'wells': 1, 'rts': 1}")
    List<SimplifiedJob> findByOrganizationIdExternal(String organizationId);

    SimplifiedJob findByJobNumberAndOrganizationId(String jobId, String organizationId);
}
