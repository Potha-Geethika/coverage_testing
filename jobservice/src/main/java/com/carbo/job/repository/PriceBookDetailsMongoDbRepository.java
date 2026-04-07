package com.carbo.job.repository;

import com.carbo.job.model.widget.PriceBookComponents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceBookDetailsMongoDbRepository extends MongoRepository<PriceBookComponents, String> {
    List<PriceBookComponents> findByOrganizationIdAndPriceBookId(String organizationId, String priceBookId);
}
