package com.carbo.job.services;

import com.carbo.job.events.PriceBookComponentChangeEvent;
import com.carbo.job.events.UpdateJobStartDateEvent;
import com.carbo.job.events.UpdateWellEvent;
import com.carbo.job.events.model.PadChangeModel;
import com.carbo.job.events.model.PriceBookComponentNameChangeModel;
import com.carbo.job.events.model.WellChangeModel;
import com.carbo.job.exception.ErrorException;
import com.carbo.job.model.*;
import com.carbo.job.model.Error.Error;
import com.carbo.job.model.jobPanel.Coordinates;
import com.carbo.job.model.jobPanel.JobData;
import com.carbo.job.model.jobPanel.JobPanel;
import com.carbo.job.repository.*;
import com.carbo.job.model.Fleet;
import com.carbo.job.model.Job;
import com.carbo.job.utils.Constants;
import com.carbo.job.utils.ErrorConstants;
import com.carbo.proppantstage.model.ProppantContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.carbo.job.utils.CommonUtils.round;
import static com.carbo.job.utils.Constants.*;
import static com.carbo.job.utils.ControllerUtil.*;
import static com.carbo.job.utils.ErrorConstants.JOB_NOT_FOUND_CODE;
import static com.carbo.job.utils.ErrorConstants.JOB_NOT_FOUND_MESSAGE;
import static java.util.Collections.emptyList;

@Service
@Slf4j
public class JobService {
    private final OnSiteEquipmentMongoDbRepository onSiteEquipmentMongoDbRepository;

    private final EquipmentInsightsRepository equipmentInsightsRepository;

    private final JobMongoDbRepository jobMongoDbRepository;

    private final FleetMongoDbRepository fleetMongoDbRepository;

    private final FleetService fleetService;

    private final MongoTemplate mongoTemplate;

    private final NotificationService notificationService;

    private final ActivityLogMongoDbRepository activityLogMongoDbRepository;

    private final WellMongoDbRepository wellMongoDbRepository;

    private final PadMongoDbRepository padMongoDbRepository;

    private final DistrictMongoDbRepository districtMongoDbRepository;

    private final UserMongoDbRepository userMongoDbRepository;

    private final OrganizationMongoDbRepository organizationMongoDbRepository;
    private final ReleaseNotesMongoDbRepository releaseNotesMongoDbRepository;

    private final JobItemCodeDiscountsMongoDbRepository jobItemCodeDiscountsMongoDbRepository;

    private final BucketTestsRepository bucketTestsRepository;
    private final OperatorMongoDbRepository operatorMongoDbRepository;

    private final JobInsightsMongoDbRepository jobInsightsMongoDbRepository;

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    public JobService(JobMongoDbRepository jobRepository, FleetMongoDbRepository fleetMongoDbRepository, ActivityLogMongoDbRepository activityLogMongoDbRepository, FleetService fleetService, MongoTemplate mongoTemplate,
                      NotificationService notificationService, ActivityLogMongoDbRepository activityLogMongoDbRepository1, WellMongoDbRepository wellMongoDbRepository, PadMongoDbRepository padMongoDbRepository,
                      DistrictMongoDbRepository districtMongoDbRepository, OrganizationMongoDbRepository organizationMongoDbRepository, UserMongoDbRepository userMongoDbRepository, ReleaseNotesMongoDbRepository releaseNotesMongoDbRepository, JobItemCodeDiscountsMongoDbRepository jobItemCodeDiscountsMongoDbRepository, BucketTestsRepository bucketTestsRepository, OperatorMongoDbRepository operatorMongoDbRepository,
                      EquipmentInsightsRepository equipmentInsightsRepository, OnSiteEquipmentMongoDbRepository onSiteEquipmentMongoDbRepository,
                      JobInsightsMongoDbRepository jobInsightsMongoDbRepository) {
        this.jobMongoDbRepository = jobRepository;
        this.fleetMongoDbRepository = fleetMongoDbRepository;
        this.fleetService = fleetService;
        this.mongoTemplate = mongoTemplate;
        this.notificationService = notificationService;
        this.activityLogMongoDbRepository = activityLogMongoDbRepository1;
        this.wellMongoDbRepository = wellMongoDbRepository;
        this.padMongoDbRepository = padMongoDbRepository;
        this.districtMongoDbRepository = districtMongoDbRepository;
        this.userMongoDbRepository = userMongoDbRepository;
        this.organizationMongoDbRepository = organizationMongoDbRepository;
        this.releaseNotesMongoDbRepository = releaseNotesMongoDbRepository;
        this.jobItemCodeDiscountsMongoDbRepository = jobItemCodeDiscountsMongoDbRepository;
        this.bucketTestsRepository = bucketTestsRepository;
        this.operatorMongoDbRepository = operatorMongoDbRepository;
        this.equipmentInsightsRepository = equipmentInsightsRepository;
        this.onSiteEquipmentMongoDbRepository = onSiteEquipmentMongoDbRepository;
        this.jobInsightsMongoDbRepository = jobInsightsMongoDbRepository;
    }

    public List<Job> getAll() {
        return jobMongoDbRepository.findAllNoneBackup();
    }

    public List<Job> getByOrganizationId(String organizationId) {
        return jobMongoDbRepository.findByOrganizationId(organizationId);
    }

    public List<Job> getByOrganizationIdSimplified(String organizationId) {
        return jobMongoDbRepository.findByOrganizationIdSimplified(organizationId);
    }

    public List<Job> findByOrganizationIdAndAndModifiedBetween(String organizationId, long startDate, long endDate) {
        return jobMongoDbRepository.findByOrganizationIdAndModifiedBetween(organizationId, startDate, endDate);
    }

    public Optional<Job> getByOrganizationIdAndJobId(String organizationId, String jobId) {
        List<Job> fromDB = jobMongoDbRepository.findByOrganizationIdAndId(organizationId, jobId);
        return fromDB.size() > 0 ? Optional.of(fromDB.get(0)) : Optional.empty();
    }

    public Optional<Job> getByOrganizationIdAndJobIdAndDistrictId(HttpServletRequest request, String organizationId, String jobId, String districtId) {
        List<Job> fromDB = jobMongoDbRepository.findByOrganizationIdAndId(organizationId, jobId);
        fromDB = removeJobsFromUnauthorizedDistricts(request, organizationId, districtId, fromDB);
        return fromDB.size() > 0 ? Optional.of(fromDB.get(0)) : Optional.empty();
    }

    public List<Job> getByOrganizationIdAndDistrictId(HttpServletRequest request, String organizationId, String districtId) {
        List<Job> fromDB = jobMongoDbRepository.findByOrganizationId(organizationId);
        fromDB = removeJobsFromUnauthorizedDistricts(request, organizationId, districtId, fromDB);
        return fromDB;
    }

    public List<Job> getByOrganizationIdAndDistrictIdIn(String organizationId, List<String> districtIds) {
        List<Job> fromDB = jobMongoDbRepository.findByOrganizationIdAndDistrictIdIn(organizationId, districtIds);
        return fromDB;
    }

    public List<Job> getByOrganizationIdAndStatusAndDistrictIdIn(String organizationId, String status, List<String> districtIds) {
        List<Job> fromDB = jobMongoDbRepository.findByOrganizationIdAndStatusAndDistrictIdIn(organizationId, status, districtIds);
        return fromDB;
    }

    public List<Job> getByOrganizationIdAndWellApi(String organizationId, String api) {
        List<Job> fromDB = jobMongoDbRepository.findByOrganizationIdAndWellApi(organizationId, api);
        return fromDB;
    }

    private List<Job> removeJobsFromUnauthorizedDistricts(HttpServletRequest request, String organizationId, String districtId, List<Job> fromDB) {
        fromDB.removeIf(job -> {
            Optional<Fleet> fleet = fleetService.getByName(request, organizationId, job.getFleet());
            if (fleet.isPresent()) {
                return !fleet.get().getDistrictId().equals(districtId);
            } else {
                return true;
            }
        });

        return fromDB;
    }

