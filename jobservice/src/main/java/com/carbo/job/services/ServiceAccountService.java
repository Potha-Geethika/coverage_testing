package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.ServiceAccount;
import com.carbo.job.repository.ServiceAccountMongoDbRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceAccountService {
    private final ServiceAccountMongoDbRepository serviceAccountMongoDbRepository;

    private static final Logger logger = LoggerFactory.getLogger(ServiceAccountService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;
    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;
    @Autowired
    public ServiceAccountService(ServiceAccountMongoDbRepository serviceAccountMongoDbRepository) {
        this.serviceAccountMongoDbRepository = serviceAccountMongoDbRepository;
    }

    public List<ServiceAccount> getAll() {
        return serviceAccountMongoDbRepository.findAll();
    }

    public List<ServiceAccount> getByOrganizationId(String organizationId) {
        return serviceAccountMongoDbRepository.findByOrganizationId(organizationId);
    }

    public Optional<ServiceAccount> get(HttpServletRequest request,String serviceAccountId) {
        //return serviceAccountMongoDbRepository.findById(serviceAccountId);
        return findByIdFromService( request, serviceAccountId);
    }

    public ServiceAccount save(ServiceAccount serviceAccount) {
        return serviceAccountMongoDbRepository.save(serviceAccount);
    }

    public void update(ServiceAccount serviceAccount) {
        serviceAccountMongoDbRepository.save(serviceAccount);
    }

    public void delete(String serviceAccountId) {
        serviceAccountMongoDbRepository.deleteById(serviceAccountId);
    }

    public Optional<ServiceAccount> findByIdFromService(HttpServletRequest request, String serviceAccountId) {

        logger.info("<-------------Invoked ServiceAccountService :: findByIdFromService ------------->");
        logger.info("Id:"+serviceAccountId);

        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        Optional <ServiceAccount> results= Optional.ofNullable(new ServiceAccount());

        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.SERVICE_ACCOUNT+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.SERVICE_ACCOUNT_FIND_BY_ID )
                            .queryParam("id", serviceAccountId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ServiceAccount.class)
                    .blockOptional();

        }catch (Exception e){
            logger.error("Error in ServiceAccountService :: findByIdFromService "+e.getMessage());
            results = serviceAccountMongoDbRepository.findById(serviceAccountId);
        }
        return  results;
    }
}
