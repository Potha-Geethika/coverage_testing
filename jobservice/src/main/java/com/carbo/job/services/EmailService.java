package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.EmailType;
import com.carbo.job.model.EndStageEmail;
import com.carbo.job.model.MigrationType;
import com.carbo.job.repository.EndStageEmailMongoDbRepository;
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
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final MigrationType MIGRATION_TYPE = MigrationType.EMAIL;

    private final EndStageEmailMongoDbRepository endStageEmailMongoDbRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;
    private WebClient webClient;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public EmailService(EndStageEmailMongoDbRepository endStageEmailMongoDbRepository, MongoTemplate mongoTemplate) {
        this.endStageEmailMongoDbRepository = endStageEmailMongoDbRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<EndStageEmail> findByOrganizationIdAndJobIdAndWellAndStage(HttpServletRequest request, String organizationId, String jobId, String well, Float stage) {
        Optional<EndStageEmail> ret;
        String stageStr;
        if (stage == Math.floor(stage)) {
            stageStr = String.format("%.0f", stage);
        } else {
            stageStr = String.format("%.1f", stage);
        }
        //List<EndStageEmail> endStageEmails = endStageEmailMongoDbRepository.findByOrganizationIdAndJobIdAndTypeAndWellAndStage(organizationId, jobId, EmailType.END_STAGE, well, stageStr);
        List<EndStageEmail> endStageEmails = findByOrganizationIdAndJobIdAndTypeAndWellAndStageFromService(request, jobId, EmailType.END_STAGE, well, stageStr,organizationId);
        if (endStageEmails.isEmpty()) {
            ret = Optional.empty();
        }
        else {
            ret = Optional.of(endStageEmails.get(endStageEmails.size() - 1));
        }

        return ret;
    }

    public List<EndStageEmail> findByOrganizationIdAndJobIdAndTypeAndWellAndStageFromService(HttpServletRequest request, String jobId, EmailType type, String well, String stageStr, String organizationId) {

        logger.info("<-------------Invoked EmailService :: findByOrganizationIdAndJobIdAndTypeAndWellAndStageFromService ------------->");
        logger.info("jobId  : "+jobId+" well  : "+well+" stage  :"+stageStr);

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        String emailType = type.name();

        List<EndStageEmail> results;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+ WebClientUtils.EMAIL+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.EMAIL_FIND_EMAIL_BY_ORG_ID_AND_JOB_ID_AND_TYPE_AND_WELL_AND_STAGE)
                            .queryParam("jobId", jobId)
                            .queryParam("emailType", emailType)
                            .queryParam("well", well)
                            .queryParam("stage", stageStr)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EndStageEmail>>() {
                    })
                    .block();

        }catch (Exception e){
            logger.error("Error while connecting to v1/emails/findEmailByOrganizationIdAndJobIdAndTypeAndWellAndStage "+e.getMessage());
            results = endStageEmailMongoDbRepository.findByOrganizationIdAndJobIdAndTypeAndWellAndStage(organizationId, jobId, EmailType.END_STAGE, well, stageStr);
        }
        return results;
    }

    public List<EndStageEmail> findByOrganizationIdAndTypeAndWellAndStageFromService(HttpServletRequest request, EmailType type, String well, String stageStr, String organizationId) {

        logger.info("<-------------Invoked EmailService :: findByOrganizationIdAndJobIdAndTypeAndWellAndStageFromService ------------->");

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        String emailType = type.name();

        List<EndStageEmail> results;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
                webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                        .baseUrl(webClientConfig.getBaseUrl()+"/"+ WebClientUtils.EMAIL+"/")
                        .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.EMAIL_FIND_EMAIL_BY_ORG_ID_AND_TYPE_AND_WELL_AND_STAGE)
                            .queryParam("emailType", emailType)
                            .queryParam("well", well)
                            .queryParam("stage", stageStr)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EndStageEmail>>() {
                    })
                    .block();

        }catch (Exception e){
            logger.error("Error while connecting to v1/emails/findEmailByOrganizationIdAndJobIdAndTypeAndWellAndStage "+e.getMessage());
            results = endStageEmailMongoDbRepository.findByOrganizationIdAndTypeAndWellAndStage(organizationId, EmailType.END_STAGE, well, stageStr);
        }
        return results;
    }

    public Optional<EndStageEmail> findByOrganizationIAndWellAndStage(HttpServletRequest request, String organizationId, String well, Float stage) {
        Optional<EndStageEmail> ret;
        String stageStr;
        if (stage == Math.floor(stage)) {
            stageStr = String.format("%.0f", stage);
        } else {
            stageStr = String.format("%.1f", stage);
        }
        //List<EndStageEmail> endStageEmails = endStageEmailMongoDbRepository.findByOrganizationIdAndJobIdAndTypeAndWellAndStage(organizationId, jobId, EmailType.END_STAGE, well, stageStr);
        List<EndStageEmail> endStageEmails = findByOrganizationIdAndTypeAndWellAndStageFromService(request, EmailType.END_STAGE, well, stageStr,organizationId);
        if (endStageEmails.isEmpty()) {
            ret = Optional.empty();
        }
        else {
            ret = Optional.of(endStageEmails.get(endStageEmails.size() - 1));
        }

        return ret;
    }
    public Optional<EndStageEmail> findByOrganizationIdAndOptionalJobIdAndTypeAndWellNameOrWellIdAndStage(
            String organizationId, String jobId, EmailType emailType, String wellName, String wellId, Float stage) {

        String stageStr;
        if (stage == Math.floor(stage)) {
            stageStr = String.format("%.0f", stage);
        } else {
            stageStr = String.format("%.1f", stage);
        }

        Query endStageEmailQuery = new Query();
        endStageEmailQuery.addCriteria(Criteria.where("organizationId").is(organizationId)
                .and("stage").is(stageStr)
                .and("type").is(emailType));

        if (jobId != null && !jobId.isEmpty()) {
            endStageEmailQuery.addCriteria(Criteria.where("jobId").is(jobId));
        }

        endStageEmailQuery.addCriteria(
                new Criteria().orOperator(
                        Criteria.where("well").is(wellId),
                        Criteria.where("well").is(wellName))
        );

        endStageEmailQuery.with(Sort.by(Sort.Direction.DESC, "created"));
        endStageEmailQuery.limit(1);

        return Optional.ofNullable(mongoTemplate.findOne(endStageEmailQuery, EndStageEmail.class));
    }

}
