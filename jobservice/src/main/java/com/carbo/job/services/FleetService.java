package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.Fleet;
import com.carbo.job.repository.FleetMongoDbRepository;
import com.carbo.job.utils.ActivityLogUtil;
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
public class FleetService {
    private final FleetMongoDbRepository fleetRepository;

    private static final Logger logger = LoggerFactory.getLogger(FleetService.class);
    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public FleetService(FleetMongoDbRepository fleetRepository) {
        this.fleetRepository = fleetRepository;
    }

    public Optional<Fleet> getByName(HttpServletRequest request, String organizationId, String name) {
        return fleetRepository.findDistinctByOrganizationIdAndName(organizationId, name);
        //return findDistinctByOrganizationIdAndNameFromService(request, organizationId, name);
    }

    public Optional<Fleet> getById(String id) {
        return fleetRepository.findDistinctById(id);
    }

    public Optional<Fleet> findDistinctByOrganizationIdAndNameFromService(HttpServletRequest request, String organizationId, String name) {

        logger.info("<-------------Invoked FleetService :: findDistinctByOrganizationIdAndNameFromService ------------->");
        logger.info("organizationId" +organizationId+" name "+name);

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        Optional<Fleet> results = Optional.ofNullable(new Fleet());
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+ WebClientUtils.FLEET_SERVICE+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.FLEET_FIND_DISTINCT_BY_ORGID_AND_NAME)
                            .queryParam("organizationId", organizationId)
                            .queryParam("name", name)
                            //.queryParam("sharedFrom", sharedFromOrganizationId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Fleet.class)
                    .blockOptional();

        }catch (Exception e){
            logger.error("Error while connecting to v1/fleets/findDistinctByOrganizationIdAndName "+e.getMessage());
            results = fleetRepository.findDistinctByOrganizationIdAndName(organizationId, name);
        }
        return results;
    }
}
