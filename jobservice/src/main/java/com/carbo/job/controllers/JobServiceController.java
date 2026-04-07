package com.carbo.job.controllers;

import com.azure.core.exception.ResourceNotFoundException;
import com.carbo.job.model.*;
import com.carbo.job.model.simplified.Material;
import com.carbo.job.model.simplified.Stage;
import com.carbo.job.model.well.StageInfo;
import com.carbo.job.model.well.Wells;
import com.carbo.job.repository.DistrictMongoDbRepository;
import com.carbo.job.repository.FleetMongoDbRepository;
import com.carbo.job.repository.WellMongoDbRepository;
import com.carbo.job.services.*;
import com.carbo.job.utils.Constants;
import com.carbo.proppantstage.model.ProppantContainer;
import com.carbo.ws.model.ProppantStage;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.carbo.job.utils.ControllerUtil.*;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping(value = "v1/jobs")
public class JobServiceController {
    private static final Logger logger = LoggerFactory.getLogger(JobServiceController.class);

    private final JobService jobService;
    private final FleetService fleetService;
    private final ActivityLogService activityLogService;
     private final FleetMongoDbRepository fleetMongoDbRepository;

    private final ProppantStageService proppantStageService;

    private final ChemicalStageService chemicalStageService;

    private final EmailService emailService;

    private final SharedJobService sharedJobService;
    private final WellService wellService;
    private final DistrictMongoDbRepository districtMongoDbRepository;
    private final JobChemicalStageService jobChemicalStageService;
    private final JobProppantStageService jobProppantStageService;

