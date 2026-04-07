// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.PutMapping
// Unresolved import (framework/JDK): org.springframework.beans.factory.annotation.Autowired
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RequestParam
// ===== Imported from: com.carbo.job.model.JobStatus =====
package com.carbo.job.model;

public enum JobStatus {
    In_Progress,
    Completed
}
// Unresolved import (framework/JDK): org.springframework.http.ResponseEntity
// ===== Imported from: com.carbo.job.services.OrganizationService =====
package com.carbo.job.services;

import com.carbo.job.model.JobStatus;
import com.carbo.job.model.Organization;
import com.carbo.job.repository.OrganizationMongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
"This code is not in use and comments have been added by Jyotish Kumar, Walkingtree on 2022-11-28 as part of JobService Stabilization activity."

*/
@Service
public class OrganizationService {
    private final OrganizationMongoDbRepository organizationRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public OrganizationService(OrganizationMongoDbRepository organizationRepository, MongoTemplate mongoTemplate) {
        this.organizationRepository = organizationRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

    public Optional<Organization> get(String organizationId) {
        return organizationRepository.findById(organizationId);
    }

    public void updateData(String tableName, String currentOrganizationId, String newOrganizationId, JobStatus jobStatus, boolean isCopyRecord) {
        try {
            Query query = new Query(Criteria.where("organizationId").is(currentOrganizationId));
            if (isCopyRecord) {
                List<Map> recordsToCopy = mongoTemplate.find(query, Map.class, tableName);

                List<Map> newRecords = new ArrayList<>();
                for (Map record : recordsToCopy) {
                    Map newRecord = new HashMap<>(record);
                    newRecord.put("organizationId", newOrganizationId);

                    if (tableName.equals("jobs") && (!ObjectUtils.isEmpty(jobStatus))) {
                        String jobStatusStr = jobStatus + "";
                        if (jobStatusStr.contains("_")) {
                            jobStatusStr = jobStatusStr.replace("_", " ");
                        }
                        newRecord.put("status", jobStatusStr);
                    }
                    newRecord.remove("_id");
                    newRecords.add(newRecord);
                }
                mongoTemplate.insert(newRecords, tableName);
            }
            else {
                if (tableName.equals("jobs") && (!ObjectUtils.isEmpty(jobStatus))) {
                    String jobStatusStr = jobStatus + "";
                    if (jobStatusStr.contains("_")) {
                        jobStatusStr = jobStatusStr.replace("_", " ");
                    }
                    query.addCriteria(Criteria.where("status").is(jobStatusStr));
                }
                Update update = new Update().set("organizationId", newOrganizationId);
                mongoTemplate.updateMulti(query, update, tableName);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while copying and inserting new data.");
        }
    }
}



// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RequestMapping
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RestController
// ===== Current file: src/main/java/com/carbo/job/controllers/OrganizationController.java =====
package com.carbo.job.controllers;
import static com.carbo.job.utils.Constants.DATA_UPDATED_SUCCESSFULLY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.carbo.job.model.JobStatus;
import com.carbo.job.services.OrganizationService;

@RestController
@RequestMapping (value = "v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PutMapping ("/update-data")
    public ResponseEntity updateData(@RequestParam String tableName, @RequestParam String currentOrganizationId, @RequestParam String newOrganizationId,
            @RequestParam(required = false) JobStatus jobStatus, @RequestParam (required = false) boolean isCopyRecord) {

        organizationService.updateData(tableName,currentOrganizationId,newOrganizationId,jobStatus,isCopyRecord);
        return ResponseEntity.ok(DATA_UPDATED_SUCCESSFULLY);
    }
}

