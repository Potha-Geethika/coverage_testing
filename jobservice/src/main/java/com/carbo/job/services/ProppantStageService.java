package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.Job;
import com.carbo.job.model.MigrationStatusEntry;
import com.carbo.job.model.MigrationType;
import com.carbo.job.repository.ProppantStageMongoDbRepository;
import com.carbo.job.utils.WebClientUtils;
import com.carbo.ws.model.ProppantStage;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Optional;

@Service
public class ProppantStageService {
    private static final Logger logger = LoggerFactory.getLogger(ProppantStageService.class);
    private static final MigrationType MIGRATION_TYPE = MigrationType.PROPPANT_STAGE;
    private static final long CHECK_MIGRATION_CUTOFF_DATE = new GregorianCalendar(2020, 12 - 1, 1).getTime().getTime();

    private final ProppantStageMongoDbRepository proppantStageMongoDbRepository;

    private final MigrationStatusEntryService migrationStatusEntryService;

    private final JobService jobService;

    @Autowired
    private WebClient.Builder webClientBuilder ;
    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public ProppantStageService(ProppantStageMongoDbRepository proppantStageMongoDbRepository, JobService jobService,
                                MigrationStatusEntryService migrationStatusEntryService, JobService jobService1) {
        this.proppantStageMongoDbRepository = proppantStageMongoDbRepository;
        this.migrationStatusEntryService = migrationStatusEntryService;
        this.jobService = jobService1;
    }

    public Optional<ProppantStage> findByOrganizationIdAndJobIdAndWellAndStage(HttpServletRequest request, String organizationId, String jobId, String well, Float stage) {
        Optional<ProppantStage> ret;
        Optional<Job> job = jobService.getJob(jobId);

        if (job.isPresent()) {
            if (job.get().getCreated() > CHECK_MIGRATION_CUTOFF_DATE) { // No need to check migration for jobs created after 2020/12/1
                ret = proppantStageMongoDbRepository.findByOrganizationIdAndJobIdAndWellAndStage(organizationId, jobId, well, stage);
            }
            else {
                Optional<MigrationStatusEntry> migrationStatusEntryOptional = migrationStatusEntryService.findByJobIdAndMigrationType(request, jobId, MIGRATION_TYPE);

                if (migrationStatusEntryOptional.isPresent()) {
                    MigrationStatusEntry migrationStatusEntry = migrationStatusEntryOptional.get();
                    if (migrationStatusEntry.isMigrated()) {
                        ret = proppantStageMongoDbRepository.findByOrganizationIdAndJobIdAndWellAndStage(organizationId, jobId, well, stage);
                    }
                    else {
                        throw new IllegalStateException("Job " + jobId + " has not been migrated.");
                    }
                }
                else {
                    throw new IllegalStateException("Job " + jobId + " has not been migrated.");
                }
            }
        }
        else {
            throw new IllegalStateException("Unable to find job with Id: " + jobId);
        }

        return ret;
    }

    public Optional<ProppantStage> findByOrganizationIdAndJobIdAndWellAndStageFromService(HttpServletRequest request, String organizationId, String jobId, String well, Float stage) {

        logger.info("<-------------Invoked ProppantService :: findByOrganizationIdAndJobIdAndWellAndStageFromService ------------->");
        logger.info("organizationId" + organizationId+" jobId" + jobId+" well" + well+" stage" + stage);

        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        Optional<ProppantStage> results = null;
        try {

            logger.info("Before initWebClient webClient "+webClient );

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPANT_STAGE+"/")
                    .build();
            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PROPANT_STAGE_GET_BY_ORG_ID_AND_JOB_ID_AND_WELL_AND_STAGE)
                            .queryParam("organizationId", organizationId)
                            .queryParam("jobId", jobId)
                            .queryParam("well", well)
                            .queryParam("stage", stage)

                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ProppantStage.class)
                    .blockOptional();

        } catch (Exception e) {
            logger.error("Error while connecting v1/proppant-stages/getByOrganizationIdAndJobIdAndWellAndStage api  " + e.getMessage());
            results = proppantStageMongoDbRepository.findByOrganizationIdAndJobIdAndWellAndStage(organizationId, jobId, well, stage);
        }
        return results;
    }
}
