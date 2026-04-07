package com.carbo.job.repository;

import com.carbo.job.model.PumpSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PumpScheduleMongoDbRepository extends MongoRepository<PumpSchedule, String> {
    List<PumpSchedule> findByOrganizationId(String organizationId);
    List<PumpSchedule> findByJobId(String jobId);
    List<PumpSchedule> findByJobIdAndWellId(String jobId, String wellId);

    List<PumpSchedule> findByOrganizationIdAndJobId(String organizationId, String jobId);
}
