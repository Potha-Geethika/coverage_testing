// ===== Imported from: com.carbo.job.repository.DailyJobRecordMongoDbRepository =====
package com.carbo.job.repository;

import com.carbo.job.model.analytics.DailyJobRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DailyJobRecordMongoDbRepository extends MongoRepository<DailyJobRecord, String> {
    List<DailyJobRecord> findByOrganizationId(String organizationId);
    Page<DailyJobRecord> findByOrganizationId(String organizationId, Pageable pageable);
    Page<DailyJobRecord> findByJobIdIn(Set<String> jobId, Pageable pageable);

//    List<DailyJobRecord> findBySharedOrganizationId(String organizationId);
    Page<DailyJobRecord> findBySharedOrganizationId(String organizationId, Pageable pageable);

    List<DailyJobRecord> findByJobIdAndDate(String jobId, Date date);
    Optional<DailyJobRecord> findByJobId(String jobId);
}

// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RequestMethod
// Unresolved import (framework/JDK): org.slf4j.LoggerFactory
// Unresolved import (framework/JDK): java.util.stream.Collectors
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RequestParam
// Unresolved import (framework/JDK): org.springframework.data.mongodb.core.MongoTemplate
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RequestMapping
// Unresolved import (framework/JDK): org.springframework.data.mongodb.core.query.Query
// Unresolved import (framework/JDK): java.util.*
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RestController
// Unresolved import (framework/JDK): jakarta.servlet.http.HttpServletRequest
// ===== Imported from: com.carbo.job.services.DailyJobRecordService =====
package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.analytics.DailyJobRecord;
import com.carbo.job.repository.DailyJobRecordMongoDbRepository;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DailyJobRecordService {
    private final DailyJobRecordMongoDbRepository dailyJobRecordMongoDbRepository;
    private final MongoTemplate mongoTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DailyJobRecordService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;

    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;
    @Autowired
    public DailyJobRecordService(DailyJobRecordMongoDbRepository dailyJobRecordMongoDbRepository, MongoTemplate mongoTemplate) {
        this.dailyJobRecordMongoDbRepository = dailyJobRecordMongoDbRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<DailyJobRecord> getByOrganizationId(HttpServletRequest request, String organizationId, int offset, int limit) {
        //return dailyJobRecordMongoDbRepository.findByOrganizationId(organizationId);
        return getByOrganizationIdFromService(request, organizationId, offset, limit);
    }

    public List<DailyJobRecord> getBySharedOrganizationId(HttpServletRequest request, String organizationId, int offset, int limit) {
        //return dailyJobRecordMongoDbRepository.findBySharedOrganizationId(organizationId);
        return getByShareOrganizationIdFromService(request, organizationId, offset, limit);
    }

    public List<DailyJobRecord> getByJobIdAndDate(String jobId, LocalDate date) {
        return dailyJobRecordMongoDbRepository.findByJobIdAndDate(jobId, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public DailyJobRecord saveDailyJobRecord(DailyJobRecord record) {
        return dailyJobRecordMongoDbRepository.save(record);
    }

    public List<DailyJobRecord> getByOrganizationIdFromService (HttpServletRequest request, String organizationId, int offset, int limit) {

        logger.info("<-------------Invoked DailyJobRecordService :: getByOrganizationIdFromService ------------->");
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(offset, limit,sortByDateDesc);
        List<DailyJobRecord> dailyJobRecords = dailyJobRecordMongoDbRepository.findByOrganizationId(organizationId, pageable).getContent();

        if (!ObjectUtils.isEmpty(dailyJobRecords)) {
            return dailyJobRecords;
        } else {
            return new ArrayList<>();
        }
    }

    public List<DailyJobRecord> getByShareOrganizationIdFromService(HttpServletRequest request, String organizationId, int offset, int limit) {

        logger.info("<-------------Invoked DailyJobRecordService :: getByShareOrganizationIdFromService ------------->");
        logger.info("sharedOrganizationId:" + organizationId);
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(offset, limit,sortByDateDesc);
        List<DailyJobRecord> dailyJobRecords = dailyJobRecordMongoDbRepository.findBySharedOrganizationId(organizationId, pageable).getContent();
        if (!ObjectUtils.isEmpty(dailyJobRecords)) {
            return dailyJobRecords;
        } else {
            return new ArrayList<>();
        }
    }
}

    

// Unresolved import (framework/JDK): com.carbo.job.model.*
// ===== Imported from: com.carbo.job.model.analytics.DailyJobRecord =====
package com.carbo.job.model.analytics;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "daily-job-records")
@CompoundIndex(def = "{'jobId': 1, 'date': 1}", name = "jobId_date_index", unique = true)
public class DailyJobRecord {
    public DailyJobRecord(){
    }
    @Id
    private String id;

    @Field("date")
    private Long date;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    private String jobId;

    @Field("fleet")
    private String fleet;

    @Field("operator")
    private String operator;

    @Field("targetStagePerDay")
    private Integer targetStagePerDay;

    @Field("actualStagePerDay")
    private Integer actualStagePerDay;

    @Field("targetHoursPerDay")
    private Float targetHoursPerDay;

    @Field("actualHoursPerDay")
    private Float actualHoursPerDay;

    @Field("nptHours")
    private Float nptHours;

    @Field("scheduledHours")
    private Float scheduledHours;

    @Field("sharedOrganizationId")
    private String sharedOrganizationId;

    @Field("pad")
    private String pad;

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
    }

    public DailyJobRecord(String organizationId,
                          Long date,
                          String jobId,
                          String fleet, Integer targetStagePerDay,
                          Integer actualStagePerDay, Float targetHoursPerDay,
            Float actualHoursPerDay, Float nptHours, Float scheduledHours,String pad) {
        this.organizationId = organizationId;
        this.date = date;
        this.jobId = jobId;
        this.fleet = fleet;
        this.targetStagePerDay = targetStagePerDay;
        this.actualStagePerDay = actualStagePerDay;
        this.targetHoursPerDay = targetHoursPerDay;
        this.actualHoursPerDay = actualHoursPerDay;
        this.nptHours = nptHours;
        this.scheduledHours = scheduledHours;
        this.pad = pad;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFleet() {
        return fleet;
    }

    public void setFleet(String fleet) {
        this.fleet = fleet;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getTargetStagePerDay() {
        return targetStagePerDay;
    }

    public void setTargetStagePerDay(Integer targetStagePerDay) {
        this.targetStagePerDay = targetStagePerDay;
    }

    public Integer getActualStagePerDay() {
        return actualStagePerDay;
    }

    public void setActualStagePerDay(Integer actualStagePerDay) {
        this.actualStagePerDay = actualStagePerDay;
    }

    public Float getTargetHoursPerDay() {
        return targetHoursPerDay;
    }

    public void setTargetHoursPerDay(Float targetHoursPerDay) {
        this.targetHoursPerDay = targetHoursPerDay;
    }

    public Float getActualHoursPerDay() {
        return actualHoursPerDay;
    }

    public void setActualHoursPerDay(Float actualHoursPerDay) {
        this.actualHoursPerDay = actualHoursPerDay;
    }

    public Float getNptHours() {
        return nptHours;
    }

    public void setNptHours(Float nptHours) {
        this.nptHours = nptHours;
    }

    public Float getScheduledHours() {
        return scheduledHours;
    }

    public void setScheduledHours(Float scheduledHours) {
        this.scheduledHours = scheduledHours;
    }

    public String getSharedOrganizationId() {
        return sharedOrganizationId;
    }

    public void setSharedOrganizationId(String sharedOrganizationId) {
        this.sharedOrganizationId = sharedOrganizationId;
    }
}

// Unresolved import (framework/JDK): org.slf4j.Logger
// ===== Imported from: com.carbo.job.services.OperatorPadInformationService =====
package com.carbo.job.services;

import com.carbo.job.model.*;
import com.carbo.job.model.well.StageInfo;
import com.carbo.job.model.well.WellInfos;
import com.carbo.job.repository.*;
import com.carbo.job.utils.WebClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.carbo.job.utils.Constants.*;

@Service
public class OperatorPadInformationService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorPadInformationService.class);

    EndStageEmailMongoDbRepository endStageEmailMongoDbRepository;

    ActivityLogMongoDbRepository activityLogMongoDbRepository;

    WellMongoDbRepository wellMongoDbRepository;

    FieldTicketMongoDbRepository fieldTicketMongoDbRepository;

    WellInfoMongoDbRepository wellInfoMongoDbRepository;

    JobMongoDbRepository jobMongoDbRepository;

    ChemicalStageMongoDbRepository chemicalStageMongoDbRepository;

    OrganizationMongoDbRepository organizationMongoDbRepository;

    public OperatorPadInformationService(WellMongoDbRepository wellMongoDbRepository,
                                         ActivityLogMongoDbRepository activityLogMongoDbRepository,
                                         EndStageEmailMongoDbRepository endStageEmailMongoDbRepository,
                                         FieldTicketMongoDbRepository fieldTicketMongoDbRepository,
                                         WellInfoMongoDbRepository wellInfoMongoDbRepository,
                                         JobMongoDbRepository jobMongoDbRepository,
                                         ChemicalStageMongoDbRepository chemicalStageMongoDbRepository,
                                         OrganizationMongoDbRepository organizationMongoDbRepository) {
        this.wellMongoDbRepository = wellMongoDbRepository;
        this.activityLogMongoDbRepository = activityLogMongoDbRepository;
        this.endStageEmailMongoDbRepository = endStageEmailMongoDbRepository;
        this.fieldTicketMongoDbRepository = fieldTicketMongoDbRepository;
        this.wellInfoMongoDbRepository = wellInfoMongoDbRepository;
        this.jobMongoDbRepository = jobMongoDbRepository;
        this.chemicalStageMongoDbRepository = chemicalStageMongoDbRepository;
        this.organizationMongoDbRepository = organizationMongoDbRepository;
    }

    public List<PadMetrics> getPadInformation(String padId,
                                              String jobId,
                                              String organizationId,
                                              boolean filterByWell,
                                              String organizationName) {
        // get organization id from a job db call
        Optional<Job> optionalJob = jobMongoDbRepository.findById(jobId);
        if (optionalJob.isEmpty())
            return new ArrayList<>();
        String jobOrganizationId = optionalJob.get().getOrganizationId();
        Long jobStartDate = optionalJob.get().getStartDate();
        Optional<Organization> optionalOrganization = organizationMongoDbRepository.findById(jobOrganizationId);
        String jobOrganizationName = optionalOrganization.get().getName();
        List<Well> wells = wellMongoDbRepository.findByPadIdAndOrganizationId(padId, jobOrganizationId);
        List<EndStageEmail> endStageEmails = endStageEmailMongoDbRepository
                .findByOrganizationIdAndTypeAndJobId(jobOrganizationId, EmailType.END_STAGE, jobId);

        List<EndStageEmail> uniqueEndStageEmails = new ArrayList<>(endStageEmails.stream()
                .collect(Collectors.toMap(
                        email -> email.getWell() + "_" + email.getStage(), // Key: wellId_stage
                        Function.identity(), // Value: the email itself
                        (existing, replacement) ->
                                existing.getSentAt().compareTo(replacement.getSentAt()) > 0
                                        ? existing
                                        : replacement // Keep the one with later sentAt
                ))
                .values());
        if (wells.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> wellNames = wells.stream().map(Well::getName).collect(Collectors.toSet());
        List<ActivityLogEntry> activityLogEntries = activityLogMongoDbRepository
                .findByOrganizationIdAndJobIdAndWellIn(jobOrganizationId, jobId, wellNames);
        // Create lookup maps
        Map<String, String> wellIdToNameMap = wells.stream()
                .collect(Collectors.toMap(Well::getId, Well::getName));

        Map<String, String> wellNameToNameMap = wells.stream()
                .collect(Collectors.toMap(Well::getName, Well::getName));

        // Normalize well field to always contain wellName
        endStageEmails.forEach(email -> {
            String wellField = email.getWell();

            if (wellField != null) {
                String wellName = wellIdToNameMap.getOrDefault(wellField,
                        wellNameToNameMap.get(wellField));

                if (wellName != null) {
                    email.setWell(wellName);
                }
            }
        });

        if (filterByWell)
            return padInfoGroupByWell(activityLogEntries, uniqueEndStageEmails, wells, jobId, jobOrganizationName, jobOrganizationId, jobStartDate, optionalJob.get());
        return padInfoGroupByDate(activityLogEntries, uniqueEndStageEmails, wells, jobId, jobOrganizationName, jobOrganizationId, jobStartDate, optionalJob.get());
    }


    public double tonsPumpedPerDay(List<FieldTicket> fieldTicketsForAllWells,
                                   Map<String, Set<String>> completedStagesPerWell) {

        if (fieldTicketsForAllWells == null || fieldTicketsForAllWells.isEmpty()
                || completedStagesPerWell == null || completedStagesPerWell.isEmpty()) {
            return 0.0;
        }

        return fieldTicketsForAllWells.stream()
                .filter(ft -> {
                    Set<String> completedStages = completedStagesPerWell.get(ft.getWell());

                    if (completedStages == null || completedStages.isEmpty()) {
                        return false;
                    }

                    return completedStages.contains(ft.getName());
                })
                .map(FieldTicket::getLastVersion)
                .flatMap(Optional::stream)
                .filter(v -> v.getProppantTons() != null)
                .mapToDouble(FieldTicketVersion::getProppantTons)
                .sum();
    }

    public double calculateStartPump(Map<String, Set<String>> completedStagesPerWell,
                                     List<EndStageEmail> endStageEmailsForAllWells) {

        if (endStageEmailsForAllWells == null || endStageEmailsForAllWells.isEmpty()
                || completedStagesPerWell == null || completedStagesPerWell.isEmpty()) {
            return 0L;
        }

        long totalStartPump = (long) endStageEmailsForAllWells.stream()
                .filter(email -> {
                    Set<String> completedStages = completedStagesPerWell.get(email.getWell());
                    return completedStages != null && completedStages.contains(email.getStage());
                })
                .mapToDouble(EndStageEmail::getPumpStart)
                .sum();

        return totalStartPump;
    }


    public double calculateTotalAvgPressure(Map<String, Set<String>> completedStagesPerWell,
                                            List<EndStageEmail> endStageEmailsForAllWells) {

        if (endStageEmailsForAllWells == null || endStageEmailsForAllWells.isEmpty()
                || completedStagesPerWell == null || completedStagesPerWell.isEmpty()) {
            return 0.0;
        }

        double totalAvgPressure = endStageEmailsForAllWells.stream()
                .filter(email -> {
                    Set<String> completedStages = completedStagesPerWell.get(email.getWell());
                    return completedStages != null && completedStages.contains(email.getStage());
                })
                .mapToDouble(email -> round(email.getAveragePressure(), 4))  // ← Round each value
                .sum();

        return round(totalAvgPressure, 4);
    }

    public double calculateTotalAvgRate(Map<String, Set<String>> completedStagesPerWell,
                                        List<EndStageEmail> endStageEmailsForAllWells) {

        if (endStageEmailsForAllWells == null || endStageEmailsForAllWells.isEmpty()
                || completedStagesPerWell == null || completedStagesPerWell.isEmpty()) {
            return 0.0;  // ← Fixed: return 0.0 instead of 0L
        }

        double totalAvgRate = endStageEmailsForAllWells.stream()
                .filter(email -> {
                    Set<String> completedStages = completedStagesPerWell.get(email.getWell());
                    return completedStages != null && completedStages.contains(email.getStage());
                })
                .mapToDouble(email -> round(email.getAverageRate(), 4))  // ← Round each value to 4 decimals
                .sum();

        return round(totalAvgRate, 4);
    }

    public double calculateCleanVolBBLs(Map<String, Set<String>> completedStagesPerWell,
                                        List<ChemicalStage> chemicalStageList) {

        if (chemicalStageList == null || chemicalStageList.isEmpty()
                || completedStagesPerWell == null || completedStagesPerWell.isEmpty()) {
            return 0.0;
        }

        double cleanVolBBLs = chemicalStageList.stream()
                .filter(chemical -> {
                    // Get the completed stages for this well
                    Set<String> completedStages = completedStagesPerWell.get(chemical.getWell());

                    if (completedStages == null) {
                        return false;
                    }

                    try {
                        // Convert chemical stage format "2.0" → "2" to match completedStages format
                        String stageStr = formatStage(chemical.getStage());
                        return completedStages.contains(stageStr);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .map(ChemicalStage::getCleanTotal)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();

        return cleanVolBBLs;
    }

    private List<PadMetrics> padInfoGroupByDate(List<ActivityLogEntry> activityLogEntries,
                                                List<EndStageEmail> endStageEmail,
                                                List<Well> wells,
                                                String jobId,
                                                String organizationName,
                                                String jobOrganizationId,
                                                Long jobStartDate,
                                                Job job) {

        List<PadMetrics> padMetricsList = new ArrayList<>();

        // ========== INITIAL DATA GROUPING ==========
        Map<String, List<EndStageEmail>> endStageEmailByWell = endStageEmail.stream()
                .collect(Collectors.groupingBy(EndStageEmail::getWell));

        // --- Group activity logs by day for this well ---
        Map<Integer, List<ActivityLogEntry>> entriesByDay = activityLogEntries
                .stream()
                .collect(Collectors.groupingBy(ActivityLogEntry::getDay));

        Map<String, String> wellIdAndNameMap = wells.stream()
                .collect(Collectors.toMap(Well::getId, Well::getName));

        // ========== PROCESS EACH WELL ==========
        List<WellInfos> wellInfoList = new ArrayList<>();
        List<EndStageEmail> endStageEmailsForAllWells = new ArrayList<>();
        List<FieldTicket> fieldTicketsforAllWells = fieldTicketMongoDbRepository
                .findByJobId(jobId);

        List<ChemicalStage> chemicalStageListForAllWells = chemicalStageMongoDbRepository
                .findByOrganizationIdAndJobId(jobOrganizationId, jobId);
        for (Well well : wells) {

            // --- Group end stage emails per stage for this well ---
            List<EndStageEmail> endStageEmails = new ArrayList<>();
            if (!endStageEmail.isEmpty()) {
                endStageEmails = endStageEmailByWell.get(well.getName());
            }
            List<WellInfos> wellInfos = wellInfoMongoDbRepository.findByWellId(well.getId());
            if (wellInfos != null)
                wellInfoList.addAll(wellInfos);
            if (endStageEmails != null)
                endStageEmailsForAllWells.addAll(endStageEmails);
        }

        // ========== PROCESS EACH DAY ==========
        for (Integer i : entriesByDay.keySet()) {

            List<ActivityLogEntry> entriesOfDay = entriesByDay.getOrDefault(i, new ArrayList<>());

            List<String> completedStagesPerDay = entriesByDay.get(i).stream()
                    .filter(ActivityLogEntry::getComplete)
                    .map(entry -> String.valueOf(entry.getStage().intValue()))
                    .toList();

            Map<String, Set<String>> completedStagesPerWell = entriesByDay.get(i).stream()
                    .filter(ActivityLogEntry::getComplete)
                    .filter(entry -> entry.getStage() != null)
                    .collect(Collectors.groupingBy(
                            ActivityLogEntry::getWell,
                            Collectors.mapping(
                                    entry -> String.valueOf(entry.getStage().intValue()),
                                    Collectors.toSet()
                            )
                    ));

            long noOfCompletedStagesPerDay = completedStagesPerDay.size();

            // -------------------- CALCULATE: medianOfTBS --------------------
            Double medianTBS = calculateMedianTBS(entriesOfDay);

            Map<String, Set<String>> stagesInCurrentDayPerWell = entriesOfDay
                    .stream()
                    .collect(Collectors.groupingBy(
                            ActivityLogEntry::getWell,
                            Collectors.mapping(
                                    entry -> String.valueOf(entry.getStage().intValue()),
                                    Collectors.toSet()
                            )
                    ));

            double tonsPumped = tonsPumpedPerDay(fieldTicketsforAllWells, completedStagesPerWell);

            // -------------------- CALCULATE: Substitution per stage --------------------
            float totalSubstitution = 0f;
            int validSubstitutionCount = 0;
            Double totalProducedWater = 0.0;

            // Calculate substitution for EACH stage individually
            for (String well : stagesInCurrentDayPerWell.keySet()) {

                // Filter to only completed stages for this well
                Set<String> completedStagesForWell = stagesInCurrentDayPerWell.get(well).stream()
                        .filter(completedStagesPerDay::contains)
                        .collect(Collectors.toSet());

                // Process EACH completed stage
                for (String stage : completedStagesForWell) {
                    // Initialize fuel values for THIS stage only
                    float stageDiesel = 0f;
                    float stageFieldGas = 0f;
                    float stageCng = 0f;
                    float stageLng = 0f;
                    float stageBtu = 0f;

                    // LAYER 1: Try to get from EndStageEmail first (highest priority)
                    List<EndStageEmail> emailsForStage = endStageEmailsForAllWells.stream()
                            .filter(e -> e.getWell().equals(well))
                            .filter(e -> {
                                try {
                                    // Convert email stage "2.0" → "2" to match completedStagesForWell format
                                    String emailStage = e.getStage();
                                    return emailStage.equals(stage);
                                } catch (NumberFormatException ex) {
                                    // If stage is not a number, compare directly
                                    return e.getStage().equals(stage);
                                }
                            })
                            .toList();

                    if (!emailsForStage.isEmpty()) {
                        EndStageEmail email = emailsForStage.get(0); // Take first/latest email

                        if (email.getDiesel() != null) {
                            stageDiesel = email.getDiesel();
                        }
                        if (email.getFieldGas() != null) {
                            stageFieldGas = email.getFieldGas();
                        }
                        if (email.getCng() != null) {
                            stageCng = email.getCng();
                        }
                        if (email.getLng() != null) {
                            stageLng = email.getLng();
                        }
                        if (email.getBtu() != null && email.getBtu() != 0) {
                            stageBtu = email.getBtu();
                        }
                        if (email.getProducedWater() != null && email.getProducedWater() != 0) {
                            totalProducedWater += email.getProducedWater();
                        }
                    }

                    // LAYER 2: Fallback to WellInfos if EndStageEmail didn't have the data
                    if (stageDiesel == 0f || stageFieldGas == 0f) {
                        // Get wellId from wellName
                        String wellId = wellIdAndNameMap.entrySet().stream()
                                .filter(entry -> entry.getValue().equals(well))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElse(null);

                        if (wellId != null) {
                            // Find WellInfos for this well
                            Optional<WellInfos> wellInfoOpt = wellInfoList.stream()
                                    .filter(wi -> wi.getWellId().equals(wellId))
                                    .findFirst();

                            if (wellInfoOpt.isPresent()) {
                                for (StageInfo stageInfo : wellInfoOpt.get().getStages()) {
                                    if (stageInfo.getStageNumber().equalsIgnoreCase(stage)) {
                                        if (stageDiesel == 0f && stageInfo.getDiesel() != null) {
                                            stageDiesel = stageInfo.getDiesel();
                                        }
                                        if (stageFieldGas == 0f && stageInfo.getFieldGas() != null) {
                                            stageFieldGas = stageInfo.getFieldGas();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // Use job BTU as fallback if stage BTU not available
                    if (stageBtu == 0.0 && job.getBtu() != 0) {
                        stageBtu = job.getBtu();
                    }

                    // Calculate substitution for THIS specific stage
                    float stageSubstitution = getSubstitution(stageDiesel, stageCng, stageFieldGas, stageLng, stageBtu);
                    totalSubstitution += stageSubstitution;
                    validSubstitutionCount++;
                }
            }

            // Calculate final average substitution
            float avgSubstitution = validSubstitutionCount > 0
                    ? totalSubstitution / validSubstitutionCount
                    : 0f;
            avgSubstitution = Math.round(avgSubstitution * 100.0f) / 100.0f;

            double totalAvgPressure = 0.0;
            double totalAvgRate = 0.0;
            double totalStartPumpTimePerDay = 0.0;
            double cleanVolBBLs = 0;

            totalStartPumpTimePerDay = calculateStartPump(completedStagesPerWell, endStageEmailsForAllWells);
            totalAvgPressure = calculateTotalAvgPressure(completedStagesPerWell, endStageEmailsForAllWells);
            totalAvgRate = calculateTotalAvgRate(completedStagesPerWell, endStageEmailsForAllWells);
            cleanVolBBLs = calculateCleanVolBBLs(completedStagesPerWell, chemicalStageListForAllWells);

            double avgDayPumpTime = noOfCompletedStagesPerDay > 0
                    ? totalStartPumpTimePerDay / noOfCompletedStagesPerDay
                    : 0;

            // -------------------- CALCULATE: avgProducedWaterUsedPercentage --------------------
            Double producedWaterPerDay = noOfCompletedStagesPerDay > 0
                    ? totalProducedWater / noOfCompletedStagesPerDay
                    : 0.0;

            double avgPressure = 0.0;
            double avgRate = 0.0;
            if (noOfCompletedStagesPerDay != 0) {
                avgPressure = (double) totalAvgPressure / noOfCompletedStagesPerDay;
                avgRate = (double) totalAvgRate / noOfCompletedStagesPerDay;
            }

            // -------------------- CALCULATE: scheduledTime --------------------
            Double scheduledTime = entriesByDay.getOrDefault(i, Collections.emptyList()).stream()
                    .filter(e -> SCHEDULED_TIME_CODE.equals(e.getOpsActivity()))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: pumpingHours (totalPumpTimePerDay) --------------------
            Double totalPumpTimePerDay = entriesByDay.getOrDefault(i, Collections.emptyList()).stream()
                    .filter(e -> PUMP_TIME_CODE.equals(e.getEventOrNptCode()))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: serviceCompanyNameNPT (totalOrgNPT) --------------------
            Double totalOrgNPT = entriesByDay.getOrDefault(i, Collections.emptyList()).stream()
                    .filter(e -> e.getOpsActivity().contains(NPT))
                    .filter(e -> e.getOpsActivity().equals(organizationName + " " + NPT))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: nonServiceCompanyNameNPT (totalNonOrgNPT) --------------------
            Double totalNonOrgNPT = entriesByDay.getOrDefault(i, Collections.emptyList()).stream()
                    .filter(e -> e.getOpsActivity().contains("NPT"))
                    .filter(e -> !e.getOpsActivity().equals(organizationName + " " + NPT))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: efficiency --------------------
            Double efficiency = 0.0;
            if (totalPumpTimePerDay + totalOrgNPT > 0) {

                efficiency = (totalPumpTimePerDay /( totalPumpTimePerDay + totalOrgNPT)) * 100;

                // Truncate final efficiency
                efficiency = Math.floor(efficiency * 100.0) / 100.0;
            }

            // -------------------- EXTRACT: date --------------------
            Integer day = entriesByDay.getOrDefault(i, Collections.emptyList()).stream()
                    .findFirst()
                    .map(ActivityLogEntry::getDay)
                    .orElse(null);


            double avgCleanVolBBLs = (noOfCompletedStagesPerDay == 0)
                    ? 0.0
                    : cleanVolBBLs / noOfCompletedStagesPerDay;

            // ========== BUILD PADMETRICS OBJECT ==========
            PadMetrics padMetrics = PadMetrics.builder()
                    .date(addDaysToDate(jobStartDate, day))
                    .stagesCompleted(noOfCompletedStagesPerDay)
                    .avgPressure(round(avgPressure, 4))
                    .avgRate(round(avgRate, 4))
                    .avgProducedWaterUsedPercentage(producedWaterPerDay)
                    .efficiency(efficiency)
                    .medianOfTBS(medianTBS)
                    .avgStartPump(avgDayPumpTime)
                    .tonsPumped(tonsPumped)
                    .avgSub(avgSubstitution)
                    .pumpingHours(convertMinutesToDecimal(totalPumpTimePerDay))
                    .serviceCompanyNameNPT(convertMinutesToDecimal(totalOrgNPT))
                    .nonServiceCompanyNameNPT(convertMinutesToDecimal(totalNonOrgNPT))
                    .scheduledTime(convertMinutesToDecimal(scheduledTime))
                    .organizationName(organizationName)
                    .cleanVolBBLs(avgCleanVolBBLs)
                    .build();

            padMetricsList.add(padMetrics);
        }


        return padMetricsList;
    }

    private Long addDaysToDate(Long jobStartDate, Integer dayNumber) {
        if (jobStartDate == null || dayNumber == null) {
            return null; // Return null date if day is null
        }
        long millisecondsPerDay = 24 * 60 * 60 * 1000L;
        return jobStartDate + ((dayNumber - 1) * millisecondsPerDay);
    }

    private String formatStage(Float stage) {
        if (stage == Math.floor(stage)) {
            return String.format("%.0f", stage);
        } else {
            return String.format("%.1f", stage);
        }
    }

    private List<PadMetrics> padInfoGroupByWell(List<ActivityLogEntry> activityLogEntries,
                                                List<EndStageEmail> endStageEmail,
                                                List<Well> wells,
                                                String jobId,
                                                String organizationName,
                                                String jobOrganiationId,
                                                Long jobStartDate,
                                                Job job) {

        List<PadMetrics> padMetricsList = new ArrayList<>();

        // ========== INITIAL DATA GROUPING ==========
        Map<String, List<ActivityLogEntry>> activitylogEntriesByWell = activityLogEntries.stream()
                .collect(Collectors.groupingBy(ActivityLogEntry::getWell));

        Map<String, List<EndStageEmail>> endStageEmailByWell = endStageEmail.stream()
                .collect(Collectors.groupingBy(EndStageEmail::getWell));

        // ========== PROCESS EACH WELL ==========
        for (Well well : wells) {

            // -------------------- INITIALIZE: Fuel & BTU Accumulators --------------------
            double totalAvgPressure = 0;
            double totalAvgRate = 0;
            double totalPumpStart = 0;
            float totalDiesel = 0f;
            float totalFieldGas = 0f;
            float totalCng = 0f;
            float totalLng = 0f;
            float sumBtu = 0f;
            int btuCount = 0;

            Double totalProducedWater = 0.0;

            // -------------------- FETCH: Chemical Stages for this Well --------------------
            List<ChemicalStage> chemicalStageList = chemicalStageMongoDbRepository
                    .findByOrganizationIdAndJobIdAndWell(jobOrganiationId, jobId, well.getName());

            // --- Group end stage emails per stage for this well ---
            Map<String, List<EndStageEmail>> endStageEmailsPerStage = new HashMap<>();
            if (!endStageEmail.isEmpty()) {
                endStageEmailsPerStage = endStageEmailByWell.getOrDefault(well.getName(), new ArrayList<>())
                        .stream()
                        .collect(Collectors.groupingBy(EndStageEmail::getStage));
            }

            // -------------------- FETCH: Field Tickets for this Well --------------------
            List<FieldTicket> fieldTickets = fieldTicketMongoDbRepository
                    .findByJobId(jobId);

            // -------------------- CALCULATE: medianOfTBS --------------------
            Double medianTBS = calculateMedianTBS(activitylogEntriesByWell.getOrDefault(well.getName(), new ArrayList<>()));

            // -------------------- IDENTIFY: All Stages in Current Well --------------------
            Set<Float> stagesInCurrentWell = activitylogEntriesByWell
                    .getOrDefault(well.getName(), Collections.emptyList())
                    .stream()
                    .map(ActivityLogEntry::getStage)
                    .collect(Collectors.toSet());

            // Get completed stages directly as String
            Set<String> completedStagesInWell = activitylogEntriesByWell
                    .getOrDefault(well.getName(), Collections.emptyList())
                    .stream()
                    .filter(ActivityLogEntry::getComplete)
                    .map(ActivityLogEntry::getStage)
                    .map(this::formatStage)  // Convert Float to String: 2.0 → "2"
                    .collect(Collectors.toSet());

            // Get the count from the Set
            Long noOfCompletedStagesPerWell = (long) completedStagesInWell.size();

            // -------------------- CALCULATE: avgPressure & avgRate (Accumulators) --------------------
            if (!endStageEmailsPerStage.isEmpty()) {
                for (String stage : activitylogEntriesByWell.get(well.getName())
                        .stream()
                        .map(ActivityLogEntry::getStage)
                        .map(this::formatStage) // Uses your formatStage method
                        .distinct() // Remove duplicates
                        .toList()) {
                    List<EndStageEmail> emails = endStageEmailsPerStage.get(stage.toString());

                    if (emails != null && !emails.isEmpty()) {
                        totalAvgPressure += emails.stream()
                                .mapToDouble(email -> round(email.getAveragePressure(), 4))
                                .sum();

                        totalAvgRate += emails.stream()
                                .mapToDouble(email -> round(email.getAverageRate(), 4))
                                .sum();

                        totalPumpStart += (long) emails.stream()
                                .mapToDouble(EndStageEmail::getPumpStart)
                                .sum();
                    }
                }
            }
            double avgDayPumpTime = noOfCompletedStagesPerWell > 0
                    ? totalPumpStart / noOfCompletedStagesPerWell
                    : 0;

            // -------------------- CALCULATE: scheduledTime --------------------
            Double scheduledTime = activitylogEntriesByWell.getOrDefault(well.getName(), Collections.emptyList()).stream()
                    .filter(e -> SCHEDULED_TIME_CODE.equals(e.getOpsActivity()))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: pumpingHours (totalPumpTimePerWell) --------------------
            Double totalPumpTimePerWell = activitylogEntriesByWell.getOrDefault(well.getName(), Collections.emptyList()).stream()
                    .filter(e -> PUMP_TIME_CODE.equals(e.getEventOrNptCode()))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: serviceCompanyNameNPT (totalOrgNPT) --------------------
            Double totalOrgNPT = activitylogEntriesByWell.getOrDefault(well.getName(), Collections.emptyList()).stream()
                    .filter(e -> e.getOpsActivity().contains(NPT))
                    .filter(e -> e.getOpsActivity().equals(organizationName + " " + NPT))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            // -------------------- CALCULATE: nonServiceCompanyNameNPT (totalNonOrgNPT) --------------------
            Double totalNonOrgNPT = activitylogEntriesByWell.getOrDefault(well.getName(), Collections.emptyList()).stream()
                    .filter(e -> e.getOpsActivity().contains(NPT))
                    .filter(e -> !e.getOpsActivity().equals(organizationName + " " + NPT))
                    .mapToDouble(e -> calculateDuration(e.getStart(), e.getEnd()))
                    .sum();

            Double efficiency = 0.0;
            if (totalPumpTimePerWell + totalOrgNPT > 0) {
                // Calculate efficiency using minutes
                efficiency = (totalPumpTimePerWell / (totalPumpTimePerWell + totalOrgNPT)) * 100;

                // Truncate final efficiency to 2 decimals
                efficiency = Math.floor(efficiency * 100.0) / 100.0;
            }

            // -------------------- EXTRACT: date --------------------
            Integer day = activitylogEntriesByWell.getOrDefault(well.getName(), Collections.emptyList()).stream()
                    .findFirst()
                    .map(ActivityLogEntry::getDay)
                    .orElse(null);

            // --- Convert stages to string format for filtering ---
            Set<String> stagesInString = stagesInCurrentWell.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());

            // -------------------- CALCULATE: tonsPumped --------------------
            double tonsPumped = fieldTickets.stream()
                    .filter(f -> f.getWell().equals(well.getName()))
                    .filter(f -> completedStagesInWell.contains(f.getName()))  // ← Only completed
                    .map(FieldTicket::getLastVersion)
                    .flatMap(Optional::stream)
                    .filter(v -> v.getProppantTons() != null)
                    .mapToDouble(FieldTicketVersion::getProppantTons)
                    .sum();

            // -------------------- ACCUMULATE: Fuel & BTU Data from EndStageEmail (Priority 1) --------------------
            for (Float stage : stagesInCurrentWell) {
                // Convert Float stage (2.0) to String ("2")
                String stageStr = formatStage(stage); // Or String.valueOf(stage.intValue())

                List<EndStageEmail> emailsForStage = endStageEmailsPerStage.get(stageStr);

                if (emailsForStage != null && !emailsForStage.isEmpty()) {
                    EndStageEmail email = emailsForStage.getFirst();

                    if (email.getDiesel() != null) {
                        totalDiesel += email.getDiesel();
                    }
                    if (email.getFieldGas() != null) {
                        totalFieldGas += email.getFieldGas();
                    }
                    if (email.getCng() != null) {
                        totalCng += email.getCng();
                    }
                    if (email.getLng() != null) {
                        totalLng += email.getLng();
                    }
                    if (email.getBtu() != null && email.getBtu() != 0) {
                        sumBtu += email.getBtu();
                        btuCount++;
                    }
                    if (email.getProducedWater() != null) {
                        totalProducedWater += email.getProducedWater();
                    }
                }
            }
            double avgProducedWater =
                    noOfCompletedStagesPerWell == 0
                            ? 0
                            : totalProducedWater / noOfCompletedStagesPerWell;

            // -------------------- CALCULATE: Substitution per stage --------------------
            List<WellInfos> wellInfos = wellInfoMongoDbRepository.findByWellId(well.getId());
            float totalSubstitution = 0f;
            int validSubstitutionCount = 0;


            // Calculate substitution for EACH stage individually
            for (String stage : completedStagesInWell) {

                // Initialize fuel values for THIS stage only
                float stageDiesel = 0f;
                float stageFieldGas = 0f;
                float stageCng = 0f;
                float stageLng = 0f;
                float stageBtu = 0f;

                // LAYER 1: Try to get from EndStageEmail first (highest priority)
                List<EndStageEmail> emailsForStage = endStageEmailsPerStage.get(stage);

                if (emailsForStage != null && !emailsForStage.isEmpty()) {
                    EndStageEmail email = emailsForStage.get(0);

                    if (email.getDiesel() != null) {
                        stageDiesel = email.getDiesel();
                    }
                    if (email.getFieldGas() != null) {
                        stageFieldGas = email.getFieldGas();
                    }
                    if (email.getCng() != null) {
                        stageCng = email.getCng();
                    }
                    if (email.getLng() != null) {
                        stageLng = email.getLng();
                    }
                    if (email.getBtu() != null && email.getBtu() != 0) {
                        stageBtu = email.getBtu();
                    }
                    if (email.getProducedWater() != null && email.getProducedWater() != 0) {
                        totalProducedWater += email.getProducedWater();
                    }
                }

                // LAYER 2: Fallback to WellInfos if EndStageEmail didn't have the data
                if ((stageDiesel == 0f || stageFieldGas == 0f) && !wellInfos.isEmpty()) {
                    for (StageInfo stageInfo : wellInfos.get(0).getStages()) {
                        if (stageInfo.getStageNumber().equalsIgnoreCase(stage)) {
                            if (stageDiesel == 0f && stageInfo.getDiesel() != null) {
                                stageDiesel = stageInfo.getDiesel();
                            }
                            if (stageFieldGas == 0f && stageInfo.getFieldGas() != null) {
                                stageFieldGas = stageInfo.getFieldGas();
                            }
                            break;
                        }
                    }
                }

                // Use job BTU as fallback if stage BTU not available
                if (stageBtu == 0f && job.getBtu() != 0) {
                    stageBtu = job.getBtu();
                }

                // Calculate substitution for THIS specific stage
                float stageSubstitution = getSubstitution(stageDiesel, stageCng, stageFieldGas, stageLng, stageBtu);
                totalSubstitution += stageSubstitution;
                validSubstitutionCount++;

            }

            // Calculate final average substitution
            float avgSubstitution = validSubstitutionCount > 0
                    ? totalSubstitution / validSubstitutionCount
                    : 0f;

            avgSubstitution = Math.round(avgSubstitution * 100.0f) / 100.0f;

            // -------------------- INITIALIZE: Pressure & Rate Averages --------------------
            double avgPressure = 0.0;
            double avgRate = 0.0;

            // -------------------- CALCULATE: cleanVolBBLs (from chemical-stages) --------------------
            Map<String, Set<String>> wellMapping = new HashMap<>();
            wellMapping.put(well.getName(), completedStagesInWell);
            double cleanVolBBLs = calculateCleanVolBBLs(wellMapping, chemicalStageList);

            // -------------------- CALCULATE: avgCleanVolBBLs (Average per Stage) --------------------
            double avgCleanVolBBLs = (noOfCompletedStagesPerWell == 0)
                    ? 0.0
                    : cleanVolBBLs / noOfCompletedStagesPerWell;

            // -------------------- CALCULATE: avgPressure & avgRate (Final Averages) --------------------
            if (noOfCompletedStagesPerWell != 0) {
                avgPressure = (double) totalAvgPressure / noOfCompletedStagesPerWell;
                avgRate = (double) totalAvgRate / noOfCompletedStagesPerWell;
            }

            // ========== BUILD PADMETRICS OBJECT ==========
            PadMetrics padMetrics = PadMetrics.builder()
                    .date(addDaysToDate(jobStartDate, day))
                    .stagesCompleted(noOfCompletedStagesPerWell)
                    .avgPressure(round(avgPressure, 4))
                    .avgRate(round(avgRate, 4))
                    .avgProducedWaterUsedPercentage(avgProducedWater)
                    .efficiency(efficiency)
                    .medianOfTBS(medianTBS)
                    .avgStartPump(avgDayPumpTime)
                    .cleanVolBBLs(avgCleanVolBBLs)
                    .tonsPumped(tonsPumped)
                    .avgSub(avgSubstitution)
                    .pumpingHours(convertMinutesToDecimal(totalPumpTimePerWell))
                    .serviceCompanyNameNPT(convertMinutesToDecimal(totalOrgNPT))
                    .nonServiceCompanyNameNPT(convertMinutesToDecimal(totalNonOrgNPT))
                    .scheduledTime(convertMinutesToDecimal(scheduledTime))
                    .well(well.getName())
                    .organizationName(organizationName)
                    .build();

            padMetricsList.add(padMetrics);
        }

        return padMetricsList;
    }

    // Helper method to convert H.MM to minutes
    private double convertHMMToMinutes(double hmmValue) {
        int hours = (int) hmmValue;
        int minutes = (int) Math.round((hmmValue - hours) * 100);
        return (hours * 60) + minutes;
    }

    private double calculateDuration(String start, String end) {
        if (start == null || end == null || start.trim().isEmpty() || end.trim().isEmpty()) {
            return 0.0;
        }

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Extract time from start (ignore date if present)
            String startTime = extractTime(start);
            LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);

            // Extract time from end (ignore date if present)
            String endTime = extractTime(end);
            LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

            Duration duration = Duration.between(startLocalTime, endLocalTime);

            // Handle case where end time is before start time (crosses midnight)
            if (duration.isNegative()) {
                duration = duration.plusDays(1);
            }

            // Return total minutes
            return duration.toMinutes();
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse time. Start: '" + start + "', End: '" + end + "'");
            return 0.0;
        }
    }

    private double convertMinutesToDecimal(double minutes) {
        int totalMinutes = (int) Math.round(minutes);
        int hours = totalMinutes / 60;
        int mins = totalMinutes % 60;
        return hours + mins / 100.0;
    }

    private String extractTime(String dateTime) {
        // If it contains a space, it's in "yyyyMMdd HH:mm" format, extract time part
        if (dateTime.contains(" ")) {
            return dateTime.substring(dateTime.indexOf(" ") + 1).trim();
        }
        // Otherwise, it's already in "HH:mm" format
        return dateTime.trim();
    }

    private static float getSubstitution(float diesel, float cng, float fieldGas, float lng, float btu) {
        float sumOfFieldGasAndCng = fieldGas + cng + lng;
        float fieldGasValue = sumOfFieldGasAndCng * btu * 1000;
        float dieselValue = diesel * 137381;
        float sumOfFieldGasAndDieselValue = fieldGasValue + dieselValue;
        float substitution = fieldGasValue / (sumOfFieldGasAndDieselValue == 0.0 ? 1 : sumOfFieldGasAndDieselValue);
        substitution = substitution * 100;
        substitution = Math.round(substitution * 100.0f) / 100.0f;
        return substitution;
    }

    private Double calculateMedianTBS(List<ActivityLogEntry> currentDayEntries) {
        if (currentDayEntries.isEmpty()) {
            return 0.0;
        }

        List<Double> allIntervals = new ArrayList<>();

        // Group entries by well
        Map<String, List<ActivityLogEntry>> entriesByWell = currentDayEntries.stream()
//                .filter(e -> "Pump Time".equals(e.getEventOrNptCode()))
                .collect(Collectors.groupingBy(ActivityLogEntry::getWell));

        // Calculate TBS for each well
        for (String well : entriesByWell.keySet()) {
            List<ActivityLogEntry> wellEntries = entriesByWell.get(well);

            // Group entries by stage and find the end of each stage for this well
            Map<Float, String> stageEndTimes = wellEntries.stream()
                    .collect(Collectors.groupingBy(
                            ActivityLogEntry::getStage,
                            Collectors.collectingAndThen(
                                    Collectors.maxBy(Comparator.comparing(ActivityLogEntry::getEnd)),
                                    opt -> opt.map(ActivityLogEntry::getEnd).orElse(null)
                            )
                    ));

            // Group entries by stage and find the start of each stage for this well
            Map<Float, String> stageStartTimes = wellEntries.stream()
                    .collect(Collectors.groupingBy(
                            ActivityLogEntry::getStage,
                            Collectors.collectingAndThen(
                                    Collectors.minBy(Comparator.comparing(ActivityLogEntry::getStart)),
                                    opt -> opt.map(ActivityLogEntry::getStart).orElse(null)
                            )
                    ));

            // Sort stages for this well
            List<Float> sortedStages = stageEndTimes.keySet().stream()
                    .sorted()
                    .collect(Collectors.toList());

            // Calculate intervals between consecutive stages for this well
            for (int i = 0; i < sortedStages.size() - 1; i++) {
                Float currentStage = sortedStages.get(i);
                Float nextStage = sortedStages.get(i + 1);

                String currentStageEnd = stageEndTimes.get(currentStage);
                String nextStageStart = stageStartTimes.get(nextStage);

                if (currentStageEnd != null && nextStageStart != null) {
                    Double interval = calculateDuration(currentStageEnd, nextStageStart);
                    if (interval > 0) {
                        allIntervals.add(interval);
                    }
                }
            }
        }

        // Calculate median from all intervals across all wells
        if (allIntervals.isEmpty()) {
            return 0.0;
        }

        Collections.sort(allIntervals);
        int size = allIntervals.size();

        double medianMinutes;
        if (size % 2 == 0) {
            medianMinutes = (allIntervals.get(size / 2 - 1) + allIntervals.get(size / 2)) / 2.0;
        } else {
            medianMinutes = allIntervals.get(size / 2);
        }

        // Convert minutes to decimal format (H.MM)
        return convertMinutesToDecimal(medianMinutes);
    }

    public static Float round(Float number, int decimalPlaces) {
        if (number == null) return null;
        double pow = Math.pow(10, decimalPlaces);
        Double result = Math.round(number * pow) / pow;
        return result.floatValue();
    }

    public static Double round(Double number, int decimalPlaces) {
        if (number == null) return null;
        double pow = Math.pow(10, decimalPlaces);
        return Math.round(number * pow) / pow;
    }

    private double addHMMValues(double value1, double value2) {
        // Convert to integer representation to avoid floating-point subtraction issues
        int totalValue1 = (int) Math.round(value1 * 100);  // 30.35 → 3035
        int hours1 = totalValue1 / 100;                     // 3035 / 100 = 30
        int minutes1 = totalValue1 % 100;                   // 3035 % 100 = 35

        int totalValue2 = (int) Math.round(value2 * 100);  // 2.46 → 246
        int hours2 = totalValue2 / 100;                     // 246 / 100 = 2
        int minutes2 = totalValue2 % 100;                   // 246 % 100 = 46

        // Add them
        int totalHours = hours1 + hours2;
        int totalMinutes = minutes1 + minutes2;

        // Handle overflow (if minutes >= 60, convert to hours)
        if (totalMinutes >= 60) {
            totalHours += totalMinutes / 60;
            totalMinutes = totalMinutes % 60;
        }

        // Return as H.MM format
        return totalHours + (totalMinutes / 100.0);
    }
}

// Unresolved import (framework/JDK): org.springframework.util.ObjectUtils
// ===== Imported from: com.carbo.job.repository.PadMongoDbRepository =====
package com.carbo.job.repository;

import com.carbo.job.model.Pad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PadMongoDbRepository extends MongoRepository<Pad, String> {
    List<Pad> findByNameIn(List<String> padNames);

    List<Pad> findByOrganizationId(String organizationId);
    Optional<Pad> findDistinctByOrganizationIdAndName(String organizationId, String name);

    List<Pad> findByNameInAndOrganizationIdIn(Set<String> padNames, Set<String> organizationIds);
}

// Unresolved import (framework/JDK): org.springframework.beans.factory.annotation.Autowired
// Unresolved import (framework/JDK): java.util.List
// Unresolved import (framework/JDK): org.springframework.data.mongodb.core.query.Criteria
// ===== Current file: src/main/java/com/carbo/job/controllers/AnalyticsServiceController.java =====
package com.carbo.job.controllers;

import com.carbo.job.model.*;
import com.carbo.job.model.analytics.DailyJobRecord;
import com.carbo.job.repository.DailyJobRecordMongoDbRepository;
import com.carbo.job.repository.PadMongoDbRepository;
import com.carbo.job.services.DailyJobRecordService;
import com.carbo.job.services.OperatorPadInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

import static com.carbo.job.utils.Constants.OPERATOR;
import static com.carbo.job.utils.ControllerUtil.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "v1/analytics")
public class AnalyticsServiceController {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceController.class);

    private final DailyJobRecordService dailyJobRecordService;
    private final DailyJobRecordMongoDbRepository dailyJobRecordMongoDbRepository;
    private MongoTemplate mongoTemplate;
    private OperatorPadInformationService operatorPadInformationService;
    private final PadMongoDbRepository padMongoDbRepository;

    @Autowired
    public AnalyticsServiceController(DailyJobRecordService dailyJobRecordService,
                                      DailyJobRecordMongoDbRepository dailyJobRecordMongoDbRepository,
                                      MongoTemplate mongoTemplate,
                                      OperatorPadInformationService operatorPadInformationService, PadMongoDbRepository padMongoDbRepository) {
        this.dailyJobRecordService = dailyJobRecordService;
        this.dailyJobRecordMongoDbRepository = dailyJobRecordMongoDbRepository;
        this.mongoTemplate=mongoTemplate;
        this.operatorPadInformationService = operatorPadInformationService;
        this.padMongoDbRepository = padMongoDbRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<DailyJobRecord> getJobs(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "200") int limit, @RequestParam(required = false) Long fromDate, @RequestParam(required = false) Long toDate) {
        String organizationId = getOrganizationId(request);
        String organizationType = getOrganizationType(request);
        if(organizationType.contentEquals(OPERATOR)){
            if (!ObjectUtils.isEmpty(fromDate)){
                Query query = new Query();
                query.addCriteria(Criteria.where("sharedOrganizationId").is(organizationId)
                        .andOperator(
                                Criteria.where("date").gte(fromDate),
                                Criteria.where("date").lte(toDate)
                        )
                );
                return mongoTemplate.find(query, DailyJobRecord.class);
            }

            return dailyJobRecordService.getBySharedOrganizationId(request,organizationId,offset,limit);
        }
        return dailyJobRecordService.getByOrganizationId(request, organizationId, offset, limit);
    }

    @RequestMapping(value = "/shared", method = RequestMethod.GET)
    public List<DailyJobRecord> getSharedJobs(HttpServletRequest request,
                                              @RequestParam(required = false, defaultValue = "0") int offset,
                                              @RequestParam(required = false, defaultValue = "200") int limit) {
        String organizationId = getOrganizationId(request);
        return dailyJobRecordService.getBySharedOrganizationId(request, organizationId, offset, limit);
    }
    @RequestMapping(value = "/operatorDashboard", method = RequestMethod.GET)
    public List<OperatorDashboardResponse> getOperatorDashboardRecord(HttpServletRequest request, @RequestParam(required = false) Long fromDate,
                                                                      @RequestParam(required = false) Long toDate) {
        String organizationId = getOrganizationId(request);
        String organizationType = getOrganizationType(request);
        List<OperatorDashboardResponse> responses = new ArrayList<>();

        if (organizationType.contentEquals(OPERATOR) ) {
            Query jobQuery = new Query();
            jobQuery.addCriteria(Criteria.where("sharedWithOrganizationId").is(organizationId));
            List<Job> jobList = mongoTemplate.find(jobQuery, Job.class);

            List<String> jobIds = jobList.stream().map(Job::getId).collect(Collectors.toList());
            Set<String> padNames = jobList.stream().map(Job::getPad).collect(Collectors.toSet());
            Set<String> organizationIds = jobList.stream().map(Job::getOrganizationId).collect(Collectors.toSet());

            Query activityLogQuery = new Query();
            activityLogQuery.addCriteria(Criteria.where("jobId").in(jobIds));
            List<ActivityLogEntry> activityLogEntries = mongoTemplate.find(activityLogQuery, ActivityLogEntry.class);

            Map<String, Map<Integer, List<ActivityLogEntry>>> jobDayActivityMap = activityLogEntries.stream()
                    .filter(entry -> entry.getDay() != null)
                    .collect(Collectors.groupingBy(
                            ActivityLogEntry::getJobId,
                            Collectors.groupingBy(ActivityLogEntry::getDay)
                    ));

            List<Pad> padlist = padMongoDbRepository.findByNameInAndOrganizationIdIn(padNames, organizationIds);
            Map<String, Map<String, Pad>> organizationPadMap = new HashMap<>();
            if(!ObjectUtils.isEmpty(padlist)) {
                organizationPadMap =
                        padlist.stream()
                                .collect(Collectors.groupingBy(
                                        Pad::getOrganizationId,           // first map key
                                        Collectors.toMap(
                                                Pad::getName,        // second map key
                                                pad -> pad              // value
                                        )
                                ));
            }
            for (Job job : jobList) {
                Map<Integer, List<ActivityLogEntry>> dayActivityMap = jobDayActivityMap.get(job.getId());
                if (dayActivityMap != null) {
                    for (Map.Entry<Integer, List<ActivityLogEntry>> dayEntry : dayActivityMap.entrySet()) {
                        Integer activityDay = dayEntry.getKey();
                        List<ActivityLogEntry> activeLogItems = dayEntry.getValue();

                        long completedStages = activeLogItems.stream()
                                .filter(each -> each.getComplete() != null && each.getComplete())
                                .count();

                        float scheduledHours = activeLogItems.stream()
                                .filter(each -> "Scheduled Time".equals(each.getOpsActivity()))
                                .map(ActivityLogEntry::getHoursSpan)
                                .reduce(0.0f, Float::sum);

                        float nptHours = activeLogItems.stream()
                                .filter(each -> !"Scheduled Time".equals(each.getOpsActivity()))
                                .map(ActivityLogEntry::getHoursSpan)
                                .reduce(0.0f, Float::sum);

                        float completedHours = activeLogItems.stream()
                                .filter(each -> "Pump Time".equals(each.getEventOrNptCode()))
                                .map(ActivityLogEntry::getHoursSpan)
                                .reduce(0.0f, Float::sum);

                        Map<String, Pad> padMap = organizationPadMap.get(job.getOrganizationId());

                        String padId = Optional.ofNullable(padMap)
                                .map(m -> m.get(job.getPad()))
                                .map(Pad::getId)
                                .orElse(null);
                        
                        OperatorDashboardResponse response = new OperatorDashboardResponse();
                        response.setDate(job.getStartDate() + ((activityDay - 1) * 86400000L));
                        response.setJobId(job.getId());
                        response.setJobNumber(job.getJobNumber());
                        response.setOrganizationId(organizationId);
                        response.setFleet(job.getFleet());
                        response.setOperator(job.getOperator());
                        response.setTargetStagePerDay(job.getTargetStagesPerDay());
                        response.setActualStagePerDay((int) completedStages);
                        response.setActualHoursPerDay(completedHours);
                        response.setNptHours(nptHours);
                        response.setScheduledHours(scheduledHours);
                        response.setSharedOrganizationId(job.getSharedWithOrganizationId());
                        response.setPad(job.getPad());
                        response.setPadId(padId);
                        response.setTargetHoursPerDay((int) job.getTargetDailyPumpTime());
                        responses.add(response);
                    }
                }
            }
        }
        if (fromDate != null && toDate != null) {
            responses = responses.stream()
                    .filter(response -> response.getDate() >= fromDate && response.getDate() <= toDate)
                    .collect(Collectors.toList());
        }

        return responses;
    }


    @RequestMapping(value = "/operatorPadMetrics", method = RequestMethod.GET)
    public List<PadMetrics> getPadInfoPerWell(HttpServletRequest request,
                                              @RequestParam String padId,
                                              @RequestParam String jobId,
                                             @RequestParam(defaultValue = "false", value = "groupByWell") boolean groupByWell) {
        String organizationId = getOrganizationId(request);
        String organizationName = getOrganization(request);
        return operatorPadInformationService.getPadInformation(padId, jobId, organizationId, groupByWell, organizationName);
    }


}

