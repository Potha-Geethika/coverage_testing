package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.MigrationStatusEntry;
import com.carbo.job.model.MigrationType;
import com.carbo.job.repository.MigrationStatusEntryMongoDbRepository;
import com.carbo.job.utils.WebClientUtils;
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
import java.util.Map;
import java.util.Optional;

@Service
public class MigrationStatusEntryService {
    private final MigrationStatusEntryMongoDbRepository migrationStatusEntryMongoDbRepository;

    private static final Logger logger = LoggerFactory.getLogger(MigrationStatusEntryService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public MigrationStatusEntryService(MigrationStatusEntryMongoDbRepository migrationStatusEntryMongoDbRepository) {
        this.migrationStatusEntryMongoDbRepository = migrationStatusEntryMongoDbRepository;
    }

    public Optional<MigrationStatusEntry> findByJobIdAndMigrationType(HttpServletRequest request, String jobId, MigrationType migrationType) {
        //return migrationStatusEntryMongoDbRepository.findByJobIdAndMigrationType(jobId, migrationType);
        return findByJobIdAndMigrationTypeFromService(request,jobId, migrationType);
    }

    public MigrationStatusEntry save(MigrationStatusEntry item) {
        return migrationStatusEntryMongoDbRepository.save(item);
    }

    public Optional<MigrationStatusEntry> findByJobIdAndMigrationTypeFromService(HttpServletRequest request, String jobId, MigrationType migrationType) {

        logger.info("<-------------Invoked MigrationStatusEntryService :: findByJobIdAndMigrationTypeFromService ------------->");
        logger.info("MigrationType:"+migrationType.name()+" JobId: "+jobId);

        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        Optional <MigrationStatusEntry> entries= null;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.MIGRATION_STATUS_ENTRY+"/")
                    .build();

            entries = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.MIGRATION_STATUS_ENTRY_FIND_BY_API_FIND_BY_JOBID_AND_MIGRATION_TYPE )
                            .queryParam("jobId", jobId)
                            .queryParam("migrationType", migrationType.name())
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(MigrationStatusEntry.class)
                    .blockOptional();

        }catch (Exception e){
            logger.error("Error in MigrationStatusEntryService : findByJobIdAndMigrationTypeFromService "+e.getMessage());
            migrationStatusEntryMongoDbRepository.findByJobIdAndMigrationType(jobId, migrationType);
        }


        return  entries;
    }
}
