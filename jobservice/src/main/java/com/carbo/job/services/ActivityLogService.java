package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.ActivityLogEntry;
import com.carbo.job.repository.ActivityLogMongoDbRepository;
import com.carbo.job.utils.ActivityLogUtil;
import com.carbo.job.utils.WebClientUtils;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;


@Service
public class ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);
    private final ActivityLogMongoDbRepository activityLogRepository;

    private final MongoTemplate mongoTemplate;
    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;

    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public ActivityLogService(ActivityLogMongoDbRepository activityLogRepository, MongoTemplate mongoTemplate) {
        this.activityLogRepository = activityLogRepository;
        this.mongoTemplate = mongoTemplate;
    }


    public List<ActivityLogEntry> getAll() {
        return activityLogRepository.findAll();
    }

    public List<ActivityLogEntry> getByOrganizationId(String organizationId) {
        return activityLogRepository.findByOrganizationId(organizationId);
    }

    public Optional<ActivityLogEntry> getActivityLog(String activityLogId) {
        return activityLogRepository.findById(activityLogId);
    }

    public List<ActivityLogEntry> findByOrganizationIdAndJobId(HttpServletRequest request, String organizationId, String jobId) {

        //return activityLogRepository.findByOrganizationIdAndJobId(organizationId, jobId);
        return findByOrganizationIdAndJobIdFromService(request, organizationId, jobId);

    }

    public List<ActivityLogEntry> findByOrganizationIdAndJobIdFromService(HttpServletRequest request, String organizationId, String jobId) {
        logger.debug("Invoked ActivityLogService::findByOrganizationIdAndJobIdFromService with organizationId: {}, jobId: {}", organizationId, jobId);

        // Getting all headers into a map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        String baseUrl = (String)headerValueMap.get("referer");
        String host = (String)headerValueMap.get("host");

        List<ActivityLogEntry> results = new ArrayList<>();

        try {
            logger.debug("Initializing WebClient. Current webClient instance: {}", webClient);

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if (webClient == null) {
                webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                        .baseUrl(webClientConfig.getBaseUrl() + "/" + WebClientUtils.ACTIVITY_LOG + "/")
                        .build();
                logger.debug("WebClient initialized successfully.");
            }

            logger.debug("Making API call to getActivityByOrganizationIdAndJobId with jobId: {}", jobId);

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.GET_ACTIVITY_BY_ORG_ID_AND_JOB_ID)
                            .queryParam("jobId", jobId)
                            .build())
                    .header("Host", host)
                    .header("Authorization", authToken)
                    .header("Referer", baseUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ActivityLogEntry>>() {})
                    .block();

            logger.debug("API call successful. Retrieved results: {}", results);

        } catch (Exception e) {
            logger.error("Error while connecting to activity log service API -> getActivityByOrganizationIdAndJobId. Error message: {}", e.getMessage(), e);
            logger.debug("Fallback to activityLogRepository for organizationId: {}, jobId: {}", organizationId, jobId);
            results = activityLogRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            logger.debug("Fallback results from activityLogRepository: {}", results);
        }

        logger.debug("Returning results: {}", results);
        return results;
    }


    public List<ActivityLogEntry> findByJobIdAndDay(String jobId, Integer day) {
        return activityLogRepository.findByJobIdAndDay(jobId, day);
    }

    public List<ActivityLogEntry> findByOrganizationIdAndWellAndStage(String organizationId, String well, Float stage) {
        return activityLogRepository.findByOrganizationIdAndWellAndStage(organizationId, well, stage);
    }

    public List<ActivityLogEntry> findByOrganizationIdAndModifiedBetween(String organizationId, Long start, Long end) {
        return activityLogRepository.findByOrganizationIdAndModifiedBetween(organizationId, start, end, Sort.by(Sort.Direction.ASC, "jobId"));
    }

    public List<ActivityLogEntry> findByJobIdAndModifiedBetween(String jobId, Long start, Long end) {
        return activityLogRepository.findByJobIdAndModifiedBetween(jobId, start, end);
    }

    public Optional<ActivityLogEntry> findByJobIdAndWellAndStageAndComplete(String jobId, String wellName, Float stage) {
        List<ActivityLogEntry> found = activityLogRepository.findByJobIdAndWellAndStageAndComplete(jobId, wellName, stage, true);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(found.get(0));
        }
    }

    public List<ActivityLogEntry> findByJobIdAndWellAndStageAndEventOrNptCode(String jobId, String wellName, Float stage, String code) {
        Query query = new Query(Criteria
                .where("jobId").is(jobId)
                .and("well").is(wellName)
                .and("stage").is(stage)
                .and("eventOrNptCode").is(code));
        return mongoTemplate.find(query, ActivityLogEntry.class);
    }

    public void updateActivityLog(HttpServletRequest request,ActivityLogEntry activityLog) {
        //activityLogRepository.save(activityLog);
        saveActivityLogEntry(request,activityLog);

    }

    public void saveActivityLogEntry(HttpServletRequest request, ActivityLogEntry activityLog) {

        logger.info("<-------------Invoked ActivityLogService :: saveActivityLogEntry ------------->");
        logger.info("saveActivityLogEntry" +activityLog);

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        logger.info("headerValueMap"+headerValueMap);
        String authToken = (String)headerValueMap.get("authorization");

        Void results = null;
        try {

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.ACTIVITY_LOG+"/")
                    .build();

            results = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.ACTIVITY_LOG_SAVE_UPDATE_ACTIVITY_LOG)
                            .build())
                    .body(Mono.just(activityLog), ActivityLogEntry.class)
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

        }catch (Exception e){
            logger.error("Error while connecting to saveActivityLogEntry API  "+e.getMessage());
            activityLogRepository.save(activityLog);
        }

    }
}
