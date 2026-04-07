package com.carbo.job.repository;

import com.carbo.job.model.ReleaseNotesResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseNotesMongoDbRepository extends MongoRepository<ReleaseNotesResponse,String> {

}