    public ZoneId getZone(Pad pad) {
        if (!ObjectUtils.isEmpty(pad)) {
            if (pad.getTimezone() != null) {
                TimeZone tz = TimeZone.getTimeZone(pad.getTimezone());
                return tz.toZoneId();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Job> getBackupJobOlderThanNDays() {
        Date timeAgo = new Date(Instant.now().minus(12, ChronoUnit.HOURS).toEpochMilli());
        return jobMongoDbRepository.findByBackupDateBefore(timeAgo, PageRequest.of(0, 10));
    }

    public Optional<Job> getJob(String jobId) {
        return jobMongoDbRepository.findById(jobId);
    }

    public Job getSimplifiedJob(String jobId) {
        return jobMongoDbRepository.getSimplifiedJob(jobId).get(0);
    }

    public Optional<Job> getSimplifiedJobForNPT(String jobId) {
        List<Job> fromDB = jobMongoDbRepository.getSimplifiedJobForNPT(jobId);
        return fromDB.size() > 0 ? Optional.of(fromDB.get(0)) : Optional.empty();
    }

    public Job saveJob(Job job) {
        // Check if priceBookId is null before save
        if (ObjectUtils.isEmpty(job.getPriceBookId())) {
            logger.warn("saveJob: Job being saved with NULL priceBookId. JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                       job.getJobNumber(),
                       job.getOrganizationId());

            // Set default priceBookId only when it's null
            ensurePriceBookId(job);
        }

        Job jobResponse = jobMongoDbRepository.save(job);
        saveEquipment(jobResponse);
        return jobResponse;
    }

    /**
     * Ensures that the job has a valid priceBookId.
     * If priceBookId is null or empty, it attempts to set the default pricebook for the organization.
     * Throws exception if no default pricebook is found.
     *
     * @param job The job to ensure priceBookId for
     * @throws IllegalStateException if no default pricebook is found
     */
    private void ensurePriceBookId(Job job) {
        if (ObjectUtils.isEmpty(job.getPriceBookId())) {
            Query priceBookQuery = new Query();
            priceBookQuery.addCriteria(
                    Criteria.where(ORGANIZATION_ID).is(job.getOrganizationId())
                            .and("priceBookName").is("XOPS Default Pricebook"));
            PriceBook priceBook = mongoTemplate.findOne(priceBookQuery, PriceBook.class);
            if (!ObjectUtils.isEmpty(priceBook) && !ObjectUtils.isEmpty(priceBook.getId())) {
                job.setPriceBookId(priceBook.getId());
                logger.info("Set default priceBookId {} for job {} in organization {}",
                           priceBook.getId(), job.getId(), job.getOrganizationId());
            } else {
                logger.error("CRITICAL: No default pricebook found for organization {}. Cannot save/update job without priceBookId",
                           job.getOrganizationId());
                throw new IllegalStateException(
                    String.format("Cannot save/update job: No default pricebook (XOPS Default Pricebook) found for organization: %s",
                                job.getOrganizationId())
                );
            }
        }
    }

    private void saveEquipment(Job job) {
        if (job == null || job.getOrganizationId() == null || job.getFleet() == null) return;

        // Fetch Fleet
        Fleet fleet = fleetMongoDbRepository.findByOrganizationIdAndName(job.getOrganizationId(), job.getFleet());
        if (fleet == null || fleet.getId() == null) return;

        // Fetch all OnSiteEquipments for this Fleet
        List<OnSiteEquipment> equipments = onSiteEquipmentMongoDbRepository
                .findByFleetIdAndOrganizationId(fleet.getId(), job.getOrganizationId());
        if (equipments == null || equipments.isEmpty()) return;

        List<String> equipmentIds = equipments.stream().map(OnSiteEquipment::getId).collect(Collectors.toList());

        // Fetch unassigned insights (jobNumber == null) for current fleet
        List<EquipmentInsights> unassignedInsights =
                equipmentInsightsRepository.findByFleetIdAndEquipmentIdInAndJobNumberIsNull(
                        fleet.getId(), equipmentIds);

        Map<String, EquipmentInsights> unassignedMap = unassignedInsights.stream()
                .collect(Collectors.toMap(
                        EquipmentInsights::getEquipmentId,
                        Function.identity(),
                        (existing, replacement) -> existing // pick first if multiple
                ));

        List<EquipmentInsights> insightsList = new ArrayList<>();

        for (OnSiteEquipment equipment : equipments) {
            if (equipment == null) continue;

            Long startDate = (job.getStartDate() != null) ? job.getStartDate() : job.getRts();
            Long endDate = (job.getEndDate() != null) ? job.getEndDate() : job.getExpectedEndDate();

            // Check if unassigned insight exists → update
            EquipmentInsights insight = unassignedMap.get(equipment.getId());
            if (insight == null) {
                // No unassigned insight → create new
                insight = new EquipmentInsights();
            }

            // Update all fields
            updateInsightWithJobAndEquipment(insight, job, fleet, equipment, startDate, endDate);

            insightsList.add(insight);
        }

        if (!insightsList.isEmpty()) {
            equipmentInsightsRepository.saveAll(insightsList);
        }
    }



    // Helper method to reduce repetition
    private void updateInsightWithJobAndEquipment(EquipmentInsights insight, Job job, Fleet fleet,
                                                  OnSiteEquipment equipment, Long startDate, Long endDate) {
        // Rental logic
        if (!ObjectUtils.isEmpty(equipment.getRental()) && Constants.YES.equalsIgnoreCase(equipment.getRental())) {
            insight.setRentalStartDate(equipment.getRentalStartDate());
            insight.setRentalEndDate(equipment.getRentalEndDate());

        }
        // Dates
        if (ObjectUtils.isEmpty(insight.getStartDate())) {
            insight.setStartDate(ObjectUtils.isEmpty(equipment.getFleetAssignDate()) ? startDate : equipment.getFleetAssignDate());
        }
        if (!ObjectUtils.isEmpty(equipment.getFleetAssignEndDate())) {
            insight.setEndDate(equipment.getFleetAssignEndDate());
        }
        if(!ObjectUtils.isEmpty(insight.getStartDate()) && !ObjectUtils.isEmpty(insight.getEndDate())){
          long totalDays = calculateJobDays(insight.getStartDate(),insight.getEndDate());
          insight.setJobDays(totalDays);
        }
        // Job details
        insight.setOrganizationId(job.getOrganizationId());
        insight.setJobId(job.getId());
        insight.setJobNumber(job.getJobNumber());
        insight.setJobStatus(job.getStatus());
        insight.setJobStartDate(startDate);
        insight.setJobEndDate(endDate);

        // Fleet details
        insight.setFleetName(fleet.getName());
        insight.setFleetId(fleet.getId());
        insight.setFleetType(fleet.getFleetType());
        insight.setFleetStatus(FleetStatusEnum.ACTIVE);

        // Equipment details
        insight.setEquipmentId(equipment.getId());
        insight.setEquipmentName(equipment.getName());
        insight.setEquipmentType(equipment.getType());
        insight.setRental(equipment.getRental());

        if (insight.getId() == null) {
            insight.setCreatedTime(System.currentTimeMillis());
        }
    }
    private Long calculateJobDays(Long startDate, Long endDate) {
        Instant startInstant = Instant.ofEpochMilli(startDate);
        Instant endInstant = Instant.ofEpochMilli(endDate);
        return ChronoUnit.DAYS.between(
                startInstant.atZone(ZoneId.systemDefault()).toLocalDate(),
                endInstant.atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }


    public Job updateJob(Job job) {
        // Check if priceBookId is null before update
        if (ObjectUtils.isEmpty(job.getPriceBookId())) {
            logger.warn("updateJob: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                       job.getId(),
                       job.getJobNumber(),
                       job.getOrganizationId());

            // Set default priceBookId only when it's null
            ensurePriceBookId(job);
        }

        return jobMongoDbRepository.save(job);
    }

    public void deleteJob(String jobId) {
        jobMongoDbRepository.deleteById(jobId);
    }

    // Update well event listener
    @EventListener ({ UpdateWellEvent.class })
    void updateWellEvent(UpdateWellEvent event) {
        WellChangeModel wellChangeModel = event.getWellChangeModel();
        List<Job> jobs = jobMongoDbRepository.findByOrganizationIdAndWellApi(wellChangeModel.getOrganizationId(), wellChangeModel.getWellAPI());
        if (jobs != null && !jobs.isEmpty()) {
            // Should have only 1
            jobs.forEach(job -> {
                job.getWells().forEach(well -> {
                    if (well.getApi().equals(wellChangeModel.getWellAPI())) {
                        well.setTotalStages(event.getWellChangeModel().getTotalStages());
                        well.setFracproId(event.getWellChangeModel().getFracproId());
                        well.setAfeNumber(event.getWellChangeModel().getWellAFE());
                    }
                });
                updateJob(job);
            });
            notificationService.sendSseEventsToUI(event.getWellChangeModel());
        }
    }

    // Update Job Start Date event listener
    @EventListener ({ UpdateJobStartDateEvent.class })
    void updateJobStartDateEvent(UpdateJobStartDateEvent event) {
        PadChangeModel updatedPad = event.getPadChangeModel();
        List<Job> jobsInPad = jobMongoDbRepository.findByOrganizationIdAndPad(updatedPad.getOrganizationId(), updatedPad.getPadName());
        if (jobsInPad != null && !jobsInPad.isEmpty()) {
            // Should have only 1 job per pad
            jobsInPad.forEach(job -> {
                if (job.getStartDate() != null) {
                    ZoneId previousTimezone = ZoneId.of(updatedPad.getPreviousTimezone());
                    Instant previousStartDateInstant = Instant.ofEpochMilli(job.getStartDate());
                    ZonedDateTime previousStartDateWithPreviousTimezone = ZonedDateTime.ofInstant(previousStartDateInstant, previousTimezone);
                    LocalDateTime previousStartDateTimeLocal = previousStartDateWithPreviousTimezone.toLocalDateTime();

                    ZoneId currentTimezone = ZoneId.of(updatedPad.getUpdatedTimezone());
                    ZonedDateTime updatedStartDateWithCurrentTimezone = ZonedDateTime.of(previousStartDateTimeLocal, currentTimezone);
                    Instant updatedStartDateInstant = updatedStartDateWithCurrentTimezone.toInstant();

                    job.setStartDate(updatedStartDateInstant.toEpochMilli());
                    updateJob(job);
                }
            });
        }
    }


    @EventListener ({ PriceBookComponentChangeEvent.class })
    public void handleComponentNameChange(PriceBookComponentChangeEvent event) {
        PriceBookComponentNameChangeModel changeModel = event.getChangeModel();
        log.info("=== Received component name change event ===");
        log.info("Type: {}, ItemCode: {}, OldName: {} -> NewName: {}",
                changeModel.getType(), changeModel.getItemCode(), changeModel.getOldName(), changeModel.getNewName());

        try {
            if (changeModel.getType() == PriceBookComponentNameChangeModel.ComponentType.CHEMICAL) {
                updateChemicalNamesByItemCode(changeModel);
            } else if (changeModel.getType() == PriceBookComponentNameChangeModel.ComponentType.PROPPANT) {
                updateProppantNamesByItemCode(changeModel);
            }
        } catch (Exception e) {
            log.error("Error processing component name change event for itemCode: {}",
                    changeModel.getItemCode(), e);
        }
    }

    /**
     * Update chemical names across all jobs for the organization
     * Matches by itemCode (code field) and updates the name
     */
    private void updateChemicalNamesByItemCode(PriceBookComponentNameChangeModel event) {
        log.info("Starting chemical update for itemCode: {} in organization: {}",
                event.getItemCode(), event.getOrganizationId());

        if (ObjectUtils.isEmpty(event.getItemCode())) {
            log.warn("ItemCode is empty, cannot update chemicals");
            return;
        }

        // Find all jobs for the organization (you can add status filter if needed)
        Query jobQuery = new Query();
        jobQuery.addCriteria(Criteria.where(ORGANIZATION_ID).is(event.getOrganizationId()));
        // Uncomment below if you only want to update IN_PROGRESS jobs
         jobQuery.addCriteria(Criteria.where(STATUS).is(IN_PROGRESS));

        List<Job> jobs = mongoTemplate.find(jobQuery, Job.class);
        log.info("Found {} jobs to scan for organization {}", jobs.size(), event.getOrganizationId());

        AtomicInteger jobsUpdated = new AtomicInteger(0);
        AtomicInteger chemicalsUpdated = new AtomicInteger(0);

        for (Job job : jobs) {
            boolean jobModified = false;

            if (!CollectionUtils.isEmpty(job.getWells())) {
                for (Well well : job.getWells()) {
                    int updatesInWell = 0;

                    // Update additionalChemicalTypes (UI uses this for ALL chemical types)
                    if (well.getAdditionalChemicalTypes() != null &&
                            !well.getAdditionalChemicalTypes().isEmpty()) {

                        for (Map.Entry<String, List<Chemical>> entry : well.getAdditionalChemicalTypes().entrySet()) {
                            List<Chemical> chemicals = entry.getValue();
                            if (!CollectionUtils.isEmpty(chemicals)) {
                                updatesInWell += updateChemicalList(chemicals, event.getItemCode(), event.getNewName());
                            }
                        }
                    }

                    // Also update legacy fields (in case some old data exists)
                    updatesInWell += updateChemicalList(well.getAcidAdditives(), event.getItemCode(), event.getNewName());
                    updatesInWell += updateChemicalList(well.getSlickwaters(), event.getItemCode(), event.getNewName());
                    updatesInWell += updateChemicalList(well.getLinearGelCrosslinks(), event.getItemCode(), event.getNewName());
                    updatesInWell += updateChemicalList(well.getDiverters(), event.getItemCode(), event.getNewName());

                    // Update in-progress chemical stage
                    if (well.getInProgressChemicalStage() != null) {
                        updatesInWell += updateChemicalStage(well.getInProgressChemicalStage(),
                                event.getItemCode(), event.getNewName());
                    }

                    if (updatesInWell > 0) {
                        well.setModified(new Date().getTime());
                        jobModified = true;
                        chemicalsUpdated.addAndGet(updatesInWell);
                        log.debug("Updated {} chemicals in well: {}", updatesInWell, well.getName());
                    }
                }
            }

            // Save the job if modified
            if (jobModified) {
                job.setTs(System.currentTimeMillis());
                job.setModified(System.currentTimeMillis());
                updateJob(job);
                jobsUpdated.incrementAndGet();
                log.info("Updated job: {} (JobNumber: {}) with new chemical name for itemCode: {}",
                        job.getName(), job.getJobNumber(), event.getItemCode());
            }
        }

        log.info("=== Chemical update completed: {} jobs updated, {} chemicals updated for itemCode: {} ===",
                jobsUpdated.get(), chemicalsUpdated.get(), event.getItemCode());
    }

    /**
     * Update proppant names across all jobs for the organization
     * Matches by itemCode (code field) and updates the name
     */
    private void updateProppantNamesByItemCode(PriceBookComponentNameChangeModel event) {
        log.info("Starting proppant update for itemCode: {} in organization: {}",
                event.getItemCode(), event.getOrganizationId());

        if (ObjectUtils.isEmpty(event.getItemCode())) {
            log.warn("ItemCode is empty, cannot update proppants");
            return;
        }

        // Find all jobs for the organization
        Query jobQuery = new Query();
        jobQuery.addCriteria(Criteria.where(ORGANIZATION_ID).is(event.getOrganizationId()));
         jobQuery.addCriteria(Criteria.where(STATUS).is(IN_PROGRESS));

        List<Job> jobs = mongoTemplate.find(jobQuery, Job.class);
        log.info("Found {} jobs to scan for organization {}", jobs.size(), event.getOrganizationId());

        AtomicInteger jobsUpdated = new AtomicInteger(0);
        AtomicInteger proppantsUpdated = new AtomicInteger(0);

        for (Job job : jobs) {
            boolean jobModified = false;

            if (!CollectionUtils.isEmpty(job.getWells())) {
                for (Well well : job.getWells()) {
                    int updatesInWell = 0;

                    // Update proppants list
                    updatesInWell += updateProppantList(well.getProppants(), event.getItemCode(), event.getNewName());

                    // Update in-progress proppant stage
                    if (well.getInProgressProppantStage() != null) {
                        updatesInWell += updateProppantStage(well.getInProgressProppantStage(),
                                event.getItemCode(), event.getNewName());
                    }

                    if (updatesInWell > 0) {
                        well.setModified(new Date().getTime());
                        jobModified = true;
                        proppantsUpdated.addAndGet(updatesInWell);
                        log.debug("Updated {} proppants in well: {}", updatesInWell, well.getName());
                    }
                }
            }

            // Save the job if modified
            if (jobModified) {
                job.setTs(System.currentTimeMillis());
                job.setModified(System.currentTimeMillis());
                updateJob(job);
                jobsUpdated.incrementAndGet();
                log.info("Updated job: {} (JobNumber: {}) with new proppant name for itemCode: {}",
                        job.getName(), job.getJobNumber(), event.getItemCode());
            }
        }

        log.info("=== Proppant update completed: {} jobs updated, {} proppants updated for itemCode: {} ===",
                jobsUpdated.get(), proppantsUpdated.get(), event.getItemCode());
    }

    /**
     * Update proppants in ProppantStageNoIndex
     * Adjust this based on your actual ProppantStageNoIndex structure
     */
    private int updateProppantStage(ProppantStageNoIndex stage, String itemCode, String newName) {
        if (stage == null) {
            return 0;
        }

        int updateCount = 0;

        // Update silos (Map<String, ProppantContainer>)
        if (stage.getSilos() != null && !stage.getSilos().isEmpty()) {
            for (Map.Entry<String, ProppantContainer> entry : stage.getSilos().entrySet()) {
                ProppantContainer container = entry.getValue();
                updateCount += updateProppantContainer(container, itemCode, newName);
            }
        }

        // Update runOrders (List<ProppantContainer>)
        if (!CollectionUtils.isEmpty(stage.getRunOrders())) {
            for (ProppantContainer container : stage.getRunOrders()) {
                updateCount += updateProppantContainer(container, itemCode, newName);
            }
        }

        if (updateCount > 0) {
            stage.setModified(new Date().getTime());
            log.debug("Updated {} proppants in ProppantStageNoIndex for stage: {}", updateCount, stage.getStage());
        }

        return updateCount;
    }

    /**
     * Update proppant within a ProppantContainer
     * ProppantContainer can have:
     * - proppant: single Proppant object
     * - proppants: List<Proppant>
     */
    private int updateProppantContainer(ProppantContainer container, String itemCode, String newName) {
        if (container == null) {
            return 0;
        }

        int updateCount = 0;

        // Update single proppant
        if (container.getProppant() != null && (updateSingleProppant(container.getProppant(), itemCode, newName))) {
                updateCount++;

        }

        // Update proppants list (if exists)
        if (!CollectionUtils.isEmpty(container.getProppants())) {
            for (Proppant proppant : container.getProppants()) {
                if (updateSingleProppant(proppant, itemCode, newName)) {
                    updateCount++;
                }
            }
        }

        return updateCount;
    }

    private boolean updateSingleProppant(Proppant proppant, String itemCode, String newName) {
        if (proppant != null && proppant.getCode() != null && proppant.getCode().equals(itemCode)) {
            String oldName = proppant.getName();
            proppant.setName(newName);
            proppant.setModified(new Date().getTime());
            log.debug("Updated single proppant: {} -> {} (itemCode: {})", oldName, newName, itemCode);
            return true;
        }
        return false;
    }
    // ================= Helper Methods =================

    /**
     * Update chemicals in a list by matching itemCode (code field)
     */
    private int updateChemicalList(List<Chemical> chemicals, String itemCode, String newName) {
        if (CollectionUtils.isEmpty(chemicals) || ObjectUtils.isEmpty(itemCode)) {
            return 0;
        }

        int updateCount = 0;
        for (Chemical chemical : chemicals) {
            if (chemical.getCode() != null && chemical.getCode().equals(itemCode)) {
                String oldName = chemical.getName();
                chemical.setName(newName);
                updateCount++;
                log.debug("Updated chemical: {} -> {} (itemCode: {})", oldName, newName, itemCode);
            }
        }
        return updateCount;
    }

    /**
     * Update proppants in a list by matching itemCode (code field)
     */
    private int updateProppantList(List<Proppant> proppants, String itemCode, String newName) {
        if (CollectionUtils.isEmpty(proppants) || ObjectUtils.isEmpty(itemCode)) {
            return 0;
        }

        int updateCount = 0;
        for (Proppant proppant : proppants) {
            if (proppant.getCode() != null && proppant.getCode().equals(itemCode)) {
                String oldName = proppant.getName();
                proppant.setName(newName);
                proppant.setModified(new Date().getTime());
                updateCount++;
                log.debug("Updated proppant: {} -> {} (itemCode: {})", oldName, newName, itemCode);
            }
        }
        return updateCount;
    }

    private int updateChemicalStage(ChemicalStageNoIndex stage, String itemCode, String newName) {
        if (stage == null) {
            return 0;
        }

        int updateCount = 0;

        // Update chemicalAdditionUnit1
        updateCount += updateStrapsChemicals(stage.getChemicalAdditionUnit1(), itemCode, newName);

        // Update chemicalAdditionUnit2
        updateCount += updateStrapsChemicals(stage.getChemicalAdditionUnit2(), itemCode, newName);

        // Update isosTransport
        updateCount += updateStrapsChemicals(stage.getIsosTransport(), itemCode, newName);

        // Update dryAdd
        updateCount += updateStrapsChemicals(stage.getDryAdd(), itemCode, newName);

        if (updateCount > 0) {
            stage.setModified(new Date().getTime());
            log.debug("Updated {} chemicals in ChemicalStageNoIndex for stage: {}", updateCount, stage.getStage());
        }

        return updateCount;
    }

    /**
     * Update chemicals within a list of Straps
     * Each Strap contains a Chemical object that needs to be checked
     */
    private int updateStrapsChemicals(List<Strap> straps, String itemCode, String newName) {
        if (CollectionUtils.isEmpty(straps)) {
            return 0;
        }

        int updateCount = 0;
        for (Strap strap : straps) {
            if (strap != null && strap.getChemical() != null) {
                Chemical chemical = strap.getChemical();
                if (chemical.getCode() != null && chemical.getCode().equals(itemCode)) {
                    String oldName = chemical.getName();
                    chemical.setName(newName);
                    chemical.setModified(new Date().getTime());
                    updateCount++;
                    log.debug("Updated chemical in Strap: {} -> {} (itemCode: {})", oldName, newName, itemCode);
                }
            }
        }
        return updateCount;
    }


    public List<Job> getBySharedWithOrganizationId(String organizationId) {
        return jobMongoDbRepository.findByOrganizationId(organizationId);
    }

    public void assignDistrictToJobs(HttpServletRequest request) {
        //        String organizationId = getOrganizationId(request);
        List<Job> jobs = jobMongoDbRepository.findAll();
        jobs.forEach(job -> {
            String fleetName = job.getFleet();
            Fleet fleet = fleetMongoDbRepository.findByOrganizationIdAndName(job.getOrganizationId(), fleetName);
            if (!ObjectUtils.isEmpty(fleet) && !ObjectUtils.isEmpty(fleet.getDistrictId())) {
                String districtId = fleet.getDistrictId();
                job.setDistrictId(districtId);
            }

            jobMongoDbRepository.save(job);
        });

    }

    public List<Job> getByStatus(String status) {
        return jobMongoDbRepository.findByStatus(status);
    }

    public List<Job> getAllJobs() {
        return jobMongoDbRepository.findAll();

    }

    public ResponseEntity jobPanelDesc(HttpServletRequest request) {
        try {
            String organizationType = getOrganizationType(request);
            String organizationId = getOrganizationId(request);
            String username = getUserName(request);
            String userRoles = getRoles(request);
            String status = Constants.IN_PROGRESS;
            boolean isAccess = true;
            List<Job> jobList = new ArrayList<>();
            Set<String> organizationIds = new HashSet<>();
            User defaultUser = new User();
            Optional<User> user = Optional.of(userMongoDbRepository.findByUserNameIgnoreCase(username).orElse(defaultUser));
            // Convert Optional<User> to List<User>
            if (organizationType.contentEquals("OPERATOR")) {
                jobList = jobMongoDbRepository.findBySharedWithOrganizationIdAndStatus(organizationId, status);
            } else {
                jobList = jobMongoDbRepository.findByOrganizationIdAndStatusAndDistrictIdIn(organizationId, status, user.get().getDistrictids());
                // jobList = jobMongoDbRepository.findByOrganizationIdAndStatus(organizationId, status);

            }
            organizationIds = jobList.stream().map(Job::getOrganizationId).collect(Collectors.toSet());
            List<String> padNames = jobList.stream().map(Job::getPad).collect(Collectors.toList());
            List<Pad> padlist = padMongoDbRepository.findByNameIn(padNames);
            OptionalDouble overAllUtilization = null;
            long jobLatestReport = 0;
            int maxDayValue = 0;
            String latestActivity = "";
            boolean isOpsActivity = false;
            double fleetUtilization = 0;
            double totalActualHours = 0;
            double totalTargetHours = 0;
            jobList = Optional.ofNullable(jobList).orElse(emptyList());
            List<String> wellIds = jobList.stream().flatMap(job -> job.getWells().stream()).map(Well::getId).collect(Collectors.toList());
            List<Well> wells = wellMongoDbRepository.findByIdIn(wellIds);
            List<Organization> organizations = (List<Organization>) organizationMongoDbRepository.findAllById(organizationIds);
            List<String> jobIds = jobList.stream().map(Job::getId).collect(Collectors.toList());
            List<ActivityLogEntry> activityLog = activityLogMongoDbRepository.findByJobIdIn(jobIds);
            activityLog = Optional.ofNullable(activityLog).orElse(emptyList());
            List<JobData> jobDescList = new ArrayList<>();
            for (Job job : jobList) {
                if (userRoles.contains(Constants.ROLE_ADMIN) || userRoles.contains(Constants.ROLE_BACK_OFFICE) || userRoles.contains(
                        Constants.ROLE_CARBO_ADMIN) || userRoles.contains(Constants.ROLE_READ_ONLY)) {
                    isAccess = true;
                } else if (userRoles.contains(Constants.ROLE_FIELD_USER) || userRoles.contains(Constants.ROLE_OPERATION)) {
                    isAccess = false;
                    List<User> jobUsers = job.getUsers(); // Assuming getUsers() returns the list of users for the current job
                    for (User jobUser : jobUsers) {
                        if (jobUser.getUserName().equalsIgnoreCase(username)) {
                            isAccess = true;
                            break; // No need to continue checking if the username is already found
                        }
                    }
                } else {
                    isAccess = false;
                }
                List<ActivityLogEntry> activityLogEntries = new ArrayList<>();
                List<ActivityLogEntry> activityLogEntries2 = new ArrayList<>();
                List<Well> currWellName = new ArrayList<>();

                for (ActivityLogEntry activityLogEntry : activityLog) {
                    if (activityLogEntry.getJobId().equals(job.getId()) && activityLogEntry.getComplete() != null && activityLogEntry.getComplete()) {
                        activityLogEntries.add(activityLogEntry);
                    }
                    if (activityLogEntry.getJobId().equals(job.getId())) {
                        activityLogEntries2.add(activityLogEntry);
                    }
                }

                for (Well wellname : wells) {
                    if (wellname.getId().equals(job.getCurWellId())) {
                        currWellName.add(wellname);
                    }

                }

                // calculate size of true stages
                double completedTrue = activityLogEntries.size();

                // Scheduled Hours
                double scheduledTimeHours = roundValue(activityLogEntries2.stream().filter(each -> each.getOpsActivity().equals("Scheduled Time")).mapToDouble(ActivityLogEntry::getMillisecondsSpan).sum() / 3600000.0, 2);

                // NPT Hours
                double jobNPT = roundValue(activityLogEntries2.stream().filter(each -> !each.getOpsActivity().equals("Scheduled Time")).mapToDouble(ActivityLogEntry::getMillisecondsSpan).sum() / 3600000.0, 2);

                //Latest Activity
                for (ActivityLogEntry activityLogEntry : activityLogEntries2) {
                    String subnpt = activityLogEntry.getSubNptCode();
                    String eventnpt = activityLogEntry.getEventOrNptCode();
                    if (activityLogEntry.getOpsActivity().equals("Scheduled Time")) {
                        latestActivity = eventnpt;
                        isOpsActivity = true;
                    } else {
                        eventnpt = activityLogEntry.getEventOrNptCode();
                        subnpt = subnpt.equals("") ? eventnpt : subnpt;
                        latestActivity = subnpt;
                        isOpsActivity = false;
                    }

                }

                //Fetch Curr Well Name
                String wellname = null;
                for (Well curWellName : currWellName) {
                    wellname = curWellName.getName();
                }

                // Fetch Max day From Activity Log-Entries
                maxDayValue = activityLogEntries2.stream().mapToInt(ActivityLogEntry::getDay).max().orElse(1);

                // Sum of totalStages for all Well
                int sumOfTotalStages = job.getWells().stream().mapToInt(Well::getTotalStages).sum();

                double jobCompletionPercentage = roundValue(completedTrue / (sumOfTotalStages), 2) * 100;

                ZoneId zoneId = null;

                for (Pad pad : padlist) {
                    if (job.getPad().equals(pad.getName())) {
                        zoneId = getZone(pad);
                    }
                }

                int curDay = 0;

                if (zoneId != null && job.getStartDate() != null) {
                    curDay = getCurDay(job.getStartDate(), zoneId);
                }
                // calculate actual pump hours

                int finalCurDay = curDay != 0 ? curDay : 1;

                List<ActivityLogEntry> filtered = activityLogEntries2.stream().filter(each -> each.getDay() <= finalCurDay).collect(Collectors.toList());
                double totalPumpTimeHours = calculateActualPumpHours(activityLogEntries2);
                totalActualHours += totalPumpTimeHours;
                fleetUtilization = (totalPumpTimeHours) / (job.getTargetDailyPumpTime() * maxDayValue);
                fleetUtilization = fleetUtilization * 100;
                totalTargetHours += job.getTargetDailyPumpTime() * maxDayValue;

                // use for Job Latest Report
                //  Job Latest Report Date
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(job.getStartDate() != null ? job.getStartDate() : 0);

                // Add maxumum days to the date
                calendar.add(Calendar.DAY_OF_MONTH, maxDayValue - 1);
                jobLatestReport = calendar.getTimeInMillis();

                // get Latitude and Longitude
                List<Coordinates> coordinates = getCoordinates(job, wells);
                coordinates = Optional.ofNullable(coordinates).orElse(emptyList());

                //set organizationId
                String joborganisationId = job.getOrganizationId();

                //set organizationName
                String jobOrganizationName = "";
                for (Organization organization : organizations) {
                    if (organization.getId().equals(joborganisationId))
                        jobOrganizationName = organization.getName();
                }

                //overall Fleet Utilization
                // number of wells
                int numberOfWells = 0;
                if (!ObjectUtils.isEmpty(job.getWells())) {
                    numberOfWells = job.getWells().size();
                }

                //  set the value

                int count = countDualFuelPumps(job);
                Query query = new Query();
                if (organizationType.contentEquals("OPERATOR")) {
                    query.addCriteria(Criteria.where("name").is(job.getFleet()).and(ORGANIZATION_ID).is(job.getOrganizationId()));
                } else {
                    query.addCriteria(Criteria.where("name").is(job.getFleet()).and(ORGANIZATION_ID).is(organizationId));
                }
                List<Fleet> fleetType  = mongoTemplate.find(query, Fleet.class);
                String type = "";
                if(!ObjectUtils.isEmpty(fleetType)){
                    type = fleetType.get(0).getFleetType();
                }


                JobData jobData = new JobData().builder()
                        .jobId(job.getId())
                        .jobNumber(job.getJobNumber())
                        .jobStartDate(job.getStartDate())
                        .jobLatestReportDate(activityLogEntries2.size() == 0 ? null : jobLatestReport)
                        .jobCompletionPercentage(roundValue(jobCompletionPercentage, 2))
                        .fleetName(job.getFleet())
                        .fleetType(type)
                        .dualFuelPumpCount(count)
                        .operatorName(job.getOperator())
                        .jobNPTHours(jobNPT)
                        .scheduledHours(scheduledTimeHours)
                        .fleetUtilization(roundValue(fleetUtilization, 2))
                        .latestActivity(job.getStartDate() != null && activityLogEntries2.size() != 0 ? latestActivity : "")
                        .isOpsActivity(isOpsActivity)
                        .curWellId(job.getCurWellId())
                        .currWellName(wellname)
                        .curStage(job.getCurStage())
                        .coordinates(coordinates)
                        .organizationId(joborganisationId)
                        .organizationName(jobOrganizationName)
                        .isAccess(isAccess)
                        .padName(job.getPad())
                        .created(job.getCreated())
                        .numberOfWells(numberOfWells)
                        .operationsType(job.getOperationsType())
                        .bankCount(job.getBankCount() != null ? job.getBankCount() : null)
                        .build();
                jobDescList.add(jobData);
            }

            // over all utilization
            overAllUtilization = OptionalDouble.of((totalActualHours / totalTargetHours) * 100);

            JobPanel panel = new JobPanel();
            panel.setUtilization(roundValue(overAllUtilization.getAsDouble(), 2));
            panel.setJobData(jobDescList);
            return new ResponseEntity(panel, HttpStatus.OK);
        } catch (Exception e) {
            // Handle general exceptions outside the loop
            Error error = Error.builder().errorCode(ERROR_WHILE_GETTING_DASHBOARD_DATA).errorMessage(ERROR_WHILE_GETTING_DASHBOARD_DATA).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public int countDualFuelPumps(Job job) {
            List<OnSiteEquipment> allPump = job.getPumps();
            List<String> pumpIds = allPump.stream()
                    .map(OnSiteEquipment::getId)
                    .collect(Collectors.toList());
            Query query = new Query(Criteria.where("_id").in(pumpIds));
            List<OnSiteEquipment> onSiteEquipments = mongoTemplate.find(query, OnSiteEquipment.class);
            Map<String, OnSiteEquipment> equipmentMap = onSiteEquipments.stream()
                    .collect(Collectors.toMap(OnSiteEquipment::getId, equipment -> equipment));
            int count = 0;
            for (OnSiteEquipment singlePump : allPump) {
                OnSiteEquipment equipment = equipmentMap.get(singlePump.getId());
                if (equipment != null && "Pumps".equalsIgnoreCase(singlePump.getType()) && equipment.getEku()) {
                    count++;
                }
            }
            return count;
        }


    private double calculateActualPumpHours(List<ActivityLogEntry> fetchData) {

        return fetchData.stream().filter(entry -> "Pump Time".equalsIgnoreCase(entry.getOpsActivity()) || "Pump Time".equalsIgnoreCase(entry.getEventOrNptCode())).mapToDouble(entry -> calculateHoursBetween(entry.getStart(), entry.getEnd())).sum();
    }

    private long getTotalTimeOfActivityMilliSec(List<ActivityLogEntry> allActivityLogEntries, String activityCode) {
        long totalTime = 0L;
        String curCode;

        for (ActivityLogEntry activity : allActivityLogEntries) {
            curCode = activity.getSubNptCode().isEmpty() ? activity.getEventOrNptCode() : activity.getSubNptCode();
            if (curCode.equals(activityCode)) {
                totalTime += activity.getMillisecondsSpan();
            }
        }

        return totalTime;
    }

    private List<Coordinates> getCoordinates(Job job, List<Well> wells) {
        List<Coordinates> coordinates = new ArrayList<>();
        List<String> jobWellIds = job.getWells().stream().map(Well::getId).collect(Collectors.toList());
        for (Well well : wells) {
            if (jobWellIds.contains(well.getId())) {
                double latitude = well.getLatitude();
                double longitude = well.getLongitude();
                coordinates.add(new Coordinates().builder().latitude(latitude).longitude(longitude).build());
            }
        }
        return coordinates;
    }

    private double calculateHoursBetween(String startTime, String endTime) {
        if (startTime.isEmpty() || endTime.isEmpty()) {
            return 0.0;
        }

        try {
            DateTimeFormatter formatter;
            LocalDateTime start, end;

            if (startTime.length() > 10) {
                formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
                start = LocalDateTime.parse(startTime, formatter);
                end = LocalDateTime.parse(endTime, formatter);
            } else if (startTime.length() < 5) {
                formatter = DateTimeFormatter.ofPattern("HH:mm");
                start = LocalDateTime.parse(startTime, formatter);
                end = LocalDateTime.parse(endTime, formatter);
            } else {
                // Handle unsupported date format
                return 0.0;
            }

            Duration duration = Duration.between(start, end);
            double hours = duration.toMinutes() / 60.0;

            System.out.println("Minutes between: " + duration.toMinutes());
            System.out.println("Hours between: " + hours);

            return hours;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    //    private long calculateHoursBetween(String startDate, String endDate) {
    //        if (startDate.isEmpty() || endDate.isEmpty()) {
    //            return 0L;
    //            }
    //
    //                    try {
    //            DateTimeFormatter formatter;
    //            LocalDateTime start, end;
    //            if (startDate.length() > 10) {
    //                formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
    //                start = LocalDateTime.parse(startDate, formatter);
    //                end = LocalDateTime.parse(endDate, formatter);
    //                System.out.println(ChronoUnit.MINUTES.between(start, end));
    //                return ChronoUnit.HOURS.between(start, end);
    //            } else if (startDate.length() < 5) {
    //                formatter = DateTimeFormatter.ofPattern("HH:mm");
    //                start = LocalDateTime.parse(startDate, formatter);
    //                end = LocalDateTime.parse(endDate, formatter);
    //                System.out.println(ChronoUnit.MINUTES.between(start, end));
    //                return ChronoUnit.HOURS.between(start, end);
    //            } else {
    //                // Handle unsupported date format
    //                return 0L;
    //            }
    //
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            return 0L;
    //        }
    //    }

    public ResponseEntity<?> getTopWidgetsData(HttpServletRequest request) {
        String organizationId = getOrganizationId(request);
        DashboadResponseTotalCards dashboadResponseTotalCards = new DashboadResponseTotalCards();
        List<Job> jobsList = jobMongoDbRepository.findByOrganizationIdAndStatus(organizationId, "In Progress");
        Set<String> jobIds = jobsList.stream().map(Job::getId).collect(Collectors.toSet());
        List<ActivityLogEntry> activityLogEntries = activityLogMongoDbRepository.findByOrganizationIdAndJobIdIn(organizationId, jobIds);
        List<Job> filteredJobs = jobsList.stream().filter(job -> activityLogEntries.stream().anyMatch(
                                                 entry -> entry.getJobId().equals(job.getId()) && (entry.getOpsActivity().equals("Pump Time") || entry.getEventOrNptCode().equals("Pump Time"))))
                                         .collect(Collectors.toList());

        Map<Integer, List<ActivityLogEntry>> entriesByDayFiltered = activityLogEntries.stream().filter(entry -> filteredJobs.stream().anyMatch(job -> entry.getJobId().equals(job.getId()))).collect(Collectors.groupingBy(ActivityLogEntry::getDay));

        long actualMinutes = 0;
        float targetTime = 0.0f;

        for (Job jobId : filteredJobs) {
            //Calculate target pump time according to numbers of days
            List<ActivityLogEntry> filteredEntriesForDays = entriesByDayFiltered.values().stream().flatMap(List::stream).filter(entry -> entry.getJobId().equals(jobId.getId()))
                                                                                .collect(Collectors.toList());

            float jobTargetTime = 0.0f;
            int daySize = 0;
            for (ActivityLogEntry dayCount : filteredEntriesForDays) {
                daySize = dayCount.getDay();
            }
            float targetDailyPumpTime = jobId.getTargetDailyPumpTime();
            jobTargetTime = daySize * targetDailyPumpTime;

            //Calculate actual hours according to pump time.
            List<ActivityLogEntry> filteredEntries = entriesByDayFiltered.values().stream().flatMap(List::stream).filter(entry -> entry.getJobId()
                                                                                                                                       .equals(jobId.getId()) && ("Pump Time".equals(
                    entry.getOpsActivity()) || "Pump Time".equals(entry.getEventOrNptCode()))).collect(Collectors.toList());

            for (ActivityLogEntry entry : filteredEntries) {
                String startTime = entry.getStart();
                String endTime = entry.getEnd();
                try {
                    LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
                    actualMinutes += start.until(end, ChronoUnit.MINUTES);
                } catch (DateTimeParseException e1) {
                    try {
                        LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"));
                        LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"));
                        actualMinutes += startDate.until(endDate, ChronoUnit.MINUTES);
                    } catch (DateTimeParseException e2) {
                        System.out.println("Invalid date/time format for Pump Time");
                    }
                }
            }
            targetTime += jobTargetTime;
        }
        // Further code to calculate target hours, operatorsTotal, fleetsTotal, wellsTotal, jobsTotal, and efficiency
        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueFleets = new HashSet<>();
        List<String> uniqueWells = new ArrayList<>();

        for (Job job : jobsList) {
            uniqueOperators.add(job.getOperator());
            uniqueFleets.add(job.getFleet());
            List<Well> wellList = job.getWells();
            uniqueWells.addAll(wellList.stream().map(Well::getName).collect(Collectors.toList()));
        }

        int operatorsTotal = uniqueOperators.size();
        int fleetsTotal = uniqueFleets.size();
        int wellsTotal = uniqueWells.size();
        int jobsTotal = jobsList.size();

        double targetHours = targetTime;
        double actualHours = (double) actualMinutes / 60;
        double efficiency = (actualHours / targetHours) * 100;

        dashboadResponseTotalCards.setOperatorsTotal(operatorsTotal);
        dashboadResponseTotalCards.setFleetsTotal(fleetsTotal);
        dashboadResponseTotalCards.setWellsTotal(wellsTotal);
        dashboadResponseTotalCards.setJobTotal(jobsTotal);
        dashboadResponseTotalCards.setPumpTimeEfficiency(roundValue(efficiency, 2));

        return new ResponseEntity<>(dashboadResponseTotalCards, HttpStatus.OK);
    }

    public ResponseEntity<?> getJobsByCompany(HttpServletRequest request, String operator, int page, int size) {
        try {
            String organizationId = getOrganizationId(request);
            Pageable pageable = PageRequest.of(page, size);
            Page<Job> jobPage = jobMongoDbRepository.findByOperatorAndOrganizationId(operator, organizationId, pageable);
            if (jobPage.isEmpty()) {
                return new ResponseEntity<>(emptyList(), HttpStatus.OK);
            }
            List<JobCompanyResponse> jobCompanyResponses = jobPage.map(
                    job -> JobCompanyResponse.builder().jobNumber(job.getJobNumber()).fleet(job.getFleet()).Pad(job.getPad()).zipper(job.getZipper()).proppantSchematicType(job.getProppantSchematicType()).location(job.getLocation())
                                             .targetStagesPerDay(job.getTargetStagesPerDay()).operator(job.getOperator()).status(job.getStatus()).build()).getContent();
            return new ResponseEntity<>(jobCompanyResponses, HttpStatus.OK);
        } catch (Exception e) {
            Error error = Error.builder().errorCode(ErrorConstants.NO_COMPANY_FOUND).errorMessage(ErrorConstants.ERROR_WHILE_FETCHING_COMPANY).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateWell(HttpServletRequest request, Well well) {
        String organizationId = getOrganizationId(request);

        // Find jobs with matching organizationId and status IN_PROGRESS or SCHEDULED
        List<String> statusList = new ArrayList<>();
        statusList.add(IN_PROGRESS);
        statusList.add(SCHEDULED);
        List<Job> jobsList = jobMongoDbRepository
                .findByOrganizationIdAndStatusIn(organizationId, statusList);

        if (!ObjectUtils.isEmpty(jobsList)) {
            for (Job job : jobsList) {
                boolean updated = false;

                for (Well existingWell : job.getWells()) {
                    if (existingWell.getId().equals(well.getId())) {
                        existingWell.setName(well.getName());
                        existingWell.setAfeNumber(well.getAfeNumber());
                        existingWell.setLongitude(well.getLongitude());
                        existingWell.setLatitude(well.getLatitude());
                        existingWell.setApi(well.getApi());
                        existingWell.setTotalStages(well.getTotalStages());

                        updated = true;
                        break;
                    }
                }
                if (updated) {
                    // Check if priceBookId is null before saving (from updateWell method)
                    if (ObjectUtils.isEmpty(job.getPriceBookId())) {
                        logger.warn("updateWell: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                                   job.getId(),
                                   job.getJobNumber(),
                                   job.getOrganizationId());
                        ensurePriceBookId(job);
                    }
                    jobMongoDbRepository.save(job);
                }
            }

        }
    }

    public void updateVendors(HttpServletRequest request, Vendor vendor, String oldVendorName, String vendorType) {
        String organizationId = getOrganizationId(request);
        try {
            oldVendorName = URLDecoder.decode(oldVendorName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Query jobQuery = new Query(Criteria.where("organizationId").is(organizationId).and("status").is("In Progress"));
        List<Job> jobsList = mongoTemplate.find(jobQuery, Job.class, "jobs");
        if (!ObjectUtils.isEmpty(jobsList)) {
            for (Job job : jobsList) {
                boolean containsWell = false;
                if (vendorType.equals("Wellhead") && oldVendorName.equals(job.getWellheadCo())) {
                    containsWell = true;
                    job.setWellheadCo(vendor.getName());
                }
                if (vendorType.equals("Wireline") && oldVendorName.equals(job.getWirelineCo())) {
                    containsWell = true;
                    job.setWirelineCo(vendor.getName());
                }
                if (vendorType.equals("WaterTransfer") && oldVendorName.equals(job.getWaterTransferCo())) {
                    containsWell = true;
                    job.setWaterTransferCo(vendor.getName());
                }
                if (containsWell) {
                    // If the wells list has been modified, perform the update
                    // Check if priceBookId is null before saving (from updateVendors method)
                    if (ObjectUtils.isEmpty(job.getPriceBookId())) {
                        logger.warn("updateVendors: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                                   job.getId(),
                                   job.getJobNumber(),
                                   job.getOrganizationId());
                        ensurePriceBookId(job);
                    }
                    mongoTemplate.save(job, "jobs");
                }
            }
        }
    }

    public ResponseEntity saveReleaseNotes(HttpServletRequest request, ReleaseNotesResponse response) {
        String userRoles = getRoles(request);
        if (userRoles.contains(Role.ROLE_ADMIN.name())) {
            String userName = getUserName(request);

            // Check if any release notes already exist
            ReleaseNotesResponse existingResponse = mongoTemplate.findOne(new Query(), ReleaseNotesResponse.class);

            // Set createdBy only if it's empty
            if (StringUtils.isEmpty(response.getCreatedBy())) {
                response.setCreatedBy(userName);
                response.setModifiedBy(userName);
            }

            mongoTemplate.save(response);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User does not have sufficient privileges", HttpStatus.UNAUTHORIZED);
        }
    }



    public ResponseEntity<?> getReleaseNotes(HttpServletRequest request) {
        try {

//            PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "modified"));
            List<ReleaseNotesResponse> releaseNotesPage = releaseNotesMongoDbRepository.findAll();
//            List<ReleaseNotesResponse> releaseNotesList = releaseNotesPage.getContent();
            return new ResponseEntity<>(releaseNotesPage, HttpStatus.OK);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(RELEASE_NOTES_NOT_FOUND)
                    .errorMessage(ERROR_WHILE_GETTING_RELEASE_NOTES_DATA)
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity updateReleaseNotes(HttpServletRequest request, ReleaseNotesResponse response) {
        String userRoles = getRoles(request);
        if (userRoles.contains(Role.ROLE_ADMIN.name())) {
            Optional<ReleaseNotesResponse> notesResponse = releaseNotesMongoDbRepository.findById(response.getId());
            if (!ObjectUtils.isEmpty(notesResponse)) {
                String useName = getUserName(request);
                response.setModifiedBy(useName);
                response.setModified(new Date().getTime());
                releaseNotesMongoDbRepository.save(response);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Error error = Error.builder().errorCode(RELEASE_NOTES_NOT_FOUND).errorMessage(Constants.RELEASE_NOTES_NOT_FOUND_MESSAGE).build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new AccessDeniedException("access denied");
        }
    }


    public void deleteReleaseNotes(String id) {
        Optional<ReleaseNotesResponse> releaseNotesResponse = releaseNotesMongoDbRepository.findById(id);
        if (!releaseNotesResponse.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT FOUND");
        }
        releaseNotesMongoDbRepository.deleteById(id);
    }

    public JobItemCodeDiscounts saveJobItemCodeDiscounts(HttpServletRequest request, JobItemCodeDiscountsRequest jobItemCodeDiscountsRequest) {
        String jobId = jobItemCodeDiscountsRequest.getJobId();
        String organizationId = getOrganizationId(request);
        DiscountAuditDetails discountAuditDetails = new DiscountAuditDetails();
        discountAuditDetails = setDiscountAuditDetails(request, discountAuditDetails, jobItemCodeDiscountsRequest.getStageDiscountFor());
        JobItemCodeDiscounts jobItemCodeDiscounts = jobItemCodeDiscountsMongoDbRepository.findByOrganizationIdAndJobId(organizationId,jobId);
        if(ObjectUtils.isEmpty(jobItemCodeDiscounts)){
            jobItemCodeDiscounts = new JobItemCodeDiscounts();
        }
        ItemCodeDetails itemDetailsCurrent = jobItemCodeDiscounts.getItemDetailsCurrent();
        List<ItemCodeDetails> itemDetailsHistorical = jobItemCodeDiscounts.getItemDetailsHistorical();

        if(!ObjectUtils.isEmpty(itemDetailsCurrent)){
            if (ObjectUtils.isEmpty(itemDetailsHistorical)) {
                itemDetailsHistorical = new ArrayList<>();
                itemDetailsHistorical.add(itemDetailsCurrent);
            } else {
                itemDetailsHistorical.add(itemDetailsCurrent);
            }
        }
        Map<String, Double> roundedMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : jobItemCodeDiscountsRequest.getItemCodeMap().entrySet()) {
            roundedMap.put(entry.getKey(), Math.round(entry.getValue() * 10000.0) / 10000.0);
        }
        ItemCodeDetails newItemDetailsCurrent = new ItemCodeDetails();
        newItemDetailsCurrent.setItemCodeMap(roundedMap);
        newItemDetailsCurrent.setDiscountAuditDetails(discountAuditDetails);
        jobItemCodeDiscounts.setItemDetailsCurrent(newItemDetailsCurrent);
        jobItemCodeDiscounts.setJobId(jobId);
        jobItemCodeDiscounts.setOrganizationId(organizationId);
        return jobItemCodeDiscountsMongoDbRepository.save(jobItemCodeDiscounts);
    }

    public JobItemCodeDiscounts getJobItemCodeDiscounts(HttpServletRequest request, String jobId) {
        String organizationId = getOrganizationId(request);
        JobItemCodeDiscounts jobItemCodeDiscounts = jobItemCodeDiscountsMongoDbRepository.findByOrganizationIdAndJobId(organizationId,jobId);
        Map<String, Double> roundedMap = new HashMap<>();

        if(!ObjectUtils.isEmpty(jobItemCodeDiscounts)) {
            if(!ObjectUtils.isEmpty(jobItemCodeDiscounts.getItemDetailsCurrent())) {
                if(!ObjectUtils.isEmpty(jobItemCodeDiscounts.getItemDetailsCurrent().getItemCodeMap())) {
                    for (Map.Entry<String, Double> entry : jobItemCodeDiscounts.getItemDetailsCurrent().getItemCodeMap().entrySet()) {
                        roundedMap.put(entry.getKey(), Math.round(entry.getValue() * 10000.0) / 10000.0);
                    }
                    jobItemCodeDiscounts.getItemDetailsCurrent().setItemCodeMap(roundedMap);
                }
            }
        } else {
            jobItemCodeDiscounts = new JobItemCodeDiscounts();
            jobItemCodeDiscounts.setJobId(jobId);
            jobItemCodeDiscounts.setOrganizationId(organizationId);
            jobItemCodeDiscounts.setItemDetailsCurrent(new ItemCodeDetails());
            jobItemCodeDiscountsMongoDbRepository.save(jobItemCodeDiscounts);
        }
        return jobItemCodeDiscounts;
    }

    public ResponseEntity<Void> migrateDiscounts(HttpServletRequest request, String jobId) {
        List<JobItemCodeDiscounts> existingDiscountRecords = jobItemCodeDiscountsMongoDbRepository.findAll();
        Set<String> existingJobIds = existingDiscountRecords.stream()
                .map(JobItemCodeDiscounts::getJobId)
                .collect(Collectors.toSet());
        List<Job> jobList = ObjectUtils.isEmpty(jobId)
                ? jobMongoDbRepository.findByIdNotIn(existingJobIds)
                : jobMongoDbRepository.findByOrganizationIdAndId(getOrganizationId(request), jobId);
        jobList.forEach(this::saveJobItemCodeDiscounts);

        return ResponseEntity.ok().build();
    }

    private void saveJobItemCodeDiscounts(Job job) {
        JobItemCodeDiscounts jobItemCodeDiscounts = new JobItemCodeDiscounts();
        jobItemCodeDiscounts.setJobId(job.getId());
        jobItemCodeDiscounts.setOrganizationId(job.getOrganizationId());

        Map<String, Float> existingDiscounts = job.getDiscounts();
        Map<String, Double> itemEntries = new HashMap<>();
        if (!ObjectUtils.isEmpty(existingDiscounts)) {
            existingDiscounts.forEach((key, value) -> {
                if (key != null && value != null) {
                    itemEntries.put(key, value.doubleValue());
                }
            });
        }
        ItemCodeDetails itemCodeDetails = new ItemCodeDetails();
        itemCodeDetails.setItemCodeMap(itemEntries);
        jobItemCodeDiscounts.setItemDetailsCurrent(itemCodeDetails);

        jobItemCodeDiscountsMongoDbRepository.save(jobItemCodeDiscounts);
    }

    public ResponseEntity addIdBucketTest(HttpServletRequest request, String jobId) {
        List<Job> jobList = jobMongoDbRepository.findAll();
        jobList.forEach(entry -> {
            BucketTest bucketTest = entry.getBucketTest();
            List<BucketTestItem> testsList = bucketTest.getTests();
            if (!ObjectUtils.isEmpty(testsList)) {
                for (BucketTestItem bucketTestItem : testsList) {
                    if (ObjectUtils.isEmpty(bucketTestItem.getId())) {
                        UUID uId = UUID.randomUUID();
                        String uniqueBucketTestId = System.currentTimeMillis() + entry.getId() + uId;
                        bucketTestItem.setId(uniqueBucketTestId);
                    }
                }
            }
            bucketTest.setTests(testsList);
            entry.setBucketTest(bucketTest);
            jobMongoDbRepository.save(entry);
        });
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> migrateBucketTest(HttpServletRequest request, String jobId) {
        List<BucketTest> existingRecords = bucketTestsRepository.findAll();
        Set<String> existingJobIds = existingRecords.stream()
                .map(BucketTest::getJobId)
                .collect(Collectors.toSet());
        List<Job> jobList = ObjectUtils.isEmpty(jobId)
                ? jobMongoDbRepository.findByIdNotIn(existingJobIds)
                : jobMongoDbRepository.findByOrganizationIdAndId(getOrganizationId(request), jobId);
        jobList.forEach(this::saveBucketTest);
        return ResponseEntity.ok().build();
    }

    private void saveBucketTest(Job job) {
        BucketTest bucketMigrationTest = new BucketTest();
        if (job.getBucketTest() != null) {
            bucketMigrationTest.setJobId(job.getId());
            bucketMigrationTest.setOrganizationId(job.getOrganizationId());
            bucketMigrationTest.setEngineers(job.getBucketTest().getEngineers());
            bucketMigrationTest.setSupervisors(job.getBucketTest().getSupervisors());
            bucketMigrationTest.setConsultants(job.getBucketTest().getConsultants());
            bucketMigrationTest.setTests(job.getBucketTest().getTests());
        }
        bucketTestsRepository.save(bucketMigrationTest);
    }

    public BucketTest updateBucketTests(HttpServletRequest request, BucketOperationEnum operation, String jobId, String testItemId, BucketTestItem bucketTestItem) {
        try {
            BucketTest bucketTest = bucketTestsRepository.findByJobIdAndOrganizationId(jobId,getOrganizationId(request));
            if (ObjectUtils.isEmpty(bucketTest)) {
                throw new ErrorException(
                        Error.builder().errorCode(ErrorConstants.BUCKET_TEST_NOT_FOUND_ERROR_CODE).errorMessage(ErrorConstants.BUCKET_TEST_NOT_FOUND_ERROR_MESSAGE)
                                .httpStatus(HttpStatus.NOT_FOUND).build());
            }
            List<BucketTestItem> testsList = bucketTest.getTests();
            switch (operation) {
                case ADD:
                    if (!ObjectUtils.isEmpty(bucketTestItem)) {
                        UUID uId = UUID.randomUUID();
                        String uniqueBucketTestId = System.currentTimeMillis() + bucketTest.getId() + uId;
                        bucketTestItem.setId(uniqueBucketTestId);
                        testsList.add(bucketTestItem);
                    }
                    break;
                case UPDATE:
                    if (!ObjectUtils.isEmpty(testsList) && !ObjectUtils.isEmpty(testItemId) && !ObjectUtils.isEmpty(bucketTestItem)) {
                        testsList.replaceAll(item -> testItemId.equals(item.getId()) ? bucketTestItem : item);
                    }
                    break;
                case DELETE:
                    if (!ObjectUtils.isEmpty(testsList) && !ObjectUtils.isEmpty(testItemId)) {
                        testsList.removeIf(item -> testItemId.equals(item.getId()));
                    }
                    break;
            }
            bucketTest.setTests(testsList);
            bucketTestsRepository.save(bucketTest);
            return bucketTest;
        }
        catch (ErrorException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ErrorException(
                    Error.builder().errorCode(ErrorConstants.BUCKET_TEST_ERROR_CODE).errorMessage(ErrorConstants.BUCKET_TEST_ERROR_MESSAGE)
                            .httpStatus(HttpStatus.BAD_REQUEST).build());
        }
    }

    public BucketTest getBucketTests(HttpServletRequest request, String jobId) {
        try {
            Boolean isPresent = jobMongoDbRepository.existsById(jobId);
            if (isPresent) {
                BucketTest bucketTest = bucketTestsRepository.findByJobIdAndOrganizationId(jobId, getOrganizationId(request));
                if (ObjectUtils.isEmpty(bucketTest)) {
                    BucketTest bucketTest1 = new BucketTest();
                    bucketTest1.setJobId(jobId);
                    bucketTest1.setOrganizationId(getOrganizationId(request));
                    bucketTestsRepository.save(bucketTest1);
                    return bucketTest1;
                }
                return bucketTest;
            } else {
                throw new ErrorException(Error.builder().errorCode(JOB_NOT_FOUND_CODE).errorMessage(JOB_NOT_FOUND_MESSAGE).httpStatus(HttpStatus.NOT_FOUND).build());
            }
        }
        catch (ErrorException e){
            throw e;
        }
        catch (Exception e){
            throw new ErrorException(
                    Error.builder().errorCode(ErrorConstants.BUCKET_TEST_ERROR_CODE).errorMessage(ErrorConstants.BUCKET_TEST_ERROR_MESSAGE)
                            .httpStatus(HttpStatus.BAD_REQUEST).build());
        }

    }

    public BucketTest populateBucketTest(HttpServletRequest request, String jobId, BucketTest bucketTest) {
        try {
            BucketTest dbBucketTest = bucketTestsRepository.findByJobIdAndOrganizationId(jobId, getOrganizationId(request));
            if (!ObjectUtils.isEmpty(bucketTest)) {
                dbBucketTest.setEngineers(bucketTest.getEngineers());
                dbBucketTest.setSupervisors(bucketTest.getSupervisors());
                dbBucketTest.setConsultants(bucketTest.getConsultants());
            }
            bucketTestsRepository.save(dbBucketTest);
            return dbBucketTest;
        } catch (Exception e) {
            throw new ErrorException(
                    Error.builder().errorCode(ErrorConstants.BUCKET_TEST_ERROR_CODE).errorMessage(ErrorConstants.BUCKET_TEST_ERROR_MESSAGE)
                            .httpStatus(HttpStatus.BAD_REQUEST).build());
        }
    }

    public Boolean existsByOrganizationIdAndJobNumber(String organizationId, String jobNumber) {
        return jobMongoDbRepository.existsByOrganizationIdAndJobNumber(organizationId,jobNumber);
    }

    public List<Job> updateSharedWithOrganizationId(String sharedWithOrganizationIdToLink, String curOrganizationId, String operatorName, boolean isShared) {
        Criteria criteria = Criteria.where(Constants.ORGANIZATION_ID).is(curOrganizationId)
                .and(Constants.OPERATOR_SHARED_WITH_ORG).is(operatorName);
        Query query = new Query(criteria);
        List<Job> jobs = mongoTemplate.find(query, Job.class);
        String sharedWithOrganizationId = isShared ? sharedWithOrganizationIdToLink : null;
        jobs.forEach(job -> {
            job.setSharedWithOrganizationId(sharedWithOrganizationId);
            job.setTs(System.currentTimeMillis());

            // Check if priceBookId is null before saving (from updateSharedWithOrganizationId method)
            if (ObjectUtils.isEmpty(job.getPriceBookId())) {
                logger.warn("updateSharedWithOrganizationId: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                           job.getId(),
                           job.getJobNumber(),
                           job.getOrganizationId());
                ensurePriceBookId(job);
            }

            mongoTemplate.save(job);
        });
        return jobs;
    }

    public ResponseEntity personalJob(String organizationId, PersonalJob personalJob, boolean isUpdate) {
        try {
            if (isUpdate) {
                Optional<Job> jobOptional = jobMongoDbRepository.findByIdAndOrganizationId(personalJob.getJobId(), organizationId);
                if (jobOptional.isPresent()) {
                    Job job = jobOptional.get();
                    job.setUsers(personalJob.getUserList());
                    jobMongoDbRepository.save(job);

                    // Check if priceBookId is null before saving (from personalJob method)
                    if (ObjectUtils.isEmpty(job.getPriceBookId())) {
                        logger.warn("personalJob: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                                   job.getId(),
                                   job.getJobNumber(),
                                   job.getOrganizationId());
                        ensurePriceBookId(job);
                    }

                    jobMongoDbRepository.save(job);
                    return new ResponseEntity<>(personalJob, HttpStatus.OK);
                } else {
                    Error error = Error.builder().errorCode(JOB_NOT_FOUND_CODE).errorMessage(JOB_NOT_FOUND_MESSAGE)
                            .httpStatus(HttpStatus.BAD_REQUEST).build();
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
            } else {
                List<Job> jobList = jobMongoDbRepository.findByOrganizationId(organizationId);
                List<PersonalJob> personalJobList = new ArrayList<>();
                for (Job jobs : jobList) {
                    PersonalJob job = new PersonalJob();
                    job.setJobId(jobs.getId());
                    job.setUserList(jobs.getUsers());
                    personalJobList.add(job);
                }
                return new ResponseEntity<>(personalJobList, HttpStatus.OK);
            }
        }catch (Exception e){
            Error error;
            if (isUpdate) {
                // Handle update issue
                error = Error.builder()
                        .errorCode(UPDATE_ISSUE)
                        .errorMessage(UPDATE_ISSUE_MESSAGE)
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            } else {
                // Handle general get issue
                error = Error.builder()
                        .errorCode(GET_ISSUE)
                        .errorMessage(GET_ISSUE_MESSAGE)
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Map<String, List<String>>> updateSharedWithOrganizationIdInAllJobs() {
        try {
            Map<String, List<String>> modifiedJobsMap = new HashMap<>();
            List<Operator> filteredOperators = operatorMongoDbRepository.findOperatorsWithLinkedOrganization();
            if (!ObjectUtils.isEmpty(filteredOperators)) {
                Map<String, List<Operator>> operatorMap = filteredOperators.stream()
                        .collect(Collectors.groupingBy(Operator::getOrganizationId));

                Set<String> operatorNames = filteredOperators.stream().map(Operator::getName).collect(Collectors.toSet());

                Set<String> organizationIds = filteredOperators.stream().map(Operator::getOrganizationId).collect(Collectors.toSet());

                List<Job> allJobsForOperator = jobMongoDbRepository.findByOperatorInAndOrganizationIdInAndSharedWithOrganizationId(operatorNames, organizationIds, null);
                if (!ObjectUtils.isEmpty(allJobsForOperator)) {
                    Map<String, List<Job>> jobMap = allJobsForOperator.stream()
                            .collect(Collectors.groupingBy(Job::getOrganizationId));
                    for (String organizationId : organizationIds) {
                        List<Operator> operators = operatorMap.get(organizationId);
                        List<Job> jobs = jobMap.get(organizationId);

                        if (!ObjectUtils.isEmpty(jobs) && !ObjectUtils.isEmpty(operators)) {
                            Map<String, List<Job>> jobsForOperatorMap = jobs.stream()
                                    .collect(Collectors.groupingBy(Job::getOperator));
                            List<String> modifiedJobNumbers = new ArrayList<>();

                            for (Operator operator : operators) {
                                List<Job> jobsForOperator = jobsForOperatorMap.get(operator.getName());

                                if (!ObjectUtils.isEmpty(jobsForOperator)) {
                                    jobsForOperator.forEach(job -> {
                                        job.setSharedWithOrganizationId(operator.getLinkedOrganizationId());
                                        job.setTs(System.currentTimeMillis());

                                        // Check if priceBookId is null before saving (from updateSharedWithOrganizationIdInAllJobs method)
                                        if (ObjectUtils.isEmpty(job.getPriceBookId())) {
                                            logger.warn("updateSharedWithOrganizationIdInAllJobs: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                                                       job.getId(),
                                                       job.getJobNumber(),
                                                       job.getOrganizationId());
                                            ensurePriceBookId(job);
                                        }

                                        mongoTemplate.save(job);
                                        modifiedJobNumbers.add(job.getJobNumber());
                                    });
                                }
                            }

                            if (!ObjectUtils.isEmpty(modifiedJobNumbers)) {
                                modifiedJobsMap.put(organizationId, modifiedJobNumbers);
                            }
                        }
                    }
                } else {
                    log.info("job not found");
                }
                return ResponseEntity.ok(modifiedJobsMap);
            } else {
                return ResponseEntity.ok(modifiedJobsMap);
            }

        } catch (Exception e) {
            throw new ErrorException(
                    Error.builder().errorCode(ErrorConstants.UNABLE_TO_SET_SHAREWITHORGANIZATIONID_ERROR_CODE)
                            .errorMessage(ErrorConstants.UNABLE_TO_SET_SHAREWITHORGANIZATIONID_ERROR_MESSAGE)
                            .httpStatus(HttpStatus.BAD_REQUEST).build());
        }
    }

    public void updateJobNumberInLatestInsights(Job job) {
        String jobNumber = job.getJobNumber();
        String organizationId = job.getOrganizationId();

        // Collect all OnSiteEquipment lists in Job
        List<List<OnSiteEquipment>> allEquipments = Arrays.asList(
                job.getBlenders(),
                job.getEPumps(),
                job.getAuxTrailers(),
                job.getBoostPumps(),
                job.getCables(),
                job.getChemicalFloats(),
                job.getFrackLocks(),
                job.getIronFloats(),
                job.getMonoLines(),
                job.getNaturalGasTrailers(),
                job.getSwitchGears(),
                job.getTractors(),
                job.getHydrationUnits(),
                job.getPumps(),
                job.getChemAds(),
                job.getIronManifolds(),
                job.getDataVans(),
                job.getSilos(),
                job.getPopOffs(),
                job.getCentipedes(),
                job.getTransformers(),
                job.getTurbines(),
                job.getPowerGenerations(),
                job.getReciprocatingGasFuels(),
                job.getFrackLocks(),
                job.getNaturalGasDistributionTrailers(),
                job.getNaturalGasEmergencyStopTrailers(),
                job.getNaturalGasHeaters(),
                job.getNaturalGasJTSkids(),
                job.getDiverterSkids(),
                job.getTransports(),
                job.getRockCatchers(),
                job.getWaterMonitoringSystems(),
                job.getBodyLoadPumps(),
                job.getMeteringSkids()
        );

        allEquipments.stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(eq -> {
                    Query query = new Query();
                    Criteria criteria = new Criteria().andOperator(
                            Criteria.where("fleetName").is(job.getFleet()),
                            Criteria.where("equipmentId").is(eq.getId()),
                            Criteria.where("organizationId").is(organizationId),
                            new Criteria().orOperator(
                                    Criteria.where("jobId").is(null),
                                    Criteria.where("jobId").is(job.getId())
                            )
                    );
                    query.addCriteria(criteria);
                    query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
                    query.limit(1);

                    EquipmentInsights latest = mongoTemplate.findOne(query, EquipmentInsights.class, "equipments-insights");

                    if (latest != null && (ObjectUtils.isEmpty(latest.getJobId()) || latest.getJobId().equals(job.getId()))) {
                        Update update = new Update().set("jobNumber", jobNumber);
                        update.set("jobId",job.getId());
                        // Job start date: prefer startDate, else use rts
                        Long resolvedStartDate = (job.getStartDate() != null) ? job.getStartDate() : job.getRts();
                        update.set("jobStartDate", resolvedStartDate);

                        // Job end date: prefer endDate, else use expectedEndDate
                        Long resolvedEndDate = (job.getEndDate() != null)
                                ? job.getEndDate()
                                : job.getExpectedEndDate();

                        // Always set jobEndDate with fallback
                        update.set("jobEndDate", resolvedEndDate);
                        update.set("jobStatus", job.getStatus());
                        Query idQuery = new Query(Criteria.where("_id").is(latest.getId()));
                        mongoTemplate.updateFirst(idQuery, update, "equipments-insights");
                    }
                });
    }

    public Job updateCalendarJob(JobUpdateRequest request,HttpServletRequest requestInfo) {
        // Fetch job by ID
        Job job = jobMongoDbRepository.findById(request.getJobId())
                                      .orElseThrow(() -> new NoSuchElementException("Job not found"));

        //  Check if status is "Scheduled"
        if (!SCHEDULED.equalsIgnoreCase(job.getStatus())) {
            throw new IllegalStateException("Job cannot be edited because it is already in progress");
        }

        //  Fetch fleets
        Fleet oldFleet = fleetMongoDbRepository.findById(request.getOldFleetId())
                                               .orElseThrow(() -> new NoSuchElementException("Old Fleet not found"));
        Fleet newFleet = fleetMongoDbRepository.findById(request.getNewFleetId())
                                               .orElseThrow(() -> new NoSuchElementException("New Fleet not found"));

        //  Validate oldFleet name with job.fleet
        if (!Objects.equals(job.getFleet(), oldFleet.getName())) {
            throw new IllegalArgumentException("Old fleet does not match current job fleet");
        }

        long today = System.currentTimeMillis(); // current timestamp

        Long start = request.getStartDate();
        Long end = request.getEndDate();

        if (start != null && end != null) {
            if (today >= start && today <= end) {
                job.setStatus(Constants.IN_PROGRESS);   // Job ongoing
            } else if (today > end) {
                job.setStatus(Constants.COMPLETED);     // Job finished
            } else {
                job.setStatus(Constants.SCHEDULED);   // Job yet to begin
            }
        } else {
            job.setStatus(Constants.SCHEDULED); // default / fallback
        }

        //  Update job.fleet with newFleet name
        job.setFleet(newFleet.getName());

        //  Update dates
        if (request.getStartDate() != null)
            job.setRts(request.getStartDate());
        if (request.getEndDate() != null)
            job.setExpectedEndDate(request.getEndDate());

        // Check if priceBookId is null before saving (from updateCalendarJob method)
        if (ObjectUtils.isEmpty(job.getPriceBookId())) {
            logger.warn("updateCalendarJob: Job being updated with NULL priceBookId. JobId: {}, JobNumber: {}, OrganizationId: {}. Setting default pricebook.",
                       job.getId(),
                       job.getJobNumber(),
                       job.getOrganizationId());
            ensurePriceBookId(job);
        }

        //add audit log for jobs scheduled.
        saveJobInsights(job, requestInfo);
        //  Save and return updated job
        return jobMongoDbRepository.save(job);
    }

    public JobInsights saveJobInsights(Job job, HttpServletRequest request) {
        String userId = getUserId(request);
        Fleet fleet = null;
        if (!ObjectUtils.isEmpty(job.getFleet())) {
            fleet = fleetMongoDbRepository.findByOrganizationIdAndName(job.getOrganizationId(), job.getFleet());
        }
        // Build the latest job insights from the current job data
        JobInsights latestJobInsights = JobInsights.builder()
                .jobId(job.getId())
                .jobNumber(job.getJobNumber())
                .endDate(job.getExpectedEndDate())
                .startDate(job.getRts())
                .organizationId(job.getOrganizationId())
                .fleetId(fleet != null ? fleet.getId() : null)
                .created(new Date().getTime())
                .modified(new Date().getTime())
                .modifiedBy(userId)
                .build();

        // Check if a record already exists
        Optional<JobInsights> existingInsightOpt =
                jobInsightsMongoDbRepository.findTopByJobIdAndOrganizationIdOrderByModifiedDesc(job.getId(), job.getOrganizationId());

        if (existingInsightOpt.isPresent()) {
            // Record exists - compare and set modified fields
            JobInsights existingJobInsight = existingInsightOpt.get();

            // Extract modified fields
            extractModifiedFields(existingJobInsight, latestJobInsights);

            // Preserve the original creation timestamp
            latestJobInsights.setCreated(existingJobInsight.getCreated());
            latestJobInsights.setModified(new Date().getTime());
        } else {
            // New record - modifiedFields remains null/empty
            latestJobInsights.setModifiedFields(new ArrayList<>());
        }

        return jobInsightsMongoDbRepository.save(latestJobInsights);
    }

    public void extractModifiedFields(JobInsights existingInsights, JobInsights latestInsights) {
        List<String> modifiedFields = new ArrayList<>();

        if (!Objects.equals(existingInsights.getFleetId(), latestInsights.getFleetId())) {
            modifiedFields.add("fleetId");
        }

        if (!Objects.equals(existingInsights.getStartDate(), latestInsights.getStartDate())) {
            modifiedFields.add("startDate");
        }

        if (!Objects.equals(existingInsights.getEndDate(), latestInsights.getEndDate())) {
            modifiedFields.add("endDate");
        }
        latestInsights.setModifiedFields(modifiedFields);
    }

    public Map<String, List<JobInsightsDTO>> getAllJobInsights(HttpServletRequest request,
                                                               GlobalFilterCalendar globalFilterCalendar) {
        String organizationId = getOrganizationId(request);

        // Get all jobIds from insights collection
        List<JobInsights> insights = jobInsightsMongoDbRepository.findAllByOrganizationId(organizationId);

        if (insights.isEmpty()) {
            return Collections.emptyMap();
        }

        // Fetch users map
        List<String> insightsUsers = insights.stream()
                .map(JobInsights::getModifiedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, String> users = userMongoDbRepository.findByIdIn(insightsUsers)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUserName));

        // Build job IDs for aggregation
        List<String> objectIds = insights.stream()
                .map(JobInsights::getJobId)
                .collect(Collectors.toList());

        // Build aggregation pipeline
        List<AggregationOperation> operations = new ArrayList<>();

        // Base match criteria
        Criteria baseCriteria = Criteria.where("organizationId").is(organizationId)
                .and("_id").in(objectIds);

        // Add dynamic filters
        List<String> districts = globalFilterCalendar.getDistricts();
        List<String> jobStatus = globalFilterCalendar.getJobStatus();

        if (jobStatus != null && !jobStatus.isEmpty()) {
            List<String> statusList = jobStatus.stream()
                    .map(status -> {
                        try {
                            Field field = Constants.class.getField(status);
                            return (String) field.get(null);
                        } catch (Exception ex) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            baseCriteria.and("status").in(statusList);
        }

        if (districts != null && !districts.isEmpty()) {
            baseCriteria.and("districtId").in(districts);
        }

        // Add match and projection operations
        operations.add(Aggregation.match(baseCriteria));
        operations.add(Aggregation.project()
                .and("_id").as("id")
                .and("jobNumber").as("jobNumber")
                .and("serviceCompany").as("serviceCompany")
                .and("districtId").as("districtId")
                .and("fleet").as("fleet")
                .and("organizationId").as("organizationId")
                .and("operator").as("operator")
        );

        // Execute aggregation
        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<JobInsightsObject> results = mongoTemplate.aggregate(
                aggregation,
                "jobs",
                JobInsightsObject.class
        );

        Map<String, JobInsightsObject> jobMap = results.getMappedResults().stream()
                .collect(Collectors.toMap(JobInsightsObject::getId, job -> job));
        Map<String, String> fleetNamesMap = getFleetNamesMap(insights);
        // Fetch district map if needed
//        Map<String, String> districtMap = new HashMap<>();
//        if (districts != null && !districts.isEmpty()) {
//            districtMap = districtMongoDbRepository.findAllById(districts)
//                    .stream()
//                    .collect(Collectors.toMap(District::getId, District::getName));
//        }

        // Build result list
        List<JobInsightsDTO> jobInsightsList = new ArrayList<>();
        for (JobInsights insight : insights) {
            JobInsightsObject job = jobMap.get(insight.getJobId());

            if (job == null) {
                continue;
            }

//            String districtName = districtMap.getOrDefault(job.getDistrictId(), null);
            String userName = users.getOrDefault(insight.getModifiedBy(), null);
            String fleetName = fleetNamesMap.getOrDefault(insight.getFleetId(), null); // Use from map

            JobInsightsDTO jobInsightsDTO = JobInsightsDTO.builder()
                    .jobId(insight.getJobId())
                    .jobNumber(job.getJobNumber())
                    .company(job.getOperator())
                    .fleetName(fleetName)
                    .fleetId(insight.getFleetId())
                    .modifiedBy(userName)
                    .modified(insight.getModified())
                    .created(insight.getCreated())
                    .startDate(insight.getStartDate())
                    .endDate(insight.getEndDate())
                    .organizationId(job.getOrganizationId())
                    .modifiedFields(insight.getModifiedFields())
                    .districtId(job.getDistrictId())
                    .build();

            jobInsightsList.add(jobInsightsDTO);
        }
        Map<String, List<JobInsightsDTO>> groupedByJobNumber = jobInsightsList.stream()
                .collect(Collectors.groupingBy(JobInsightsDTO::getJobNumber));
        // Group by jobNumber
        groupedByJobNumber.forEach((jobNumber, dtoList) ->
                dtoList.sort(Comparator.comparing(JobInsightsDTO::getModified).reversed())
        );

        return groupedByJobNumber;
    }

    private Map<String, String> getFleetNamesMap(List<JobInsights> insights) {
        // Extract unique fleet IDs from insights
        List<String> fleetIds = insights.stream()
                .map(JobInsights::getFleetId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Return empty map if no fleet IDs found
        if (fleetIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // Fetch fleets from repository and map ID to name
        return fleetMongoDbRepository.findAllById(fleetIds)
                .stream()
                .collect(Collectors.toMap(
                        Fleet::getId,
                        Fleet::getName,
                        (existing, replacement) -> existing // Handle duplicates if any
                ));
    }


    public List<Job> findBySharedWithOrganizationIdAndDistrictIdsAndStatusIn(String organizationId, List<String> districtIds, Set<String> status) {
        return jobMongoDbRepository.findBySharedWithOrganizationIdAndDistrictIdInAndStatusIn(organizationId, districtIds, status);
    }

    public List<Job> findByOrganizationIdAndDistrictIdsAndStatusIn(String organizationId, List<String> districtIds, Set<String> status) {
        return jobMongoDbRepository.findByOrganizationIdAndDistrictIdInAndStatusIn(organizationId, districtIds, status);
    }

    public ResponseEntity getOperatorJobPanel(HttpServletRequest request) {
        try{
            String organizationId = getOrganizationId(request);
            ZoneId zoneId = resolveTimeZone(request);
            String userRoles = getRoles(request);
            String username = getUserName(request);
            List<ServiceCompanyGroup> serviceCompanyGroups = new ArrayList<>();
            List<Job> jobs = jobMongoDbRepository
                    .findBySharedWithOrganizationIdAndStatus(organizationId, "In Progress");

            if (!ObjectUtils.isEmpty(jobs)) {
                Set<String> fleet = jobs.stream().map(Job::getFleet).collect(Collectors.toSet());
                Set<String> organizationIds = jobs.stream().map(Job::getOrganizationId).collect(Collectors.toSet());
                Query query = new Query();
                query.addCriteria(Criteria.where("name").in(fleet).and(ORGANIZATION_ID).in(organizationIds));
                List<Fleet> fleetType  = mongoTemplate.find(query, Fleet.class);
                Map<String, Fleet> fleetMap = Optional.ofNullable(fleetType)
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(f -> f.getName() != null && f.getOrganizationId() != null)
                        .collect(Collectors.toMap(
                                f -> f.getName() + "_" + f.getOrganizationId(), // key
                                Function.identity(),
                                (existing, replacement) -> existing            // handle duplicates
                        ));
                List<Organization> organizations = organizationMongoDbRepository.findAllById(organizationIds);
                Map<String, Organization> organizationMap = organizations.stream()
                        .collect(Collectors.toMap(
                                Organization::getId,
                                org -> org
                        ));
                List<String> jobIds = jobs.stream().map(Job::getId).collect(Collectors.toList());
                List<ActivityLogEntry> activityLogEntries = activityLogMongoDbRepository.findByJobIdIn(jobIds);

                Map<String, List<ActivityLogEntry>> mapOfActivityLogEntriesInJob = new HashMap<>();

                if(!ObjectUtils.isEmpty(activityLogEntries)){
                    mapOfActivityLogEntriesInJob = activityLogEntries.stream().collect(Collectors.groupingBy(ActivityLogEntry::getJobId));
                }
                Map<String, List<Job>> mapOfJobsInOrganization = jobs.stream()
                        .collect(Collectors.groupingBy(Job::getOrganizationId));

                for(Map.Entry<String, List<Job>> jobsEntry : mapOfJobsInOrganization.entrySet()){
                    if (!organizationMap.containsKey(jobsEntry.getKey())) {
                        continue;
                    }
                    String serviceCompany = organizationMap.get(jobsEntry.getKey()).getName();
                    ServiceCompanyGroup serviceCompanyGroup = new ServiceCompanyGroup();
                    List<Job> jobList = jobsEntry.getValue();
                    serviceCompanyGroup.setServiceCompany(serviceCompany);
                    serviceCompanyGroup.setJobCount(jobList.size());
                    serviceCompanyGroup.setJobWidgets(jobWidgetsMapper(jobList, mapOfActivityLogEntriesInJob, zoneId, userRoles, username, organizationMap, fleetMap));



                    serviceCompanyGroups.add(serviceCompanyGroup);
                }
            }
            return new ResponseEntity<>(serviceCompanyGroups, HttpStatus.OK);
        } catch (Exception e) {
            throw new ErrorException(
                    Error.builder().errorCode(ErrorConstants.UNABLE_TO_GET_SHARED_JOB_ERROR_CODE)
                            .errorMessage(ErrorConstants.UNABLE_TO_GET_SHARED_JOB_ERROR_MESSAGE)
                            .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    private List<JobWidgets> jobWidgetsMapper(List<Job> jobs, Map<String, List<ActivityLogEntry>> mapOfActivityLogEntriesInJob, ZoneId zoneId, String userRoles, String userName, Map<String, Organization> organizationMap, Map<String, Fleet> fleetMap) {
        List<JobWidgets> jobWidgetsList = new ArrayList<>();
        for(Job job : jobs){
            boolean isAccess = true;
            if (userRoles.contains(Constants.ROLE_ADMIN) || userRoles.contains(Constants.ROLE_BACK_OFFICE) || userRoles.contains(
                    Constants.ROLE_CARBO_ADMIN) || userRoles.contains(Constants.ROLE_READ_ONLY)) {
                isAccess = true;
            } else if (userRoles.contains(Constants.ROLE_FIELD_USER) || userRoles.contains(Constants.ROLE_OPERATION)) {
                isAccess = false;
                List<User> jobUsers = job.getUsers(); // Assuming getUsers() returns the list of users for the current job
                for (User jobUser : jobUsers) {
                    if (jobUser.getUserName().equalsIgnoreCase(userName)) {
                        isAccess = true;
                        break; // No need to continue checking if the username is already found
                    }
                }
            }
            String fleetType = "";
            Fleet fleet = fleetMap.get(job.getFleet() + "_" + job.getOrganizationId());
            if(!ObjectUtils.isEmpty(fleet)){
                fleetType = fleet.getFleetType();
            }

//            total number of wells
            int numberOfWells = 0;
            if (!ObjectUtils.isEmpty(job.getWells())) {
                numberOfWells = job.getWells().size();
            }

            //Fetch Curr Well Name
            String wellname = null;
            for (Well curWellName : job.getWells()) {
                wellname = curWellName.getName();
            }
            int count = countDualFuelPumps(job);

            JobWidgets jobWidgets = new JobWidgets();
            List<ActivityLogEntry> activityLogEntries = mapOfActivityLogEntriesInJob.get(job.getId());
            jobWidgets.setJobId(job.getId());
            jobWidgets.setJobNumber(job.getJobNumber());
            jobWidgets.setPadName(job.getPad());
            jobWidgets.setFleet(job.getFleet());
            jobWidgets.setFleetType(fleetType);
            jobWidgets.setDualFuelPumpCount(count);
            jobWidgets.setIsAccess(isAccess);
            jobWidgets.setCurWellId(job.getCurWellId());
            jobWidgets.setCurrWellName(wellname);
            jobWidgets.setNumberOfWells(numberOfWells);
            jobWidgets.setCurStage(job.getCurStage());
            jobWidgets.setOperationsType(job.getOperationsType());
            jobWidgets.setCreated(job.getCreated());
            jobWidgets.setOrganizationId(job.getOrganizationId());
            jobWidgets.setOrganizationName(organizationMap.get(job.getOrganizationId()).getName());

            StagesCompleted stagesCompleted = new StagesCompleted();
            stagesCompleted.setTotalStages(job.getWells().stream()
                    .filter(Objects::nonNull)
                    .mapToInt(Well::getTotalStages)
                    .sum());
            double completedStages = 0;
            if(!ObjectUtils.isEmpty(activityLogEntries)) {
                completedStages = activityLogEntries.stream()
                        .filter(e -> e != null)
                        .filter(ActivityLogEntry::getComplete)
                        .filter(e -> e.getWell() != null && e.getStage() != null)
                        .map(e -> e.getWell() + "_" + e.getStage())
                        .distinct()
                        .count();
            }
            stagesCompleted.setCompletedStages(completedStages);

            jobWidgets.setStagesCompleted(stagesCompleted);

            jobWidgets.setStartedOn(job.getStartDate());
            jobWidgets.setExpectedCompletionBy(job.getExpectedEndDate());

            jobWidgets.setNptBreakdown(nptPieChartMapping(activityLogEntries));

            jobWidgets.setActivityBreakdown(activityBreakdownGraph(activityLogEntries, zoneId, job.getStartDate()));

            jobWidgets.setActivityBreakdownTable(activityBreakdownTableMapping(activityLogEntries));
            jobWidgetsList.add(jobWidgets);
        }
        return jobWidgetsList;
    }

    private Map<String, NptHours> nptPieChartMapping(
            List<ActivityLogEntry> activityLogEntries) {

        Map<String, NptHours> nptHoursMap = new HashMap<>();

        if (ObjectUtils.isEmpty(activityLogEntries)) {
            return nptHoursMap;
        }

        // Filter NPT entries (identified by name)
        List<ActivityLogEntry> nptEntries = activityLogEntries.stream()
                .filter(e ->
                        e != null &&
                                StringUtils.hasText(e.getOpsActivity()) &&
                                e.getOpsActivity().toUpperCase().contains("NPT")
                ).collect(Collectors.toList());

        if (nptEntries.isEmpty()) {
            return nptHoursMap;
        }

        long totalNptMillis = nptEntries.stream()
                .mapToLong(ActivityLogEntry::getMillisecondsSpan)
                .sum();

        if (totalNptMillis == 0) {
            return nptHoursMap;
        }

        Map<String, List<ActivityLogEntry>> activityLogNptMap =
                nptEntries.stream()
                        .collect(Collectors.groupingBy(ActivityLogEntry::getOpsActivity));

        for (Map.Entry<String, List<ActivityLogEntry>> entry : activityLogNptMap.entrySet()) {
            String opsActivity = entry.getKey();

            long activityMillis = entry.getValue().stream()
                    .mapToLong(ActivityLogEntry::getMillisecondsSpan)
                    .sum();

            String formattedTime = formatMillisToHHmm(activityMillis);

            double percentage = ((double) activityMillis / totalNptMillis) * 100;

            NptHours nptHours = new NptHours();
            nptHours.setHours(formattedTime);
            nptHours.setPercentage(round(percentage,2));

            nptHoursMap.put(opsActivity, nptHours);
        }

        return nptHoursMap;
    }

    private List<ActivityBreakdown> activityBreakdownGraph(List<ActivityLogEntry> activityLogEntries, ZoneId zoneId, Long jobStartDate) {
        List<ActivityBreakdown> activityBreakdownList = new ArrayList<>();

        if(ObjectUtils.isEmpty(activityLogEntries)){
            return activityBreakdownList;
        }
        // Determine max day (from filtered entries only)
        int maxDay = activityLogEntries.stream()
                .mapToInt(ActivityLogEntry::getDay)
                .max()
                .orElse(0);

        Map<Integer, List<ActivityLogEntry>> activityLogPerDayMap = activityLogEntries.stream().collect(Collectors.groupingBy(ActivityLogEntry::getDay));

        LocalDate startDate = Instant.ofEpochMilli(jobStartDate)
                .atZone(zoneId)
                .toLocalDate();

        for (int day = 1; day <= maxDay; day++) {
            LocalDate currentDate = startDate.plusDays(day - 1);
            List<ActivityLogEntry> activityLogEntryList = activityLogPerDayMap.get(day);
            ActivityBreakdown activityBreakdown = new ActivityBreakdown();
            activityBreakdown.setDay(day);
            activityBreakdown.setDate(currentDate.toString());
            activityBreakdown.setActivityTime(activityAndTimeMapping(activityLogEntryList));

            activityBreakdownList.add(activityBreakdown);
        }


        return activityBreakdownList;
    }

    private Map<String, Double> activityAndTimeMapping(List<ActivityLogEntry> activityLogEntryList) {
        Map<String, Double> activityTimeMap = new HashMap<>();
        Map<String, List<ActivityLogEntry>> activityLogNptMap =
                activityLogEntryList.stream()
                        .collect(Collectors.groupingBy(ActivityLogEntry::getOpsActivity));

        for (Map.Entry<String, List<ActivityLogEntry>> entry : activityLogNptMap.entrySet()) {
            String opsActivity = entry.getKey();

            long activityMillis = entry.getValue().stream()
                    .mapToLong(ActivityLogEntry::getMillisecondsSpan)
                    .sum();
            double activityHours = activityMillis / (1000.0 * 60 * 60);
            activityTimeMap.put(opsActivity, round(activityHours, 2));
        }
        return activityTimeMap;
    }

    private List<ActivityBreakdownTable> activityBreakdownTableMapping(List<ActivityLogEntry> activityLogEntries) {
        List<ActivityBreakdownTable> activityBreakdownTableList = new ArrayList<>();
        if(ObjectUtils.isEmpty(activityLogEntries)){
            return activityBreakdownTableList;
        }
        Map<String, List<ActivityLogEntry>> activityLogMap =
                activityLogEntries.stream()
                        .collect(Collectors.groupingBy(ActivityLogEntry::getOpsActivity));

        for(Map.Entry<String, List<ActivityLogEntry>> entry : activityLogMap.entrySet()){
            ActivityBreakdownTable activityBreakdownTable = new ActivityBreakdownTable();
            activityBreakdownTable.setCategoryName(entry.getKey());
            List<ActivityLogEntry> activityLogEntryList = entry.getValue();
            long activityMillis = activityLogEntryList.stream()
                    .mapToLong(ActivityLogEntry::getMillisecondsSpan)
                    .sum();

            double activityHours = activityMillis / (1000.0 * 60 * 60);

            activityBreakdownTable.setTotalTime(round(activityHours, 2));

            activityBreakdownTable.setSubCategories(mapSubCategories(activityLogEntryList));

            activityBreakdownTableList.add(activityBreakdownTable);
        }


        return activityBreakdownTableList;
    }

    private List<ActivityBreakdownSubCategory> mapSubCategories(List<ActivityLogEntry> activityLogEntryList) {
        List<ActivityBreakdownSubCategory> activityBreakdownSubCategoryList = new ArrayList<>();
        if(ObjectUtils.isEmpty(activityLogEntryList)){
            return activityBreakdownSubCategoryList;
        }
        Map<String, List<ActivityLogEntry>> subCategoryActivityLogMap = activityLogEntryList.stream()
                .collect(Collectors.groupingBy(ActivityLogEntry::getEventOrNptCode));

        for(Map.Entry<String, List<ActivityLogEntry>> entry : subCategoryActivityLogMap.entrySet()) {
            ActivityBreakdownSubCategory activityBreakdownSubCategory = new ActivityBreakdownSubCategory();
            activityBreakdownSubCategory.setActivityName(entry.getKey());
            List<ActivityLogEntry> activityLogEntries = entry.getValue();
            long activityMillis = activityLogEntries.stream()
                    .mapToLong(ActivityLogEntry::getMillisecondsSpan)
                    .sum();

            double activityHours = activityMillis / (1000.0 * 60 * 60);
            activityBreakdownSubCategory.setTime(round(activityHours,2));
            activityBreakdownSubCategoryList.add(activityBreakdownSubCategory);
        }

        return activityBreakdownSubCategoryList;
     }

    private String formatMillisToHHmm(long millis) {
        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    private ZoneId resolveTimeZone(HttpServletRequest request) {
        String tz = request.getHeader("Time-Zone");
        if (StringUtils.hasText(tz)) {
            try {
                return ZoneId.of(tz);
            } catch (Exception e) {
                return ZoneId.of("UTC");
            }
        }
        return ZoneId.of("UTC");
    }

}