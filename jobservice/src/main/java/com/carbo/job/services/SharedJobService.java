package com.carbo.job.services;

import com.carbo.job.model.Job;
import com.carbo.job.repository.SharedJobMongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class SharedJobService {
    private final SharedJobMongoDbRepository sharedJobRepository;

    @Autowired
    public SharedJobService(SharedJobMongoDbRepository sharedJobRepository) {
        this.sharedJobRepository = sharedJobRepository;
    }

    public List<Job> findSharedToMe(String organizationId) {
        return sharedJobRepository.findBySharedWithOrganizationId(organizationId);
    }

    public Optional<Job> findById(String jobId) {
        return sharedJobRepository.findById(jobId);
    }
}

