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
