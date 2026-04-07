package com.carbo.job.utils;


import com.carbo.job.config.WebClientConfig;
import com.carbo.job.services.ChemicalStageService;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class WebClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebClientUtils.class);

    public static final String ACTIVITY_LOG = "activitylog";
    public static final String CHEMICAL_STAGE = "chemicalstage";
    public static final String PROPANT_STAGE = "proppantstage";
    public static final String PUMP_SCHEDULE = "pumpschedule";
    public static final String AUTHENTICATION = "auth";
    public static final String WELL_INFO = "wellinfo";
    public static final String SERVICE_ACCOUNT = "serviceaccount";
    public static final String WELL_SERVICE = "well";
    public static final String PUMP_SCHEDULE_JOB_CFG_SERVICE = "pumpschedulejobcfg";

    public static final String MIGRATION_STATUS_ENTRY = "migrationstatusentry";
    public static final String PROPOSAL_SERVICE = "proposal";
    public static final String EMAIL = "email";
    public static final String FLEET_SERVICE = "fleet";
    public static final String FIELD_TICKET_SERVICE = "fieldticket";




    public static final String GET_ACTIVITY_BY_ORG_ID_AND_JOB_ID = "v1/activity-logs/getActivityByOrganizationIdAndJobId";

    public static final String SAVE_UPDATE_ACTIVITY_API = "v1/activity-logs/";
    public static final String CHEMICAL_STAGE_GET_ALL_RECORDS_BY_JOB_ID_AND_WELL_ID_AND_STAGE_ORG =  "v1/chemical-stages/getAllRecordsByJobIdAndWellAndStageAndOrganizationId";
    public static final String PROPANT_STAGE_GET_BY_ORG_ID_AND_JOB_ID_AND_WELL_AND_STAGE =  "v1/proppant-stages/getByOrganizationIdAndJobIdAndWellAndStage";

    public static final String PUMP_SCHEDULE_STAGE_FIND_BY_JOB_ID_AND_WELL_IDE =  "v1/pump-schedules/findByJobIdAndWellId";
    public static final String AUTHENTICATION_FIND_BY_USER_NAME =  "/auth/findByUserName";
    public static final String WELL_INFO_BY_WELL_ID =  "v1/well-info/findByWellId";

    public  static final String SERVICE_ACCOUNT_FIND_BY_ID = "v1/service-accounts/findById";

    public  static final String WELLSERVICE_FIND_BY_API = "v1/wells/findByApi";
    public  static final String PROPOSAL_FIND_BY_ID = "v1/proposal/findById";
    public  static final String PROPOSAL_FIND_BY_JOB_ID = "v1/proposal/getProposalByJobId";
    public  static final String PROPOSAL_GET_ALL = "v1/proposal/getAll";
    public  static final String PROPOSAL_FIND_BY_JOB_ID_AND_WELL_ID = "v1/proposal/findByJobIdAndWellId";
    public  static final String PROPOSAL_DELETE_BY_ID = "v1/proposal/delete";
    public  static final String SAVE_PROPOSAL = "v1/proposal/save";
    public  static final String UPDATE_PROPOSAL = "v1/proposal/update";
    public  static final String DELETE_PROPOSAL = "v1/proposal/delete";

    
    public static final String MIGRATION_STATUS_ENTRY_FIND_BY_API_FIND_BY_JOBID_AND_MIGRATION_TYPE ="v1/migration-status-entry/findByJobIdAndMigrationType" ;

    public  static final String PUMP_SCHEDULE_JOB_CFG_FIND_BY_JOB_ID = "v1/pump-schedule-job-cfg/findByJobId";
    
    public static final String DAILY_JOB_RECORD = "dailyjobrecord";

    public  static final String DAILY_JOB_RECORD_FIND_BY_ORGANIZATION_ID = "v1/daily-job-record/getByOrganizationId";

    public  static final String DAILY_JOB_RECORD_FIND_BY_SHARE_ORGANIZATION_ID = "v1/daily-job-record/getBySharedOrganizationId";

    public  static final String EMAIL_FIND_EMAIL_BY_ORG_ID_AND_JOB_ID_AND_TYPE_AND_WELL_AND_STAGE = "v1/emails/findEmailByOrganizationIdAndJobIdAndTypeAndWellAndStage";
    public  static final String EMAIL_FIND_EMAIL_BY_ORG_ID_AND_TYPE_AND_WELL_AND_STAGE = "v1/emails/findEmailByOrganizationIdAndTypeAndWellAndStage";
    public  static final String FLEET_FIND_DISTINCT_BY_ORGID_AND_NAME =  "v1/fleets/findDistinctByOrganizationIdAndName";
    public  static final String FIELD_TICKET_FIND_COPY_TICKET =  "v1/field-tickets/copyFieldTicket";

    public  static final String ACTIVITY_LOG_SAVE_UPDATE_ACTIVITY_LOG =  "v1/activity-logs/";

    public static final String DELETE_IRON_FAILURE_SAFETY_REPORT = "ironfailure/v1/reports/deleteSafetyReportsByJobId";


    public static WebClient initWebClient(WebClient.Builder webClientBuilder,Map<String, String> headerValueMap, String apiName, String baseUrl){
        logger.info("Inside initWebClient : ");

        WebClient newWebClient = null;
        try {

            if(baseUrl == null) {
                throw new Exception("Base URL not defined Exception.");
            }
            String baseUrlWithApi = baseUrl+apiName+"/";

            logger.info("baseUrlwithApi  "+baseUrlWithApi);

            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

            newWebClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(baseUrlWithApi)
                    .build();

        }catch(Exception e){
            logger.error("Error while Initilizing webclient "+e.getMessage());
            e.printStackTrace();
        }
        return newWebClient;

    }


    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
