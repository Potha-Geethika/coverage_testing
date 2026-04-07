package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.ChemicalStage;
import com.carbo.job.model.MigrationStatusEntry;
import com.carbo.job.model.MigrationType;
import com.carbo.job.repository.ChemicalStageMongoDbRepository;
import com.carbo.job.utils.WebClientUtils;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChemicalStageService {
    private static final Logger logger = LoggerFactory.getLogger(ChemicalStageService.class);
    private static final MigrationType MIGRATION_TYPE = MigrationType.CHEMICAL_STAGE;

    private final ChemicalStageMongoDbRepository chemicalStageMongoDbRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private SslContext sslContext;
    private WebClient webClient;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public ChemicalStageService(ChemicalStageMongoDbRepository chemicalStageMongoDbRepository) {
        this.chemicalStageMongoDbRepository = chemicalStageMongoDbRepository;
    }

    public Optional<ChemicalStage> findByOrganizationIdAndJobIdAndWellAndStage(HttpServletRequest request,String organizationId, String jobId, String well, Float stage) {
        //Optional<ChemicalStage> ret = chemicalStageMongoDbRepository.findByOrganizationIdAndJobIdAndWellAndStage(organizationId, jobId, well, stage);
        Optional<ChemicalStage> ret = findByOrganizationIdAndJobIdAndWellAndStageFromService(request, organizationId, jobId, well, stage);
        return ret;
    }

    public Optional<ChemicalStage> findByOrganizationIdAndJobIdAndWellAndStageFromService (HttpServletRequest
                                                                                                   request, String organizationId, String jobId, String well, Float stage){


        logger.info("<-------------Invoked getDetailsByOrganizationIdAndJobIdAndWellAndStageFromSerice ------------->");
        logger.info("organizationId  : " + organizationId+"jobId  : " + jobId+"well  : " + well+"stage  :" + stage);

        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        List<ChemicalStage> results = new ArrayList<ChemicalStage>();
        Optional returnValue = null;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.CHEMICAL_STAGE+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.CHEMICAL_STAGE_GET_ALL_RECORDS_BY_JOB_ID_AND_WELL_ID_AND_STAGE_ORG)
                            .queryParam("jobId", jobId)
                            .queryParam("well", well)
                            .queryParam("stage", stage)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ChemicalStage>>() {
                    }).block();

        } catch (Exception e) {
            logger.error("Error while connecting to v1/chemical-stages/getAllRecordsByJobIdAndWellAndStageAndOrganizationId " + e.getMessage());
            returnValue = chemicalStageMongoDbRepository.findByOrganizationIdAndJobIdAndWellAndStage(organizationId, jobId, well, stage);
            return returnValue;
        }
        if (results != null && results.size() > 0)
            returnValue = Optional.ofNullable(results.get(0));
        else
            returnValue = Optional.ofNullable(new ChemicalStage());
        return returnValue;


    }
}
