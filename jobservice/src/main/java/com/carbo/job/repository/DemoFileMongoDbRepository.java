package com.carbo.job.repository;

import com.carbo.job.model.widget.DemoFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DemoFileMongoDbRepository  extends MongoRepository<DemoFile, String> {
}
