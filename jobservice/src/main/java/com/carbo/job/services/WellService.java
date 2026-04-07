package com.carbo.job.services;

import java.util.Map;
import java.util.Optional;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.Well;
import com.carbo.job.utils.WebClientUtils;

import io.netty.handler.ssl.SslContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import org.springframework.stereotype.Service;
import com.carbo.job.model.well.Wells;
import com.carbo.job.repository.WellMongoDbRepository;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;



import jakarta.servlet.http.HttpServletRequest;

@Service
public class WellService {
    private final WellMongoDbRepository wellRepository;

    private static final Logger logger = LoggerFactory.getLogger(WellService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;


    @Autowired
    private SslContext sslContext;

    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public WellService(WellMongoDbRepository wellRepository) {
        this.wellRepository = wellRepository;

    }


    public Optional<Wells> getByWellAPI(HttpServletRequest request,String api) {
        //return wellRepository.findByApi(api);
        return findApiFromService(request,api);
    }

    public Optional<Wells> getById(HttpServletRequest request,String id) {
        return wellRepository.findById(id);
    }

    public Optional<Wells> findApiFromService(HttpServletRequest request, String api) {

        logger.info("<-------------Invoked WellService::findByApi ------------->");
        logger.info("api:"+api);

        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        Optional <Wells> results = Optional.ofNullable(new Wells());
        try {

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
                webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                        .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.WELL_SERVICE+"/")
                        .build();

            results= webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.WELLSERVICE_FIND_BY_API )
                            .queryParam("api",api)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Wells.class)
                    .blockOptional();

        }catch (Exception e){
            logger.error("Error in WellService  :: findApiFromService "+e.getMessage());
            results = wellRepository.findByApi(api);
        }
        return  results;
    }

    public void updateWell(Wells well) {
        //wellSourceBean.publishWellChanged("UPDATE", well);
        wellRepository.save(well);
    }
}