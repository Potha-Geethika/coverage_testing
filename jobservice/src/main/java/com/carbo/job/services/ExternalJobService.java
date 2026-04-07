package com.carbo.job.services;

import com.carbo.job.model.SimplifiedJob;
import com.carbo.job.repository.ExternalJobMongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalJobService {
    private final ExternalJobMongoDbRepository jobMongoDbRepository;

    @Autowired
    public ExternalJobService(ExternalJobMongoDbRepository jobRepository) {
        this.jobMongoDbRepository = jobRepository;
    }

    public List<SimplifiedJob> getByOrganizationIdExternal(String organizationId) {
        return jobMongoDbRepository.findByOrganizationIdExternal(organizationId);
    }

    public Optional<SimplifiedJob> getJob(String jobId) {
        return jobMongoDbRepository.findById(jobId);
    }

    public SimplifiedJob getByJobNumberAndOrganizationId(String jobNumber, String organizationId){
        return jobMongoDbRepository.findByJobNumberAndOrganizationId(jobNumber, organizationId);
    }

}
