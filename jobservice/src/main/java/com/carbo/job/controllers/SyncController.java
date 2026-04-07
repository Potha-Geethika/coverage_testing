package com.carbo.job.controllers;

import com.carbo.job.adapters.ProppantContainerAdapter;
import com.carbo.job.adapters.SetupContainerAdapter;
import com.carbo.job.model.*;
import com.carbo.job.repository.DistrictMongoDbRepository;
import com.carbo.job.services.ActivityLogService;
import com.carbo.job.services.FleetService;
import com.carbo.job.services.JobService;
import com.carbo.job.utils.Constants;
import com.carbo.proppantstage.model.ProppantContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.carbo.job.utils.ErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.carbo.job.utils.Constants.COMPLETED;
import static com.carbo.job.utils.ControllerUtil.*;

@RestController
@RequestMapping(value = "v1/sync")
public class SyncController {
    private static final Logger logger = LoggerFactory.getLogger(SyncController.class);
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ProppantContainer.class, new ProppantContainerAdapter())
            .registerTypeAdapter(SetupContainer.class, new SetupContainerAdapter())
            .create();

    private final JobService jobService;
    private final ActivityLogService activityLogService;

    private final FleetService fleetService;
    private final DistrictMongoDbRepository districtMongoDbRepository;
    private final MongoTemplate mongoTemplate;
    @Autowired
    public SyncController(JobService jobService, ActivityLogService activityLogService, FleetService fleetService,
                          DistrictMongoDbRepository districtMongoDbRepository, MongoTemplate mongoTemplate) {
        this.jobService = jobService;
        this.activityLogService = activityLogService;
        this.fleetService = fleetService;
        this.districtMongoDbRepository = districtMongoDbRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public Map<String, Long> view(HttpServletRequest request,
                                  @RequestParam(name = "status", required = false) String status) {
        Map<String, Long> result = new HashMap<>();
        String organizationId = getOrganizationId(request);
        List<String> districtId = getDistrictIds(request);
        List<Job> all;
        if (!ObjectUtils.isEmpty(districtId)){
            if (!StringUtils.isEmpty(status)) {
                all = jobService.getByOrganizationIdAndStatusAndDistrictIdIn(organizationId, status, districtId);
            }else{
                all = jobService.getByOrganizationIdAndDistrictIdIn(organizationId, districtId);
            }
        }
        else {
            List<District> districtList=districtMongoDbRepository.findByOrganizationId(organizationId);
            if (!ObjectUtils.isEmpty(districtList)){
                List<String> districtIds = districtList.stream()
                                                       .map(District::getId) // Map each District object to its ID
                                                       .collect(Collectors.toList());
                if (!StringUtils.isEmpty(status)) {
                    all = jobService.getByOrganizationIdAndStatusAndDistrictIdIn(organizationId, status, districtId);
                }else{
                    all = jobService.getByOrganizationIdAndDistrictIdIn(organizationId, districtId);
                }
            }else {
                    return result;
            }

        }
        long now = new Date().getTime() + 60000;
        all.forEach(each -> result.put(each.getId(), each.getTs() > now ? now : each.getTs()));
        return result;
    }

    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public SyncResponse<Job> sync(
            HttpServletRequest request,
            @RequestBody SyncRequest<Job> sync,
            @RequestParam(value = "module", required = false) String module) {
        SyncResponse response = new SyncResponse<Job>();

        List<Job> gets = new ArrayList<>();
        String organizationId = getOrganizationId(request);
        String userFullName = getUserFullName(request);

        if (sync.getUpdate() != null && !sync.getUpdate().isEmpty()) {
            Map<String, Long> updated = new HashMap<>();
            for (Job job : sync.getUpdate()) {
                if (job.getOrganizationId() != null) {
                    if (!job.getOrganizationId().equals(organizationId)) {
                        continue;
                    }
                }
                else {
                    job.setLastModifiedBy(userFullName);
                    job.setOrganizationId(organizationId);
                }
                if(job.getStatus().equals("Completed")) {
                    String fleetName = job.getFleet();
                    Optional<Fleet> fleet = fleetService.getByName(request,organizationId,fleetName);

                    String districtId;

                    if(ObjectUtils.isEmpty(fleet)){
                       districtId = job.getDistrictId();

                    } else {
                        districtId = fleet.get().getDistrictId();
                        String fleetType = fleet.get().getFleetType();
                        job.setFleetType(fleetType);
                    }

                    job.setDistrictWhenCompleted(districtId);
                }

                if (Constants.IN_PROGRESS.equals(job.getStatus()) && !ObjectUtils.isEmpty(job.getProposalId())) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("proposalId").is(job.getProposalId()).and(Constants.ORGANIZATION_ID).is(job.getOrganizationId()));
                    ProposalScheduler proposalScheduler =  mongoTemplate.findOne(query, ProposalScheduler.class);
                    if(!ObjectUtils.isEmpty(proposalScheduler)) {
                        Update update = new Update().set("districtId", job.getDistrictId());
                        mongoTemplate.updateFirst(query, update, ProposalScheduler.class);
                    }
                    else {
                        logger.warn("No ProposalScheduler found for proposalId: {} and organizationId: {}", job.getProposalId(), job.getOrganizationId());
                    }
                }

                if (job.getTs() > 0) {
                    // update
                    Job dbJob = this.jobService.getSimplifiedJob(job.getId());
                    validateNonNullableFields(job, dbJob);
                    validateModule(job,dbJob,module);
                    if (!new HashSet<>(dbJob.getUsers())
                            .equals(new HashSet<>(job.getUsers()))) {
                        job.setUsers(new ArrayList<>(dbJob.getUsers()));
                    }
                    List<Vendor> updatedVendors = job.getVendors();
                    for (Vendor updatedVendor : updatedVendors) {
                        if (updatedVendor.getPoNumberValues() != null) {
                            String[] arrOfStr = updatedVendor.getPoNumberValues().split(",");
                            Set<String> poNumbersSet = Arrays.stream(arrOfStr)
                                    .map(String::trim)
                                    .collect(Collectors.toSet());
                            if (poNumbersSet.size() != arrOfStr.length) {
                                throw new IllegalArgumentException("Duplicate poNumber found for Job: " +
                                        "Job Number: " + job.getJobNumber() +
                                        ", PoNumber: " + updatedVendor.getPoNumberValues());                            }
                                                    }

                    }
                    if (dbJob.getTs() > job.getTs()) {
                        Job fullJob = this.jobService.getJob(job.getId()).get();
                        // db object is newer than the version sent from the client
                        gets.add(fullJob);
                    } else {
                        job.updateModified();
                        job.setLastModifiedBy(userFullName);
                        List<ActivityLogEntry> entries = activityLogService.findByOrganizationIdAndJobId(request, organizationId, job.getId());
                        if (!entries.isEmpty() && COMPLETED.equals(job.getStatus())) {
                            // Get the latest activity log entry based on modified date
                            entries.stream()
                                    .max(Comparator.comparing(ActivityLogEntry::getModified))
                                    .ifPresent(latestEntry -> job.setEndDate(latestEntry.getModified()));

                        }
                            // Job number is updated
                        if (!job.getJobNumber().equals(dbJob.getJobNumber())) {
                                for (ActivityLogEntry each : entries) {
                                    each.updateModified();
                                    activityLogService.updateActivityLog(request, each);
                                }
                            }

                        long now = new Date().getTime();
                        if (job.getTs() > now) {
                            job.setTs(now);
                        }
                        if (!ObjectUtils.isEmpty(dbJob.getProposalId()) && ObjectUtils.isEmpty(job.getProposalId())) {
                            job.setProposalId(dbJob.getProposalId());
                        }
                        if(!Objects.equals(dbJob.getExpectedEndDate(),job.getExpectedEndDate()) || !Objects.equals(dbJob.getRts(),job.getRts())) {
                            jobService.saveJobInsights(job,request);
                        }
                        jobService.updateJobNumberInLatestInsights(job);
                        this.jobService.updateJob(job);
                        updated.put(job.getId(), job.getTs());
                    }
                }
                else {
                    // insert
                    validateNotNullValues(job);
                    Query priceBookQuery = new Query();
                    priceBookQuery.addCriteria(
                            Criteria.where("organizationId").is(job.getOrganizationId()).and("priceBookName").is("XOPS Default Pricebook"));
                    PriceBook priceBook = mongoTemplate.findOne(priceBookQuery, PriceBook.class);
                    if (!ObjectUtils.isEmpty(priceBook) && !ObjectUtils.isEmpty(priceBook.getId())) {
                       job.setPriceBookId(priceBook.getId());
                    }
                    job.setTs(System.currentTimeMillis());
                    Job saved = this.jobService.saveJob(job);
                    updated.put(saved.getId(), saved.getCreated());
                }
            }
            response.setUpdated(updated);
        }

        if (sync.getRemove() != null && !sync.getRemove().isEmpty()) {
            Set<String> removed = new HashSet<>();
            for (String id : sync.getRemove()) {
                //this.jobService.deleteJob(id);
                Optional<Job> optionalJob = this.jobService.getJob(id);
                if ( optionalJob.isPresent()) {
                    Job fullJob = optionalJob.get();
                    fullJob.setOrganizationId("deleted_" + dtf.format(LocalDateTime.now()));
                    fullJob.setBackupDate(new Date());
                    this.jobService.updateJob(fullJob);
                    removed.add(id);
                }
            }
            response.setRemoved(removed);
        }

        if (sync.getGet() != null && !sync.getGet().isEmpty()) {
            for (String id : sync.getGet()) {
                Optional<Job> obj = this.jobService.getJob(id);
                if (obj.isPresent()) {
                    gets.add(obj.get());
                }
            }

            if (!gets.isEmpty()) {
                response.setGet(gets);
            }
        }

        return response;
    }

    private void validateModule(Job job, Job dbJob, String module) {
        if(StringUtils.isEmpty(module) || !module.equals("vendors")) {
            if(!ObjectUtils.isEmpty(dbJob.getVendors())) {
                boolean hasValidPoNumber = dbJob.getVendors().stream()
                        .anyMatch(vendor -> !StringUtils.isEmpty(vendor.getPoNumberValues()));

                if (hasValidPoNumber) {
                    job.setVendors(dbJob.getVendors());
                }
            }
        }
    }

    private void backupJobBeforeUpdate(Job job) {
        try {
            Job deepCopy = gson.fromJson(gson.toJson(job), Job.class);
            deepCopy.setOrganizationId("backup" + dtf.format(LocalDateTime.now()));
            deepCopy.setId(null);
            deepCopy.setBackupDate(new Date());
            Job saved = this.jobService.saveJob(deepCopy);
        }
        catch (Exception ex) {
            logger.error("Ignore...");
            logger.error("Error when backing up job before modifying.", ex);
        }
    }

    private void validateNotNullValues(Job job) {
        if (!ObjectUtils.isEmpty(job)) {
            if (ObjectUtils.isEmpty(job.getPadEnergyType())) {
                throw new IllegalArgumentException(ErrorConstants.PAD_ENERGY_TYPE_CANNOT_BE_SET_TO_NULL);
            }
            if (ObjectUtils.isEmpty(job.getOperationsType())) {
                throw new IllegalArgumentException(ErrorConstants.OPERATIONS_TYPE_CANNOT_BE_SET_TO_NULL);
            }
        }
    }


    public void validateNonNullableFields(Job job, Job dbJob) {
        if (!ObjectUtils.isEmpty(dbJob)) {
            if (!ObjectUtils.isEmpty(dbJob.getWellheadCo()) && ObjectUtils.isEmpty(job.getWellheadCo())) {
                throw new IllegalArgumentException(ErrorConstants.WELLHEADCO_CANNOT_BE_SET_TO_NULL);
            }
            if (!ObjectUtils.isEmpty(dbJob.getWirelineCo()) && ObjectUtils.isEmpty(job.getWirelineCo())) {
                throw new IllegalArgumentException(ErrorConstants.WIRELINECO_CANNOT_BE_SET_TO_NULL);
            }
            if (!ObjectUtils.isEmpty(dbJob.getWaterTransferCo()) && ObjectUtils.isEmpty(job.getWaterTransferCo())) {
                throw new IllegalArgumentException(ErrorConstants.WATERTRANSFERCO_CANNOT_BE_SET_TO_NULL);
            }
            if (!ObjectUtils.isEmpty(dbJob.getPadEnergyType()) && ObjectUtils.isEmpty(job.getPadEnergyType())) {
                throw new IllegalArgumentException(ErrorConstants.PAD_ENERGY_TYPE_CANNOT_BE_SET_TO_NULL);
            }
            if (!ObjectUtils.isEmpty(dbJob.getOperationsType()) && ObjectUtils.isEmpty(job.getOperationsType())) {
                throw new IllegalArgumentException(ErrorConstants.OPERATIONS_TYPE_CANNOT_BE_SET_TO_NULL);
            }
        }
    }
}
