package com.carbo.job.repository;

import com.carbo.job.model.Pad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PadMongoDbRepository extends MongoRepository<Pad, String> {
    List<Pad> findByNameIn(List<String> padNames);

    List<Pad> findByOrganizationId(String organizationId);
    Optional<Pad> findDistinctByOrganizationIdAndName(String organizationId, String name);

    List<Pad> findByNameInAndOrganizationIdIn(Set<String> padNames, Set<String> organizationIds);
}
