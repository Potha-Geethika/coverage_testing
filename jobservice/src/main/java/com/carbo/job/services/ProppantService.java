package com.carbo.job.services;

import com.carbo.job.model.Proppant;

import com.carbo.job.model.widget.PriceBookComponents;
import com.carbo.job.repository.ProppantMongoDbRepository;
import com.carbo.job.utils.Constants;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProppantService {
    private final ProppantMongoDbRepository proppantRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProppantService(ProppantMongoDbRepository proppantRepository, MongoTemplate mongoTemplate) {
        this.proppantRepository = proppantRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Proppant> getAll() {
        return proppantRepository.findAll();
    }

    public List<Proppant> getByOrganizationId(String organizationId) {
        return proppantRepository.findByOrganizationId(organizationId);
    }

    public PriceBookComponents getProppant(String proppantId, String organizationId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(new ObjectId(proppantId))
                        .and(Constants.ORGANIZATION_ID).is(organizationId))
        );
        PriceBookComponents priceBookComponents = mongoTemplate.aggregate(
                aggregation, "pricebook-components-v2", PriceBookComponents.class
        ).getUniqueMappedResult();

        return priceBookComponents;
    }

    public Proppant saveProppant(Proppant proppant) {
        return proppantRepository.save(proppant);
    }

    public void updateProppant(Proppant proppant) {
        proppantRepository.save(proppant);
    }

    public void deleteProppant(String proppantId) {
        proppantRepository.deleteById(proppantId);
    }

    public Optional<Proppant> getProppantByOrganizationIdAndCode(String organizationId,String code) {
        return proppantRepository.findByOrganizationIdAndCode(organizationId,code); }
}
