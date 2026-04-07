package com.carbo.job.repository;

import com.carbo.job.model.GeneralSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralSettingMongoDbRepository extends MongoRepository<GeneralSetting, String> {
    List<GeneralSetting> findByOrganizationId(String organizationId);
}
