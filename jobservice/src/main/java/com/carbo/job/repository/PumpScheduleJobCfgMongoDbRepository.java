package com.carbo.job.repository;

import com.carbo.job.model.PumpScheduleJobCfg;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PumpScheduleJobCfgMongoDbRepository extends MongoRepository<PumpScheduleJobCfg, String> {
    PumpScheduleJobCfg findByJobId(String jobId);
}
