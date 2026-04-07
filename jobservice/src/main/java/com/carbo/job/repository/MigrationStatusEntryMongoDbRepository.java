package com.carbo.job.repository;

import com.carbo.job.model.MigrationStatusEntry;
import com.carbo.job.model.MigrationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MigrationStatusEntryMongoDbRepository extends MongoRepository<MigrationStatusEntry, String> {
    Optional<MigrationStatusEntry> findByJobIdAndMigrationType(String jobId, MigrationType migrationType);
}
