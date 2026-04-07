package com.carbo.job.ipc.client;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.Company;
import com.carbo.job.utils.Constants;
import com.carbo.job.utils.ControllerUtil;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
@Component
public class ProposalServiceClient {


    private final WebClientConfig webClientConfig;

    private final SslContext sslContext;


    Logger logger = LoggerFactory.getLogger(ProposalServiceClient.class);

    public ProposalServiceClient(WebClientConfig webClientConfig, SslContext sslContext) {
        this.webClientConfig = webClientConfig;
        this.sslContext = sslContext;
    }

    public ResponseEntity<Company> saveOrUpdateCompany(HttpServletRequest request, Company company) {
        Map<String, String> headerValueMap = ControllerUtil.getHeadersInfo(request);
        String url = webClientConfig.getBaseUrl();
        logger.info("Base Url : {} ", url);
        String authToken = headerValueMap.get(Constants.AUTHORIZATION);

        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            WebClient webClientCall = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).baseUrl(url).defaultHeader(HttpHeaders.AUTHORIZATION, authToken).build();

            String jobServiceUrl = Constants.CREATE_COMPANY_URI;

            String finalUrl = url + jobServiceUrl;
            logger.info("save company uri  : {} ", finalUrl);

            Mono<Company> responseMono = webClientCall.post().uri(jobServiceUrl).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(company)).retrieve().onStatus(status -> status.isError(), response -> Mono.error(new RuntimeException("Error creating company"))).bodyToMono(Company.class);
            return new ResponseEntity<>(responseMono.block(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error creating company : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
