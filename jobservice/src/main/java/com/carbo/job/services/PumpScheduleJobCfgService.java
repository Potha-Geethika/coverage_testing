package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.PumpScheduleJobCfg;
import com.carbo.job.repository.PumpScheduleJobCfgMongoDbRepository;
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
public class PumpScheduleJobCfgService {
    private final PumpScheduleJobCfgMongoDbRepository pumpScheduleJobCfgMongoDbRepository;

    private static final Logger logger = LoggerFactory.getLogger(PumpScheduleJobCfgService.class);

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;

    private WebClient webClient;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public PumpScheduleJobCfgService(PumpScheduleJobCfgMongoDbRepository pumpScheduleJobCfgMongoDbRepository) {
        this.pumpScheduleJobCfgMongoDbRepository = pumpScheduleJobCfgMongoDbRepository;
    }

    public PumpScheduleJobCfg getByJobId(HttpServletRequest request, String jobId) {
        //return pumpScheduleJobCfgMongoDbRepository.findByJobId(jobId);
        return getByJobIdFromService(request, jobId);
    }

    public Optional<PumpScheduleJobCfg> getPumpSchedule(String id) {
        return pumpScheduleJobCfgMongoDbRepository.findById(id);
    }

    public PumpScheduleJobCfg save(PumpScheduleJobCfg rec) {
        return pumpScheduleJobCfgMongoDbRepository.save(rec);
    }

    public void delete(String id) {
        pumpScheduleJobCfgMongoDbRepository.deleteById(id);
    }

    public PumpScheduleJobCfg getByJobIdFromService(HttpServletRequest request, String jobId) {

        logger.info("<-------------Invoked PumpScheduleJobCfg :: getByJobIdFromService ------------->");
        logger.info("jobId  : "+jobId);
        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        PumpScheduleJobCfg results = new PumpScheduleJobCfg();
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+ WebClientUtils.PUMP_SCHEDULE_JOB_CFG_SERVICE+"/")
                    .build();
            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PUMP_SCHEDULE_JOB_CFG_FIND_BY_JOB_ID)
                            .queryParam("jobId", jobId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(PumpScheduleJobCfg.class)
                    .block();

        }catch (Exception e){
            logger.error("Error while connecting to getActivityByOrganizationIdAndJobId "+e.getMessage());
            results =  pumpScheduleJobCfgMongoDbRepository.findByJobId(jobId);
        }
        return results;
    }
}
