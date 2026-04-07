package com.carbo.job.repository;

import com.carbo.job.model.ChemicalStageNoIndex;
import com.carbo.job.model.Job;
import com.carbo.job.model.ProppantStageNoIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JobProppantStageRepositoryImpl implements JobProppantStageRepository {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private static final String COLLECTION_NAME = "jobs";

    @Override
    public void updateInProgressProppantStage(String jobId, String wellId, ProppantStageNoIndex stageDto, String modifiedBy, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(
                ObjectId.isValid(jobId) ? new ObjectId(jobId) : jobId
        ));

        query.addCriteria(Criteria.where("wells._id").is(
                ObjectId.isValid(wellId) ? new ObjectId(wellId) : wellId
        ));
        query.addCriteria(Criteria.where("organizationId").is(organizationId));

        Update update = new Update();

        // Convert DTO to Map for MongoDB update
        @SuppressWarnings("unchecked")
        ProppantStageNoIndex stageMap = objectMapper.convertValue(stageDto, ProppantStageNoIndex.class);

        // Update the inProgressProppantStage for the specific well
        update.set("wells.$.inProgressProppantStage", stageMap);

        // Update job-level metadata
        update.set("modified", System.currentTimeMillis());
        update.set("lastModifiedBy", modifiedBy != null ? modifiedBy : "SYSTEM");
        update.set("ts", new Date().getTime());

        var result = mongoTemplate.updateFirst(query, update, COLLECTION_NAME);

        log.info("Updated inProgressProppantStage for job: {}, well: {}, matched: {}, modified: {}",
                jobId, wellId, result.getMatchedCount(), result.getModifiedCount());
    }

    @Override
    public boolean existsByJobId(String jobId) {
        Query query = new Query(Criteria.where("_id").is(jobId));
        return mongoTemplate.exists(query, COLLECTION_NAME);
    }

    @Override
    public Job getJobByIdAndOrganizationId(String jobId, String organizationId) {
        Query query = new Query(Criteria.where("_id").is(jobId).and("organizationId").is(organizationId));
        return mongoTemplate.findOne(query, Job.class, COLLECTION_NAME);
    }
}