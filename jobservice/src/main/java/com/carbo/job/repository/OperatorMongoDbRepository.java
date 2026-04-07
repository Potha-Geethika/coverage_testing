package com.carbo.job.repository;

import com.carbo.job.model.Operator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OperatorMongoDbRepository extends MongoRepository<Operator, String> {
    Set<Operator> findByOrganizationId(String organizationId);

    @Query("{ 'linkedOrganizationId': { $exists: true, $ne: '' } }")
    List<Operator> findOperatorsWithLinkedOrganization();
}
