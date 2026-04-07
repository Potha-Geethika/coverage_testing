package com.carbo.job.repository;

import com.carbo.job.model.well.WellInfos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellInfoMongoDbRepository extends MongoRepository<WellInfos, String> {
    List<WellInfos> findByWellId(String wellId);
}

