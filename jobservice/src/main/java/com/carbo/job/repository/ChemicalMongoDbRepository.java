package com.carbo.job.repository;

import com.carbo.job.model.widget.misc.Chemical;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChemicalMongoDbRepository extends MongoRepository<Chemical, String> {
    List<Chemical> findByOrganizationId(String organizationId);

}
