package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.repository.PumpScheduleMongoDbRepository;
import com.carbo.job.model.PumpSchedule;
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
public class PumpScheduleService {
    private final PumpScheduleMongoDbRepository pumpScheduleRepository;
    private static final Logger logger = LoggerFactory.getLogger(PumpScheduleService.class);
    @Autowired
    private WebClient.Builder webClientBuilder ;
    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public PumpScheduleService(PumpScheduleMongoDbRepository pumpScheduleRepository) {
        this.pumpScheduleRepository = pumpScheduleRepository;
    }

    public List<PumpSchedule> getAll() {
        return pumpScheduleRepository.findAll();
    }

    public List<PumpSchedule> getByOrganizationId(String organizationId) {
        return pumpScheduleRepository.findByOrganizationId(organizationId);
    }

    public List<PumpSchedule> getByJobId(String jobId) {
        return pumpScheduleRepository.findByJobId(jobId);
    }

    public List<PumpSchedule> getByJobIdAndWellId(HttpServletRequest request, String jobId,String wellId) {
        //return pumpScheduleRepository.findByJobIdAndWellId(jobId,wellId);
        return findByJobIdAndWellIdFromService(request,jobId,wellId);
    }

    public Optional<PumpSchedule> getPumpSchedule(String pumpScheduleId) {
        return pumpScheduleRepository.findById(pumpScheduleId);
    }

    public PumpSchedule savePumpSchedule(PumpSchedule pumpSchedule) {
        return pumpScheduleRepository.save(pumpSchedule);
    }

    public void updatePumpSchedule(PumpSchedule pumpSchedule) {
        pumpScheduleRepository.save(pumpSchedule);
    }

    public void deletePumpSchedule(String pumpScheduleId) {
        pumpScheduleRepository.deleteById(pumpScheduleId);
    }

    public List<PumpSchedule> findByJobIdAndWellIdFromService(HttpServletRequest request, String jobId, String wellId) {

        logger.info("<-------------Invoked PumpScheduleService :: findByJobIdAndWellIdFromService ------------->");
        logger.info("jobId"+jobId+" well Id"+wellId);
        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);
        String authToken = (String)headerValueMap.get("authorization");
        List<PumpSchedule> results = new ArrayList<>();
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PUMP_SCHEDULE+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PUMP_SCHEDULE_STAGE_FIND_BY_JOB_ID_AND_WELL_IDE )
                            .queryParam("jobId", jobId)
                            .queryParam("wellId", wellId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PumpSchedule>>() {
                    })
                    .block();

        }catch (Exception e){
            logger.error("Error in PumpScheduleService ::  findByJobIdAndWellIdFromService "+e.getMessage());
            results = pumpScheduleRepository.findByJobIdAndWellId(jobId,wellId);
        }
        return results;
    }
}
