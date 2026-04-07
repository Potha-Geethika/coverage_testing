package com.carbo.job.repository;

import com.carbo.job.model.analytics.DailyJobRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DailyJobRecordMongoDbRepository extends MongoRepository<DailyJobRecord, String> {
    List<DailyJobRecord> findByOrganizationId(String organizationId);
    Page<DailyJobRecord> findByOrganizationId(String organizationId, Pageable pageable);
    Page<DailyJobRecord> findByJobIdIn(Set<String> jobId, Pageable pageable);

//    List<DailyJobRecord> findBySharedOrganizationId(String organizationId);
    Page<DailyJobRecord> findBySharedOrganizationId(String organizationId, Pageable pageable);

    List<DailyJobRecord> findByJobIdAndDate(String jobId, Date date);
    Optional<DailyJobRecord> findByJobId(String jobId);
}
