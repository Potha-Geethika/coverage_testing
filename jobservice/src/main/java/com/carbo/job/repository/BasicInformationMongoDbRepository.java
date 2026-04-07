package com.carbo.job.repository;

import com.carbo.job.model.proposal.BasicInformation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicInformationMongoDbRepository extends MongoRepository<BasicInformation, String> {

    BasicInformation findByProposalIdAndOrganizationId(String id, String organizationId);
}