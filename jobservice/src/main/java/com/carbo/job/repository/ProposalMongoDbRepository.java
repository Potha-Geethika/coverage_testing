package com.carbo.job.repository;

import com.carbo.job.model.proposal.Proposal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalMongoDbRepository extends MongoRepository<Proposal, String> {
    List<Proposal> findByJobId(String jobId);

    List<Proposal> findByJobIdAndWellId(String jobId, String wellId);
}
