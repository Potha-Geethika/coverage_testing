package com.carbo.job.services;

import com.carbo.job.config.WebClientConfig;
import com.carbo.job.model.FieldTicket;
import com.carbo.job.model.FieldTicketServiceRequest;
import com.carbo.job.model.proposal.Proposal;
import com.carbo.job.repository.FieldTicketMongoDbRepository;
import com.carbo.job.repository.ProposalMongoDbRepository;
import com.carbo.job.utils.ActivityLogUtil;
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
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProposalService {

    private static final Logger logger = LoggerFactory.getLogger(ProposalService.class);

    private final ProposalMongoDbRepository proposalRepository;
    private final FieldTicketMongoDbRepository fieldTicketMongoDbRepository;

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Autowired
    private SslContext sslContext;
    private WebClient webClient = null;

    private WebClient fleetWebClient = null;
    @Autowired
    WebClientConfig webClientConfig;

    @Autowired
    public ProposalService(ProposalMongoDbRepository proposalRepository,FieldTicketMongoDbRepository fieldTicketMongoDbRepository) {
        this.proposalRepository = proposalRepository;
        this.fieldTicketMongoDbRepository = fieldTicketMongoDbRepository;
    }

    public List<Proposal> getAll(HttpServletRequest request) {
        //return proposalRepository.findAll();
        return getAllFromService(request);
    }

    public List<Proposal> getByJobId(HttpServletRequest request,String jobId) {
        //return proposalRepository.findByJobId(jobId);
        return findByJobIdFromService(request, jobId);
    }

    public List<Proposal> getByJobIdAndWellId(HttpServletRequest request, String jobId, String wellId) {
        //return proposalRepository.findByJobIdAndWellId(jobId, wellId);
        return getAllByJobIdAndWellIdFromService(request, jobId, wellId);
    }

    public Optional<Proposal> getById(HttpServletRequest request, String id) {
        //return proposalRepository.findById(id);
        return findByIdFromService(request, id);
    }

    public Proposal saveProposal(HttpServletRequest request, Proposal job) {
        //return proposalRepository.save(job);
        return saveProposalFromService(request, job);
    }

    public Proposal updateProposal(HttpServletRequest request, Proposal job) {
        //return proposalRepository.save(job);
        return updateProposalFromService(request, job);
    }

    public void deleteProposal(HttpServletRequest request, String jobId) {
        //proposalRepository.deleteById(jobId);
        deleteByIdFromService( request,  jobId);
    }

    public void copyFieldTicket(HttpServletRequest request, String jobId, String well, String name, FieldTicket fieldticket) {
        /*
        List<FieldTicket> existingFieldTicketList = fieldTicketMongoDbRepository.findByJobIdAndWellAndName(jobId,well,name);
        if ( !existingFieldTicketList.isEmpty() ) { // if Field Ticket exist then update the versions
            existingFieldTicketList.get(0).setVersions(fieldticket.getVersions());
            fieldTicketMongoDbRepository.save(existingFieldTicketList.get(0));
        } else { // If Field Ticket doesn't exist then create new one by passing id as a null in the input FieldTicket object
            fieldticket.setJobId(jobId);
            fieldticket.setWell(well);
            fieldticket.setName(name);
            fieldticket.setId(null);
            fieldTicketMongoDbRepository.save(fieldticket);
        }
*/
        copyFieldTicketFromService(request, jobId,  well,  name,  fieldticket);
    }

    public Optional<Proposal> findByIdFromService(HttpServletRequest request, String proposalId) {

        logger.info("<-------------Invoked ProposalService :: findByIdFromService ------------->");
        logger.info("proposalId  : " + proposalId);
        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        Optional<Proposal> results = null;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+ WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PROPOSAL_FIND_BY_ID)
                            .queryParam("proposalId", proposalId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Proposal.class)
                    .blockOptional();
        } catch (Exception e) {
            logger.error("Error while connecting to /findById " + e.getMessage());
            results = proposalRepository.findById(proposalId);
        }
        return results;
    }


    public List<Proposal> findByJobIdFromService(HttpServletRequest request, String jobId) {

        logger.info("<-------------Invoked ProposalService :: findByJobIdFromService ------------->");
        logger.info("jobId  : " + jobId);
        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        List<Proposal> results = new ArrayList<>();
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PROPOSAL_FIND_BY_JOB_ID)
                            .queryParam("jobId", jobId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Proposal>>() {
                    }).block();

        } catch (Exception e) {
            logger.error("Error while connecting to /findByJobId " + e.getMessage());
            results = proposalRepository.findByJobId(jobId);
        }
        return results;
    }

    public List<Proposal> getAllFromService(HttpServletRequest request) {

        logger.info("<-------------Invoked ProposalService :: findByJobIdFromService ------------->");
        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        List<Proposal> results = new ArrayList<Proposal>();
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();

            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PROPOSAL_GET_ALL)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Proposal>>() {
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Error while connecting to /findByJobId " + e.getMessage());
            results = proposalRepository.findAll();
        }
        return results;
    }

    public List<Proposal> getAllByJobIdAndWellIdFromService(HttpServletRequest request, String jobId, String wellId) {

        logger.info("<-------------Invoked ProposalService ::  getAllByJobIdAndWellIdFromService ------------->");
        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        List<Proposal> results = new ArrayList<>();
        try {

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();
            results = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.PROPOSAL_FIND_BY_JOB_ID_AND_WELL_ID)
                            .queryParam("jobId", jobId)
                            .queryParam("wellId", wellId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Proposal>>() {
                    })
                    .block();

        } catch (Exception e) {
            logger.error("Error while connecting to /getAllByJobIdAndWellIdFromService " + e.getMessage());
            results = proposalRepository.findByJobIdAndWellId(jobId, wellId);
        }
        return results;
    }

    public Proposal saveProposalFromService(HttpServletRequest request, Proposal proposal) {

        logger.info("<-------------Invoked ProposalService :: saveProposalFromService ------------->");
        logger.info("proposal " + proposal);

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        String authToken = (String) headerValueMap.get("authorization");
        Proposal results = null;
        try {

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();

            results = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.SAVE_PROPOSAL)
                            .build())
                    .body(Mono.just(proposal), Proposal.class)
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Proposal.class)
                    .block();

        } catch (Exception e) {
            logger.error("Error while connecting to saveProposal API  " + e.getMessage());
            proposalRepository.save(proposal);
        }
        return results;
    }

    public Proposal updateProposalFromService(HttpServletRequest request, Proposal proposal) {

        logger.info("<-------------Invoked ProposalService :: updateProposalFromService ------------->");
        logger.info("proposal " + proposal);

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        logger.info("headerValueMap" + headerValueMap);

        String authToken = (String) headerValueMap.get("authorization");
        Proposal results = null;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();

            results = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.UPDATE_PROPOSAL)
                            .build())
                    .body(Mono.just(proposal), Proposal.class)
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Proposal.class)
                    .block();

        } catch (Exception e) {
            logger.error("Error while connecting to updateProposal API  " + e.getMessage());
            proposalRepository.save(proposal);
        }
        return results;
    }

    public void deleteByIdFromService(HttpServletRequest request, String jobId) {

        logger.info("<-------------Invoked ProposalService :: deleteByIdFromService ------------->");
        logger.info("jobId " +jobId);

        //Getting all headers into map
        Map<String, String> headerValueMap = ActivityLogUtil.getHeadersInfo(request);
        logger.info("headerValueMap"+headerValueMap);
        String authToken = (String)headerValueMap.get("authorization");
        Void results = null;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            if(webClient==null)
            webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.PROPOSAL_SERVICE+"/")
                    .build();

            results = webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.DELETE_PROPOSAL)
                            .queryParam("proposalId", jobId)
                            .build())
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }catch (Exception e){
            logger.error("Error while connecting to saveActivityLogEntry API  "+e.getMessage());
            proposalRepository.deleteById(jobId);
        }
    }

    public void copyFieldTicketFromService(HttpServletRequest request, String jobId, String well, String name, FieldTicket fieldticket) {

        logger.info("<-------------Invoked ProposalService :: copyFieldTicketFromService ------------->");
        logger.info("jobId "+jobId);
        logger.info("well "+well);
        logger.info("name "+name);
        logger.info("fieldticket "+fieldticket);

        //Getting all headers into map
        Map<String, String> headerValueMap = WebClientUtils.getHeadersInfo(request);

        String authToken = (String)headerValueMap.get("authorization");
        FieldTicketServiceRequest fieldTicketServiceRequest = new FieldTicketServiceRequest();
        fieldTicketServiceRequest.setJobId(jobId);
        fieldTicketServiceRequest.setName(name);
        fieldTicketServiceRequest.setWell(well);
        fieldTicketServiceRequest.setFieldticket(fieldticket);
        Void results = null;
        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            //if(fleetWebClient==null)
            fleetWebClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(webClientConfig.getBaseUrl()+"/"+WebClientUtils.FIELD_TICKET_SERVICE+"/")
                    .build();

            results = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(WebClientUtils.FIELD_TICKET_FIND_COPY_TICKET)
                            .build())
                    .body(Mono.just(fieldTicketServiceRequest), FieldTicketServiceRequest.class)
                    .header("Authorization",authToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

        }catch (Exception e){
            logger.error("Error while connecting to v1/field-tickets/copyFieldTicket "+e.getMessage());
            List<FieldTicket> existingFieldTicketList = fieldTicketMongoDbRepository.findByJobIdAndWellAndName(jobId,well,name);
            if ( !existingFieldTicketList.isEmpty() ) { // if Field Ticket exist then update the versions
                existingFieldTicketList.get(0).setVersions(fieldticket.getVersions());
                fieldTicketMongoDbRepository.save(existingFieldTicketList.get(0));
            } else { // If Field Ticket doesn't exist then create new one by passing id as a null in the input FieldTicket object
                fieldticket.setJobId(jobId);
                fieldticket.setWell(well);
                fieldticket.setName(name);
                fieldticket.setId(null);
                fieldTicketMongoDbRepository.save(fieldticket);
            }
        }
    }


}
