package com.carbo.job.repository;

import com.carbo.job.model.Proppant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProppantMongoDbRepository extends MongoRepository<Proppant, String> {
    List<Proppant> findByOrganizationId(String organizationId);

    Optional<Proppant> findByOrganizationIdAndCode(String organizationId,String code);
}
