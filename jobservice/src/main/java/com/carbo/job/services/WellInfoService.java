package com.carbo.job.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.WellInfo;
import com.carbo.job.utils.WebClientUtils;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import com.carbo.job.model.well.WellInfos;
import com.carbo.job.repository.WellInfoMongoDbRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WellInfoService {
    private final WellInfoMongoDbRepository wellInfoRepository;
    private static final Logger logger = LoggerFactory.getLogger(WellInfoService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;
    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;
    @Autowired
    public WellInfoService(WellInfoMongoDbRepository wellInfoRepository) {
        this.wellInfoRepository = wellInfoRepository;
    }

    public List<WellInfos> getByWellId(String wellId) {
        return wellInfoRepository.findByWellId(wellId);
    }

    public void updateWellInfo(WellInfos wellInfo) {
        wellInfoRepository.save(wellInfo);
    }

    public List<WellInfos> findByWellIdFromService(HttpServletRequest request, String wellId) {

        logger.info("<-------------Invoked WellInfoService ::findByWellIdFromService ------------->");
        logger.info("wellId  : "+wellId);
        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        List<WellInfo> results = new ArrayList<>();
        List<WellInfos> newList = null;
        try {

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.WELL_INFO+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.WELL_INFO_BY_WELL_ID)
                            .queryParam("wellId", wellId)
                            .build())

                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<WellInfo>>() {
                    }).block();


        }catch (Exception e){
            logger.error("Error in WellInfoService:: findByWellIdFromService "+e.getMessage());
            newList = wellInfoRepository.findByWellId(wellId);
        }
        newList = convertWellInfoModel(results);
        return newList;
    }

    private List<WellInfos> convertWellInfoModel(List<WellInfo> inputList){

        List<WellInfos> newList = new ArrayList<>();
        if(inputList!=null){
            WellInfo wellInfo = null;
            for(int i=0;i<inputList.size();i++){
                wellInfo = (WellInfo) inputList.get(i);
                WellInfos wellInfos = new WellInfos();
                wellInfos.setId(wellInfo.getId());
                wellInfos.setJobId(wellInfo.getJobId());
                wellInfos.setWellId(wellInfo.getWellId());
                wellInfos.setPerfSize(wellInfo.getPerfSize());
                wellInfos.setKop(wellInfo.getKop());
                wellInfos.setHeel(wellInfo.getHeel());
                wellInfos.setStages(wellInfo.getStages());
                wellInfos.setOrganizationId(wellInfo.getOrganizationId());
                wellInfos.setTs(wellInfo.getTs());
                newList.add(wellInfos);
            }
        }
        return newList;
    }
}
