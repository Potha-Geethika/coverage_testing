package com.carbo.job.repository;

import com.carbo.job.model.proposal.Well;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalWellMongoDbRepository extends MongoRepository<Well, String> {
    Well findByProposalIdAndOrganizationId(String id, String organizationId);
}
