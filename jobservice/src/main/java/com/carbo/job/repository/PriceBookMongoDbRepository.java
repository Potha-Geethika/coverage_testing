package com.carbo.job.repository;

import com.carbo.job.model.PriceBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceBookMongoDbRepository extends MongoRepository<PriceBook, String> {
    Optional<PriceBook> findByPriceBookNameAndOrganizationId(String priceBookName, String organizationId);
}