    private final MongoTemplate mongoTemplate;
    private final WellMongoDbRepository wellMongoDbRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public JobServiceController(JobService jobService, ActivityLogService activityLogService, FleetMongoDbRepository fleetMongoDbRepository, ProppantStageService proppantStageService,
                                ChemicalStageService chemicalStageService, EmailService emailService, SharedJobService sharedJobService, FleetService fleetService,
                                WellService wellService, DistrictMongoDbRepository districtMongoDbRepository, MongoTemplate mongoTemplate,
                                WellMongoDbRepository wellMongoDbRepository, ModelMapper modelMapper, JobChemicalStageService jobChemicalStageService,
                                JobProppantStageService jobProppantStageService) {
        this.jobService = jobService;
        this.activityLogService = activityLogService;
        this.fleetMongoDbRepository = fleetMongoDbRepository;
        this.proppantStageService = proppantStageService;
        this.chemicalStageService = chemicalStageService;
        this.emailService = emailService;
        this.sharedJobService = sharedJobService;
        this.fleetService = fleetService;
        this.wellService = wellService;
        this.districtMongoDbRepository = districtMongoDbRepository;
        this.jobChemicalStageService = jobChemicalStageService;
        this.jobProppantStageService = jobProppantStageService;
        this.mongoTemplate = mongoTemplate;
        this.wellMongoDbRepository = wellMongoDbRepository;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Job> getJobs(HttpServletRequest request,
                             @RequestParam(name = "wellAPI", required = false) String wellAPI,
                             @RequestParam(name = "stage", required = false) Float stage,
                             @RequestParam(name = "districtIds", required = false, defaultValue = "") List<String> districtIds,
                             @RequestParam(required = false, defaultValue = "0") int offset,
                             @RequestParam(required = false, defaultValue = "" + Constants.DEFAULT_LIMIT) int limit,
                             @RequestParam(name = "status", required = false) String status) {
        String organizationId = getOrganizationId(request);
        String organizationType = getOrganizationType(request);
        String organization = getOrganization(request);
        String userName = getUserFullName(request);
        logger.debug("Getting all jobs for " + organization + "/" + userName);
        List<String> districtId = getDistrictIds(request);
        Query query;
        if(ObjectUtils.isEmpty(districtId)){
           return Collections.emptyList();
        }
        List<Job> all;
        if(organizationType.contentEquals("OPERATOR")){
            query =new Query();
            if (!StringUtils.isEmpty(status)) {
                query.addCriteria(Criteria.where("sharedWithOrganizationId").is(organizationId).and("status").is(status));
            }else {
                query.addCriteria(Criteria.where("sharedWithOrganizationId").is(organizationId));
            }
            query.skip(offset * limit).limit(limit);
            all = mongoTemplate.find(query, Job.class,"jobs");
        } else {
            query =new Query();
            if (!StringUtils.isEmpty(status)) {
                query.addCriteria(Criteria.where("organizationId").is(organizationId).and("status").is(status).and("districtId").in(districtId));
            }else {
                query.addCriteria(Criteria.where("organizationId").is(organizationId).and("districtId").in(districtId));
            }
            query.skip(offset * limit).limit(limit);
            all = mongoTemplate.find(query,Job.class,"jobs");
        }

        if (!all.isEmpty() && wellAPI != null) {
            Optional<Job> job = filterByWellAPI(all, wellAPI);

            if (job.isPresent() && stage != null) {
                job = Optional.of(filterByStage(request, job.get(), stage));
            }

            all = job.isPresent() ? Collections.singletonList(job.get()) : Collections.emptyList();
        }
        List<Job> filteredJobsBasisOnDistrictIds = new ArrayList<>();
        if (!districtIds.isEmpty() && districtIds.size() != 0) {
            for (String distId : districtIds) {
                all.forEach(j -> {
                    if (distId.equals(j.getDistrictId())){
                        filteredJobsBasisOnDistrictIds.add(j);
                    }
                });
            }
            return filteredJobsBasisOnDistrictIds;
        }
        return all;
    }


    @RequestMapping(value = "/shared", method = RequestMethod.GET)
    public List<Job> getSharedJobs(HttpServletRequest request) {
        String organizationId = getOrganizationId(request);
        List<Job> all = sharedJobService.findSharedToMe(organizationId);
        return all;
    }


    @RequestMapping(value = "/sharedById/{jobId}", method = RequestMethod.GET)
    public Job getSharedJobById(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        String organizationId = getOrganizationId(request);
        Job result = null;
        Optional<Job> fromDBOptional = sharedJobService.findById(jobId);
        if (fromDBOptional.isPresent()) {
            Job fromDB = fromDBOptional.get();
            if (fromDB.getSharedWithOrganizationId().equals(organizationId)) {
                result = fromDB;
            }
        }

        return result;
    }


    @RequestMapping(value = "/simplified/", method = RequestMethod.GET)
    public Stage getSimplified(HttpServletRequest request,
                               @RequestParam(name = "wellAPI", required = true) String wellAPI,
                               @RequestParam(name = "stage", required = true) Float stage) {
        logger.info("<------------- Invoked getSimplified ------------->");
        logger.debug("Parameters received - wellAPI: {}, stage: {}", wellAPI, stage);

        String organizationId = getOrganizationId(request);
        Optional<String> districtId = getDistrictId(request);
        Stage ret = new Stage();
        List<Job> jobs;

        try {
            if (districtId.isPresent() && !districtId.get().isEmpty()) {
                jobs = jobService.getByOrganizationIdAndDistrictId(request, organizationId, districtId.get());
                logger.debug("Jobs fetched by organizationId and districtId: {}", jobs);
            } else {
                jobs = jobService.getByOrganizationIdAndWellApi(organizationId, wellAPI);
                logger.debug("Jobs fetched by organizationId and wellAPI: {}", jobs);
            }

            if (!jobs.isEmpty() && wellAPI != null) {
                Optional<Job> job = filterByWellAPI(jobs, wellAPI);

                if (job.isPresent() && stage != null) {
                    logger.debug("Job found for wellAPI: {} and stage: {}", wellAPI, stage);
                    job = Optional.of(filterByStage(request, job.get(), stage));
                }

                if (stage != null && wellAPI != null) {
                    wellService.getByWellAPI(request, wellAPI)
                            .filter(wells -> wells.getApi().equals(wellAPI))
                            .ifPresent(wells -> {
                                emailService.findByOrganizationIdAndOptionalJobIdAndTypeAndWellNameOrWellIdAndStage(organizationId, null, EmailType.END_STAGE, wells.getName(), wells.getId(), stage)
                                        .ifPresent(email -> {
                                            logger.debug("Email data fetched for wellName: {} and stage: {}", wells.getName(), stage);
                                            ret.setDiesel(email.getDiesel());
                                            ret.setFieldGas(email.getFieldGas());
                                            ret.setCng(email.getCng());
                                        });
                            });
                }

                if (job.isPresent()) {
                    Job found = job.get();
                    logger.debug("Processing job: {}", found);
                    ret.setJobNumber(found.getJobNumber());

                    for (Well well : found.getWells()) {
                        PadInfo padInfo = getPadStageSummary(request, organizationId, found.getId(), districtId, well.getName(), stage);
                        ret.setPadStageTotal(padInfo.getPadStageTotal());
                        ret.setPadStageCompleted(padInfo.getPadStageCompleted());

                        Optional<EndStageEmail> email = emailService.findByOrganizationIdAndOptionalJobIdAndTypeAndWellNameOrWellIdAndStage(
                                organizationId, found.getId(), EmailType.END_STAGE, well.getName(), well.getId(), stage);

                        if (email.isPresent()) {
                            EndStageEmail endStageEmail = email.get();
                            logger.debug("EndStageEmail fetched for wellName: {} and stage: {}", well.getName(), stage);
                            ret.setPumpStart(endStageEmail.getPumpStart());
                            ret.setPumpEnd(endStageEmail.getPumpEnd());
                        }

                        if (well.getTmpMigratedChemicalStage() !=null) {
                            ChemicalStage submittedStage = well.getTmpMigratedChemicalStage();
                            ret.setCleanTotal(submittedStage.getCleanTotal());
                            logger.debug("Processing chemical stage for well: {}", well.getName());

                            List<Strap> straps = submittedStage.getChemicalAdditionUnit1();
                            addStrapsToReturnedStage(ret, straps);

                            straps = submittedStage.getChemicalAdditionUnit2();
                            addStrapsToReturnedStage(ret, straps);

                            straps = submittedStage.getDryAdd();
                            addStrapsToReturnedStage(ret, straps);

                            straps = submittedStage.getIsosTransport();
                            addStrapsToReturnedStage(ret, straps);
                        }

                        if (well.getTmpMigratedProppantStage() != null) {
                            ProppantStage submittedStage = well.getTmpMigratedProppantStage();
                            List<ProppantContainer> containers = submittedStage.getAllSubmittedContainer(found.getProppantSchematicType().equals("boxes"));

                            for (ProppantContainer container : containers) {
                                if (container.getActualRun() != null && container.getActualRun() > 0) {
                                    Optional<Proppant> proppantOpt = well.getProppants().stream().filter(each -> each.getId().equals(container.getProppant().getId())).findFirst();
                                Proppant proppant = new Proppant();
                                if(proppantOpt.isPresent()){
                                    proppant = proppantOpt.get();
                                }
                                    ret.addProppant(new Material(proppant.getName(), proppant.getVolumePerStage(), container.getActualRun(), proppant.getUom()));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in getSimplified. Error message: {}", e.getMessage(), e);
            throw e;
        }

        return ret;
    }


    private void addStrapsToReturnedStage(Stage ret, List<Strap> straps) {
        for (Strap strap : straps) {
            Chemical chemical = strap.getChemical();
            if (chemical.getSubtype().equals("Acid")) {
                Material material = new Material(strap.getName(), chemical.volumePerStage, strap.getUsed(), chemical.getUom());
                ret.addAcid(material);
            }
            else {
                ret.addChemical(new Material(chemical.getName(), chemical.volumePerStage, strap.getRawUsed(), chemical.getUom()));
            }
        }
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    public Object getJob(@PathVariable("jobId") String jobId) {
        logger.debug("Looking up data for job {}", jobId);
        Optional<Job> job = jobService.getJob(jobId);
        if (job.isPresent()) {

            if(ObjectUtils.isEmpty(job.get().getFleetType())) {
                Fleet fleet = fleetMongoDbRepository.findByOrganizationIdAndName(
                        job.get().getOrganizationId(),
                        job.get().getFleet()
                );

                job.get().setFleetType(fleet != null ? fleet.getFleetType() : null);
            }
            int count = jobService.countDualFuelPumps(job.get());
            List<OnSiteEquipment> allPump = job.get().getPumps();
            for(OnSiteEquipment singlePump : allPump){
                if(singlePump.getType().equalsIgnoreCase("Pumps") && singlePump.getEku()){
                    count++;
                }
            }
            job.get().setDualFuelPumpCount(count);
            return job.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.PUT)
    public Job updateJob(@PathVariable("jobId") String jobId, @RequestBody Job job) {
        return jobService.updateJob(job);
    }

    @RequestMapping(value = "/{jobId}/start-date", method = RequestMethod.PUT)
    public ResponseEntity<Object> setStartDate(@PathVariable("jobId") String jobId, @RequestBody Job job) {
        if (!jobId.equals(job.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Job ID did not match");
        }

        Job dbJob = jobService.getJob(jobId).get();
        dbJob.setStartDate(job.getStartDate());
        dbJob.setStartDateModified(job.getStartDateModified());
        if (job.getTimezone()!=null)
            dbJob.setTimezone(job.getTimezone());
        if (job.getStartDateStr()!=null)
            dbJob.setStartDateStr(job.getStartDateStr());
        jobService.updateJob(dbJob);
        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Job saveJob(@RequestBody Job job) {
        return jobService.saveJob(job);
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable("jobId") String jobId) {
        jobService.deleteJob(jobId);
    }

    @RequestMapping(value = "/{jobId}/{wellName}/{stage}/summary", method = RequestMethod.GET)
    public JobSummary getSummary(HttpServletRequest request,
                                 @PathVariable("jobId") String jobId,
                                 @PathVariable("wellName") String wellName,
                                 @PathVariable("stage") Float stage) {
        logger.debug("Entering getSummary with jobId: {}, wellName: {}, stage: {}", jobId, wellName, stage);
        JobSummary ret;
        try {
            String organizationId = getOrganizationId(request);
            logger.debug("Retrieved organizationId: {}", organizationId);

            Optional<String> districtId = getDistrictId(request);
            logger.debug("Retrieved districtId: {}", districtId);

            PadInfo padInfo = getPadStageSummary(request, organizationId, jobId, districtId, wellName, stage);
            logger.debug("Retrieved PadInfo: {}", padInfo);

            ret = new JobSummary(padInfo);
            logger.debug("Created JobSummary: {}", ret);
        } catch (Exception e) {
            logger.error("Exception occurred in getSummary with jobId: {}, wellName: {}, stage: {}",
                    jobId, wellName, stage, e);
            throw e;
        }
        logger.debug("Exiting getSummary with result: {}", ret);
        return ret;
    }

    /**
     * Find the job that contain the well matched wellAPI
     * @param jobs
     * @param wellAPI
     * @return
     */
    public Optional<Job> filterByWellAPI(List<Job> jobs, String wellAPI) {
        Optional<Job> ret = jobs
                .stream()
                .filter(job -> job
                        .getWells()
                        .stream()
                        .map(well -> well.getApi())
                        .filter(api -> api.equals(wellAPI))
                        .count() > 0).findFirst();
        if (ret.isPresent()) {
            ret.get().getWells().removeIf(well -> !well.getApi().equals(wellAPI));
        }
        return ret;
    }

    public Job filterByStage(HttpServletRequest request,Job job, Float stage) {
        Job ret = job;

        job.getWells().forEach(well -> {

            Optional<ProppantStage> proppantStage = this.proppantStageService.findByOrganizationIdAndJobIdAndWellAndStage(request, job.getOrganizationId(), job.getId(), well.getName(), stage);
            proppantStage.ifPresent(well::setTmpMigratedProppantStages);

            Optional<ChemicalStage> chemicalStage = this.chemicalStageService.findByOrganizationIdAndJobIdAndWellAndStage(request, job.getOrganizationId(), job.getId(), well.getName(), stage);
            chemicalStage.ifPresent(well::setTmpMigratedChemicalStage);
        });

        return ret;
    }

    private PadInfo getPadStageSummary(HttpServletRequest request, String organizationId, String jobId, Optional<String> districtId, String wellName, Float stage) {
        PadInfo ret = new PadInfo();
        int count = 0;

        try {
            logger.debug("Fetching activity logs for organizationId: {}, jobId: {}", organizationId, jobId);
            List<ActivityLogEntry> entries = activityLogService.findByOrganizationIdAndJobId(request, organizationId, jobId);

            // Compare by first name and then last name
            Comparator<ActivityLogEntry> compareByDayAndStart = Comparator
                    .comparing(ActivityLogEntry::getDay)
                    .thenComparing(ActivityLogEntry::getStart);

            logger.debug("Filtering and sorting activity logs");
            List<ActivityLogEntry> sorted = entries.stream()
                    .filter(each -> each.getComplete() != null && each.getComplete())
                    .sorted(compareByDayAndStart)
                    .collect(Collectors.toList());

            for (ActivityLogEntry each : sorted) {
                if (each.getComplete() && each.getStage() >= 1) {
                    ret.increaseCompletedCount();
                    if (each.getWell().equals(wellName)) {
                        ret.addCompleteStage(each.getStage());
                        if (each.getStage().equals(stage)) {
                            break;
                        }
                    }
                }
            }

            logger.debug("Fetching job information for organizationId: {}, jobId: {}", organizationId, jobId);
            Optional<Job> job;
            if (districtId.isPresent() && !districtId.get().isEmpty()) {
                logger.debug("Fetching job with districtId: {}", districtId.get());
                job = jobService.getByOrganizationIdAndJobIdAndDistrictId(request, organizationId, jobId, districtId.get());
            } else {
                logger.debug("Fetching job without districtId");
                job = jobService.getByOrganizationIdAndJobId(organizationId, jobId);
            }

            if (job.isPresent()) {
                logger.debug("Fetching wells for jobId: {}", job.get().getId());
                List<Well> wells = job.get().getWells();
                Set<String> wellIds = wells.stream().map(Well::getId).collect(Collectors.toSet());
                List<Wells> wellList = (List<Wells>) wellMongoDbRepository.findAllById(wellIds);

                Map<String, Wells> wellMap = wellList.stream()
                        .collect(Collectors.toMap(Wells::getId, well -> well));

                for (Well well : wells) {
                    Wells well1 = wellMap.get(well.getId());
                    if (well1 != null) {
                        for (StageInfo stageInfo : well1.getStageInfo()) {
                            if (stageInfo.getJobs().contains(job.get().getId())) {
                                count++;
                            }
                        }
                        ret.setJobNumber(job.get().getJobNumber());
                        ret.setPadStageTotal(job.get().getPadStageTotal() - count);
                    }
                }
            } else {
                logger.warn("Job not found for organizationId: {}, jobId: {}", organizationId, jobId);
            }
        } catch (Exception e) {
            logger.error("An error occurred in getPadStageSummary: {}", e.getMessage(), e);
            throw e;
        }

        return ret;
    }

    @RequestMapping(value = "/assignDistrictToJobs", method = RequestMethod.PUT)
    public void assignDistrictToJobs(HttpServletRequest request) {
        jobService.assignDistrictToJobs(request);
    }



    @RequestMapping(value = "/jobPanel", method = RequestMethod.GET)
    public ResponseEntity getJobPanelDesc(HttpServletRequest request) {
        return jobService.jobPanelDesc(request);


    }

    @GetMapping("/dashboardTopWidgetCardData/")
    public ResponseEntity getTopWidgetCardData(HttpServletRequest request){
        return jobService.getTopWidgetsData(request);
    }

    @GetMapping ("/getJobsByCompany")
    public ResponseEntity getJobsByCompany(HttpServletRequest request, String operator, @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return jobService.getJobsByCompany(request, operator, page, size);
    }

    @PutMapping("/updateWell")
    public void updateWell(HttpServletRequest request, @RequestBody Well well){
        jobService.updateWell(request,well);
    }
    @PutMapping("/updateVendors")
    public void updateVendors(HttpServletRequest request, @RequestBody Vendor vendor, @RequestParam String oldVendorName, @RequestParam String vendorType) {
        jobService.updateVendors(request, vendor, oldVendorName, vendorType);
    }
    @RequestMapping(value = "/saveReleaseNotes", method = RequestMethod.POST)
    public ResponseEntity saveReleaseNotes(HttpServletRequest request, @RequestBody ReleaseNotesResponse releaseNotesResponse) {
        return jobService.saveReleaseNotes(request, releaseNotesResponse);
    }

    @RequestMapping(value = "/getReleaseNotes", method = RequestMethod.GET)
    public ResponseEntity getReleaseNotes(HttpServletRequest request) {
        return jobService.getReleaseNotes(request);
    }

    @RequestMapping(value = "/updateReleaseNotes", method = RequestMethod.PUT)
    public ResponseEntity updateReleaseNotes(HttpServletRequest request, @RequestBody ReleaseNotesResponse releaseNotesResponse) {
        return jobService.updateReleaseNotes(request, releaseNotesResponse);
    }

    @RequestMapping(value = "/deleteReleaseNotes", method = RequestMethod.DELETE)
    public void deleteReleaseNotes(@RequestParam String id) {
        jobService.deleteReleaseNotes(id);
    }

    @RequestMapping(value = "/saveJobItemCodeDiscounts", method = RequestMethod.POST)
    public JobItemCodeDiscounts saveJobItemCodeDiscounts(HttpServletRequest request, @RequestBody JobItemCodeDiscountsRequest jobItemCodeDiscountsRequest) {
        return jobService.saveJobItemCodeDiscounts(request, jobItemCodeDiscountsRequest);
    }

    @RequestMapping(value = "/getJobItemCodeDiscounts", method = RequestMethod.GET)
    public JobItemCodeDiscounts getJobItemCodeDiscounts(HttpServletRequest request, @RequestParam String jobId) {
        return jobService.getJobItemCodeDiscounts(request, jobId);
    }

    @RequestMapping(value = "/migrateDiscounts", method = RequestMethod.GET)
    public ResponseEntity migrateDiscounts(HttpServletRequest request, @RequestParam(required = false) String jobId) {
        return jobService.migrateDiscounts(request, jobId);
    }

    @RequestMapping(value = "/addIdBucketTest", method = RequestMethod.GET)
    public ResponseEntity addIdBucketTest(HttpServletRequest request, @RequestParam(required = false) String jobId) {
        return jobService.addIdBucketTest(request, jobId);
    }

    @RequestMapping(value = "/migrateBucketTest", method = RequestMethod.GET)
    public ResponseEntity migrateBucketTest(HttpServletRequest request, @RequestParam(required = false) String jobId) {
        return jobService.migrateBucketTest(request, jobId);
    }

    @RequestMapping(value = "/getBucketTests", method = RequestMethod.GET)
    public ResponseEntity getBucketTests(HttpServletRequest request, @RequestParam(required = true) String jobId) {
        BucketTest bucketTest = jobService.getBucketTests(request, jobId);
        return ResponseEntity.ok(bucketTest);
    }

    @RequestMapping(value = "/updateBucketTests/", method = RequestMethod.PUT)
    public ResponseEntity updateBucketTests(HttpServletRequest request, @RequestParam(required = true) BucketOperationEnum operation, @RequestParam(required = true) String jobId, @RequestParam(required = false) String testItemId, @RequestBody(required = false) BucketTestItem bucketTestItem) {
        BucketTest bucketTest = jobService.updateBucketTests(request, operation, jobId, testItemId, bucketTestItem);
        return ResponseEntity.ok(bucketTest);
    }
    @RequestMapping(value = "/populateBucketTest", method = RequestMethod.PUT)
    public ResponseEntity populateBucketTest(HttpServletRequest request, @RequestParam String jobId, @RequestBody BucketTest bucketTest) {
        BucketTest dbBucketTest = jobService.populateBucketTest(request, jobId, bucketTest);
        return ResponseEntity.ok(dbBucketTest);
    }

    @RequestMapping(value = "/doesJobExists", method = RequestMethod.GET)
    public ResponseEntity<Boolean> getSharedJobs(HttpServletRequest request, @RequestParam(required = true) String jobNumber) {
        String organizationId = getOrganizationId(request);
        return new ResponseEntity<>(jobService.existsByOrganizationIdAndJobNumber(organizationId,jobNumber),HttpStatus.OK);
    }

    @RequestMapping(value = "/personalJob", method = RequestMethod.POST)
    public ResponseEntity personalJob(HttpServletRequest request, @RequestBody(required = false) PersonalJob personalJob,@RequestParam(required = false) boolean isUpdate) {
        String organizationId = getOrganizationId(request);
        return jobService.personalJob(organizationId,personalJob,isUpdate);
    }
    /**
     /shareJobsWithOperator, share or unShare the sharing of jobs between organizations via an operator.
     It updates the sharedWithOrganizationId field for jobs based on the parameters:
     sharedWithOrganizationIdToLink (organization ID), curOrganizationId (current organization ID), operatorName (name of the operator), and isShared (sharing flag).
     The updated jobs are returned.
     */

    @PutMapping("/shareJobsWithOperator")
    public ResponseEntity<List<Job>> shareJobsWithOperator(HttpServletRequest request,
                                                           @RequestParam(required = true) String sharedWithOrganizationIdToLink,
                                                           @RequestParam(required = true) String operatorName,
                                                           @RequestParam(required = true) boolean isShared){
        String organizationId = getOrganizationId(request);
        return ResponseEntity.status(HttpStatus.OK).body(jobService.updateSharedWithOrganizationId(sharedWithOrganizationIdToLink, organizationId,operatorName,isShared));
    }
    @RequestMapping(value = "/updateSharedWithOrganizationIdInAllJobs", method = RequestMethod.POST)
    public ResponseEntity<Map<String, List<String>>> updateAllJob() {
        return jobService.updateSharedWithOrganizationIdInAllJobs();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCalendarJob(@RequestBody JobUpdateRequest request,
                                               HttpServletRequest requestInfo) {
        try {
            Job updatedJob = jobService.updateCalendarJob(request,requestInfo);
            return ResponseEntity.ok(updatedJob); // return updated job
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", ex.getMessage()));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getMessage()));
        } catch (Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update job");
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("jobInsights")
    public Map<String, List<JobInsightsDTO>>  getJobInsights(HttpServletRequest request,
                                            @RequestBody(required = false) GlobalFilterCalendar globalFilterCalendar) {
      if (globalFilterCalendar == null) {
         globalFilterCalendar = new GlobalFilterCalendar();
        }
       return jobService.getAllJobInsights(request,globalFilterCalendar);
    }

    @PostMapping("checkJobConflict")
    public ResponseEntity<?>  getJobInsights(HttpServletRequest request,
            @RequestParam(required = false) String fleetName, @RequestParam(required = false) Long scheduledStartDate, @RequestParam(required = false) Long scheduledEndDate,  @RequestParam(required = false) String currentJobId ) {
        try {
            String organizationId = getOrganizationId(request);
            checkJobConflict(organizationId, fleetName, scheduledStartDate, scheduledEndDate, currentJobId);
            checkEquipmentConflict(fleetName, organizationId);
            return ResponseEntity.ok("");
        } catch (Exception ex) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to Check Conflict");
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**
     * Checks for scheduling conflicts for a job based on fleet and date range.
     *
     * @param fleetName The fleet name to check conflicts for
     * @param scheduledStartDate The scheduled start date of the job
     * @param scheduledEndDate The scheduled end date of the job
     * @param currentJobId Optional - The current job ID (for update scenarios, to exclude self from conflict check)
     */
    public void checkJobConflict(String organizationId, String fleetName,
            Long scheduledStartDate,
            Long scheduledEndDate,
            String currentJobId) {

        // Validate input parameters
        if (fleetName == null || scheduledStartDate == null || scheduledEndDate == null) {
            throw new IllegalArgumentException("Fleet Name, scheduled start date, and scheduled end date are required for conflict check");
        }

        // Build query to find conflicting jobs
        // A conflict exists if there's any job with the same fleet where date ranges overlap
        Query query = new Query();
        query.addCriteria(Criteria.where("fleet").is(fleetName));
        query.addCriteria(Criteria.where("organizationId").is(organizationId));

        // Exclude current job from conflict check (for update scenarios)
        if (currentJobId != null && !currentJobId.isEmpty()) {
            query.addCriteria(Criteria.where("_id").ne(currentJobId));
        }

        // Date range overlap logic:
        // Two date ranges overlap if: startA <= endB AND endA >= startB
        Criteria dateOverlapCriteria = new Criteria().andOperator(
                Criteria.where("rts").lte(scheduledEndDate),
                Criteria.where("expectedEndDate").gte(scheduledStartDate)
        );

        query.addCriteria(dateOverlapCriteria);

        // Check if any conflicting job exists
        boolean conflictExists = mongoTemplate.exists(query, Job.class);

        if (conflictExists) {
            throw new IllegalArgumentException("A job is already scheduled for this fleet during the specified date range.");
        }
    }

    /**
     * Checks for equipment conflicts based on equipment availability status for a single fleet.
     *
     * @param fleetName The fleet name to check equipment for
     * @param organizationId The organization ID
     */
    public void checkEquipmentConflict(String fleetName,
            String organizationId) {

        // Validate input parameters
        if (fleetName == null || fleetName.isEmpty() || organizationId == null) {
            throw new IllegalArgumentException("Fleet name and organization ID are required for equipment conflict check");
        }

        // Step 1: Fetch fleet based on name
        Query fleetQuery = new Query();
        fleetQuery.addCriteria(Criteria.where("name").is(fleetName).and("organizationId").is(organizationId));

        Fleet fleet = mongoTemplate.findOne(fleetQuery, Fleet.class);

        if (fleet == null) {
            throw new IllegalArgumentException("No fleet found with the name: " + fleetName);
        }

        String fleetId = fleet.getId();

        // Step 2: Fetch all equipment for this fleet ID and organization ID
        Query equipmentQuery = new Query();
        equipmentQuery.addCriteria(
                Criteria.where("fleetId").is(fleetId)
                        .and("organizationId").is(organizationId)
        );

        List<OnSiteEquipment> equipments = mongoTemplate.find(equipmentQuery, OnSiteEquipment.class);

        if (equipments.isEmpty()) {
            // No equipment found for this fleet, no conflict
            return;
        }

        // Step 3: Check equipment availability
        // Valid statuses: "Active", "Available", "Ready", or empty/null
        Set<String> validStatuses = new HashSet<>(Arrays.asList("Active", "Available", "Ready"));

        List<String> unavailableEquipments = new ArrayList<>();

        for (OnSiteEquipment equipment : equipments) {
            String status = equipment.getNewAddStatus();

            // Equipment is unavailable if status is NOT in valid statuses and NOT empty/null
            if (status != null && !status.isEmpty() && !validStatuses.contains(status)) {
                unavailableEquipments.add(equipment.getName() + " (" + status + ")");
            }
        }

        // Step 4: Throw conflict exception if any equipment is unavailable
        if (!unavailableEquipments.isEmpty()) {
            throw new IllegalArgumentException("Equipment Unavailable!");
        }
    }

    /**
     * PATCH endpoint to update inProgressChemicalStage for a specific well
     *
     * @param jobId Job ID
     * @param patchDto DTO containing inProgressChemicalStage data
     * @return Updated job object
     */
    @PatchMapping("/{jobId}/in-progress-chemical-stage")
    public ResponseEntity<?> updateInProgressChemicalStage(
            HttpServletRequest request,
            @PathVariable String jobId,
            @Valid @RequestBody JobInProgressChemicalStagePatchDto patchDto) {


        try {
            patchDto.setId(jobId);

            return jobChemicalStageService.updateInProgressChemicalStage(request, jobId, patchDto);

        } catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body(Map.of(
                                  "error", "Update failed",
                                  "message", e.getMessage()
                          ));
        }
    }

    /**
     * PATCH endpoint to update inProgressProppantStage for a specific well
     *
     * @param jobId Job ID
     * @param patchDto DTO containing inProgressProppantStage data
     * @return Updated job object
     */
    @PatchMapping("/{jobId}/in-progress-proppant-stage")
    public ResponseEntity<?> updateInProgressProppantStage(
            HttpServletRequest request,
            @PathVariable String jobId,
            @Valid @RequestBody JobInProgressProppantStagePatchDto patchDto) {

        try {
            patchDto.setId(jobId);

            return jobProppantStageService.updateInProgressProppantStage(request, jobId, patchDto);

        }catch (RuntimeException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body(Map.of(
                                          "error", "Update failed",
                                          "message", e.getMessage()
                                  ));
        }
    }

    @GetMapping(value = "/getByStatus")
    public ResponseEntity<List<JobDTOResponse>> getByStatus(HttpServletRequest request, @RequestParam Set<String> status) {
        String organizationId = getOrganizationId(request);
        List<String> districtIds = getDistrictIds(request);
        String organizationType = getOrganizationType(request);
        List<Job> jobList;
        if (status.contains(Constants.ALL)) {
            status.clear();
            status.add(Constants.IN_PROGRESS);
            status.add(Constants.COMPLETED);
            status.add(Constants.SCHEDULED);
        }
        if (organizationType.contentEquals(Constants.OPERATOR)) {
            jobList = jobService.findBySharedWithOrganizationIdAndDistrictIdsAndStatusIn(organizationId, districtIds, status);
        } else {
            jobList = jobService.findByOrganizationIdAndDistrictIdsAndStatusIn(organizationId, districtIds, status);
        }
        List<JobDTOResponse> response = modelMapper.map(jobList, new TypeToken<List<JobDTOResponse>>() {
        }.getType());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/activity-breakdown", method = RequestMethod.GET)
    public ResponseEntity getOperatorJobPanel(HttpServletRequest request) {
        return jobService.getOperatorJobPanel(request);
    }
}