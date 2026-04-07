package com.carbo.job.controllers.external;

import com.carbo.job.constants.ErrorCodes;
import com.carbo.job.model.*;
import com.carbo.job.model.well.StageInfo;
import com.carbo.job.model.well.WellResponse;
import com.carbo.job.model.well.Wells;
import com.carbo.job.model.widget.PriceBookComponents;
import com.carbo.job.model.widget.PriceBookTypeEnum;
import com.carbo.job.repository.GeneralSettingMongoDbRepository;
import com.carbo.job.repository.JobMongoDbRepository;
import com.carbo.job.services.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import com.sun.jersey.api.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import static com.carbo.job.utils.ControllerUtil.*;

@RestController
@RequestMapping(value = "v1/jobs/external")
public class ExternalJobServiceController extends RestCalls{
    private static final Logger logger = LoggerFactory.getLogger(ExternalJobServiceController.class);

    private final ExternalJobService externalJobService;
    private final UserService userService;
    private final ServiceAccountService serviceAccountService;
    private final WellInfoService wellInfoService;
    private final WellService wellService;
    private final FleetService fleetService;
    private final ProppantService proppantService;
    private final JobService jobService;
    private final GeneralSettingMongoDbRepository generalSettingMongoDbRepository;

    private final ProppantDeliveryService proppantDeliveryService;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ExternalJobServiceController(ExternalJobService externalJobService, UserService userService, ServiceAccountService serviceAccountService,
            WellInfoService wellInfoService, WellService wellService, FleetService fleetService, ProppantService proppantService, JobService jobService, ProppantDeliveryService proppantDeliveryService,
            JobMongoDbRepository jobMongoDbRepository, GeneralSettingMongoDbRepository generalSettingMongoDbRepository, MongoTemplate mongoTemplate) {
        this.externalJobService = externalJobService;
        this.serviceAccountService = serviceAccountService;
        this.userService = userService;
        this.wellInfoService = wellInfoService;
        this.wellService = wellService;
        this.fleetService = fleetService;
        this.proppantService = proppantService;
        this.jobService = jobService;
        this.proppantDeliveryService = proppantDeliveryService;
        this.generalSettingMongoDbRepository = generalSettingMongoDbRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<SimplifiedJob> getJobs(HttpServletRequest request) {
        String organizationType = getOrganizationType(request);
        List<SimplifiedJob> result = new ArrayList<>();
        if (organizationType.contentEquals("PARTNER")) {
            String userName = getUserName(request);
            Optional<User> userInfo = userService.getUserByUserName(request, userName);
            String userId = userInfo.get().getId();
            Optional<ServiceAccount> serviceAccount = serviceAccountService.get(request, userId);
            if (serviceAccount.isPresent()) {
                List<String> organizationIds = serviceAccount.get().getOrganizationIds();
                for (String id : organizationIds) {
                    List<SimplifiedJob> temp = externalJobService.getByOrganizationIdExternal(id);
                    for (SimplifiedJob job : temp) {
                        result.add(job);
                    }
                }
            }
        }

        return result;
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    public SimplifiedJob getJob(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        String organizationType = getOrganizationType(request);
        SimplifiedJob result = null;
        if (organizationType.contentEquals("PARTNER")) {
            String userName = getUserName(request);
            Optional<User> userInfo = userService.getUserByUserName(request, userName);
            String userId = userInfo.get().getId();
            Optional<ServiceAccount> serviceAccount = serviceAccountService.get(request, userId);
            if (serviceAccount.isPresent()) {
                List<String> organizationIds = serviceAccount.get().getOrganizationIds();
                result = externalJobService.getJob(jobId).get();
                if (organizationIds.contains(result.getOrganizationId())) {
                    return result;
                } else {
                    throw new AccessDeniedException("This account do not have access");
                }
            }
        }
        return result;
    }


    @RequestMapping(value = "/simplified/", method = RequestMethod.PUT)
    public WellResponse saveSimplified(HttpServletRequest request, @RequestParam(name = "wellAPI") String wellAPI,
                                       @RequestParam(name = "stage") String stage,
                                       @RequestParam(name = "diesel", required = false) Float diesel,
                                       @RequestParam(name = "fieldGas", required = false) Float fieldGas,
                                       @RequestParam(name = "cng", required = false) Float cng,
                                       @RequestParam(name = "times", required = false) String times) {


        logger.info(" diesel = {} : fieldGas = {} : cng = {}  : timeStart = {} : timeEnd = {} ", diesel, fieldGas, cng, times);
        logger.info(" wellAPI : {} ", wellAPI);
        Optional<Wells> well = wellService.getByWellAPI(request, wellAPI);
        logger.info(" well  : {} ", well.isPresent());
        WellResponse wellResponse = new WellResponse();
        if (well.isPresent()) {
            List<StageInfo> stageInfoList = well.get().getStageInfo();
            if (stageInfoList == null) {
                // initialize the list with a single stage info object
                stageInfoList = new ArrayList<>();
                StageInfo stageInfo = new StageInfo();
                stageInfo.setStageNumber("1");
                stageInfoList.add(stageInfo);
                well.get().setStageInfo(stageInfoList);
                wellService.updateWell(well.get());
            }
            for (int j = 0; j < well.get().getTotalStages(); j++) {
                if (j == stageInfoList.size()) {
                    StageInfo stageInfo = new StageInfo();
                    stageInfo.setStageNumber(Integer.toString(j + 1));
                    logger.info("stage number : {}", stageInfo.getStageNumber());
                    well.get().getStageInfo().add(stageInfo);
                    wellService.updateWell(well.get());
                }
            }
            logger.info(" wellId  : {} ", well.get().getId());
            if (!well.get().getStageInfo().isEmpty()) {
                final List<String> stageNumberList = new ArrayList<>();
                well.get().getStageInfo().stream().forEach(e -> {
                    logger.info(" well Info Stage Number  : {}, input stage number : {} ", e.getStageNumber(), stage);
                    if (e.getStageNumber().equalsIgnoreCase(stage)) {
                        stageNumberList.add(e.getStageNumber());
                        if (diesel != null && diesel >= 0.0) {
                            e.setDiesel(diesel);
                        }
                        if (fieldGas != null && fieldGas >= 0.0) {
                            e.setFieldGas(fieldGas);
                        }
                        if (cng != null && cng >= 0.0) {
                            e.setCng(cng);
                        }
                        if (times != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode timesNodee = null;
                            String resultStr = null;
                            try {
                                timesNodee = mapper.readTree(times);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }
                            JsonNode resultNode = mapper.createObjectNode().set("times", timesNodee);
                            try {
                                resultStr = mapper.writeValueAsString(resultNode);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode jsonNode = null;
                            try {
                                jsonNode = objectMapper.readTree(resultStr);
                            } catch (JsonProcessingException ex) {
                                throw new RuntimeException(ex);
                            }

                            JsonNode timesNode = jsonNode.get("times");
                            Long timeStart = null;
                            Long timeEnd = null;

                            if (timesNode.isArray()) {
                                List<String> pumpTimeList = new ArrayList<>();
                                for (JsonNode timeNode : jsonNode.get("times")) {
                                    timeStart = timeNode.get("timeStart").asLong();
                                    timeEnd = timeNode.get("timeEnd").asLong();

                                    if (timeStart != null && timeEnd != null && timeStart < timeEnd) {
                                        Instant timestamp1 = Instant.ofEpochMilli(timeStart * 1000);
                                        Instant timestamp2 = Instant.ofEpochMilli(timeEnd * 1000);
                                        Duration duration = Duration.between(timestamp1, timestamp2);
                                        long minutes = duration.plusSeconds(30).toMinutes();
                                        e.setTimeStart(timeStart);
                                        e.setTimeEnd(timeEnd);
                                        pumpTimeList.add(String.valueOf(minutes));
                                        logger.info("time 1 and time 2 = {} : {} ", timestamp1, timestamp2);
                                        logger.info("inside start time, end time, and hours duration set: {} : {} : {} : =", timeStart, timeEnd, minutes, duration);
                                    }
                                }
                                e.setPumpTime(pumpTimeList);
                            }
                            logger.info(" Stage Number  : {}, Diesel : {}, field gas : {},cng :{} ", e.getStageNumber(), e.getDiesel(), e.getFieldGas(), e.getCng());
                        }
                        wellService.updateWell(well.get());
                    }
                });

                //made an understandable response for the stage for which the value is updated
                wellResponse.setApi(well.get().getApi());
                wellResponse.setAfeNumber(well.get().getAfeNumber());
                wellResponse.setLatitude(well.get().getLatitude());
                wellResponse.setName(well.get().getName());
                wellResponse.setLongitude(well.get().getLongitude());
                wellResponse.setFracproId(well.get().getFracproId());
                wellResponse.setTs(well.get().getTs());
                wellResponse.setOperatorId(well.get().getOperatorId());
                wellResponse.setPadId(well.get().getPadId());
                wellResponse.setOrganizationId(well.get().getOrganizationId());
                wellResponse.setTotalStages(well.get().getTotalStages());
                wellResponse.setUpdatedStage(well.get().getStageInfo().get(Integer.parseInt(stage) - 1));

            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCodes.WELL_STAGE_NOT_NOT_FOUND.getCode() + " - " + ErrorCodes.WELL_STAGE_NOT_NOT_FOUND.getMessage() + " : " + wellAPI + ", wellId : " + well.get().getId());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCodes.WELL_NOT_NOT_FOUND.getCode() + " - " + ErrorCodes.WELL_NOT_NOT_FOUND.getMessage() + " : " + wellAPI);
        }
        return wellResponse;
    }

    @PostMapping("/delivery-tracking")
    public String saveDeliveryRecord(HttpServletRequest request, @RequestBody DeliveryRecordRequest deliveryRecordRequest) {
        String organizationId = getOrganizationId(request);
        String jobNumber = deliveryRecordRequest.getNavId();
        int orderStatusID = deliveryRecordRequest.getOrderStatusID();

        if (deliveryRecordRequest.getCustomerID() != null && deliveryRecordRequest.getCustomerID().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerID must be 30 characters or less.");
        }
        List<GeneralSetting> generalSettingList=generalSettingMongoDbRepository.findByOrganizationId(organizationId);
        if (ObjectUtils.isEmpty(generalSettingList)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCodes.AUTOMATIZE_TOGGLE_OFF.getCode() + " - " + ErrorCodes.GENERAL_SETTING_DATA_NOT_FOUND.getMessage() + " : " + jobNumber);
        }
        GeneralSetting generalSetting=generalSettingList.get(0);
        if (!generalSetting.getAutomatize()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCodes.AUTOMATIZE_TOGGLE_OFF.getCode() + " - " + ErrorCodes.AUTOMATIZE_TOGGLE_OFF_IN_GENERAL_SETTING.getMessage());
        }

        SimplifiedJob job = externalJobService.getByJobNumberAndOrganizationId(jobNumber, organizationId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCodes.JOB_NOT_FOUND.getCode() + " - " + ErrorCodes.JOB_NOT_FOUND.getMessage() + " : " + jobNumber);
        }
        if (!job.isAutomatize()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCodes.AUTOMATIZE_TOGGLE_OFF.getCode() + " - " + ErrorCodes.AUTOMATIZE_TOGGLE_OFF.getMessage() + " : " + jobNumber);
        }
        if (deliveryRecordRequest.getAutoOrderId()!=0) {
            Optional<ProppantDeliveryEntry> proppantDelivery = proppantDeliveryService.findByOrganizationIdAndAutoOrderId(organizationId, deliveryRecordRequest.getAutoOrderId());
            Optional<ProppantDeliveryEntry> proppantDeliveryEntryObj = proppantDeliveryService.findByJobIdAndBolAndPoAndAutoOrderId(job.getId(),
                    deliveryRecordRequest.getBol(), deliveryRecordRequest.getPo(), deliveryRecordRequest.getAutoOrderId());
            if (proppantDelivery.isPresent()) {
                if (!proppantDelivery.get().getJobId().equals(job.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCodes.ALREADY_EXISTS_AUTO_ORDER_ID.getCode() + " - " + ErrorCodes.ALREADY_EXISTS_AUTO_ORDER_ID.getMessage() + " : " + deliveryRecordRequest.getAutoOrderId());
                } else if (!proppantDeliveryEntryObj.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCodes.ALREADY_EXISTS_AUTO_ORDER_ID.getCode() + " - " + ErrorCodes.ALREADY_EXISTS_AUTO_ORDER_ID.getMessage() + " : " + deliveryRecordRequest.getAutoOrderId());
                }
            }
        }
        List<Vendor> vendors = job.getVendors();
        String vendorName = null;
        String proppantId = null;
        for (Vendor vendor : vendors) {
            List<PoItem> poItems = vendor.getPoItems();
            if(!ObjectUtils.isEmpty(poItems)) {
                for (PoItem poItem : poItems) {
                    if (deliveryRecordRequest.getPo().equals(poItem.getPoNumber())) {
                        vendorName = vendor.getName();
                        proppantId = poItem.getProppantId();
                        break;
                    }
                }
                if (vendorName != null) {
                    break;
                }
            }
        }
        if (vendorName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found for PO number: " + deliveryRecordRequest.getPo());
        }
      PriceBookComponents proppant = proppantService.getProppant(proppantId,organizationId);
        if (ObjectUtils.isEmpty(proppant)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCodes.PROPPANT_NOT_FOUND.getCode() + " - " + ErrorCodes.PROPPANT_NOT_FOUND.getMessage());
        }
        Optional<ProppantDeliveryEntry> proppantDeliveryEntry = proppantDeliveryService.findByJobIdAndProppantAndBolAndPoWithoutSource(job.getId(), proppant.getName(), deliveryRecordRequest.getBol(), deliveryRecordRequest.getPo());
        Optional<ProppantDeliveryEntry> proppantDeliveryEntryByJobId = proppantDeliveryService.findByJobId(job.getId());
        boolean isSplit = false;
        List<ProppantDeliveryEntry> proppantDeliveryEntriesSplit = new ArrayList<>();
        if(job.getProppantSchematicType().equals("split")){
            isSplit = true;
            proppantDeliveryEntriesSplit = proppantDeliveryService.findByJobIdAndProppantAndBolAndPo(job.getId(), proppant.getName(), deliveryRecordRequest.getBol(), deliveryRecordRequest.getPo());
        }
        if (isOrderStatusValid(orderStatusID)) {
            proppantDeliveryService.mapper(deliveryRecordRequest, organizationId, jobNumber, job, proppantDeliveryEntry, proppantDeliveryEntryByJobId, proppant, vendorName, isSplit, proppantDeliveryEntriesSplit);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCodes.ORDER_ID_MISMATCHED.getCode() + " - " + ErrorCodes.ORDER_ID_MISMATCHED.getMessage());
        }
        return "Delivery record saved successfully.";
    }

    private boolean isOrderStatusValid(int orderStatusID) {
        return orderStatusID == 3 || orderStatusID == 4 || orderStatusID == 6;
    }

    @PutMapping("/saveDistrictForCompletedJobs")
    public String saveDistrictForCompletedJobs(HttpServletRequest request){
        List<Job> completedJobs = jobService.getByStatus("Completed");
        for(Job job : completedJobs) {
            String fleetName=job.getFleet();
            if(fleetName != null) {
                Optional<Fleet> fleet = fleetService.getByName(request, job.getOrganizationId(), fleetName);
                if (fleet.isPresent()) {
                    String districtId = fleet.get().getDistrictId();
                    job.setDistrictWhenCompleted(districtId);
                }
            }
        }
        return "Records updated successfully";
    }


    @Scheduled(cron = "0 */30 * * * *") // Every 30 Minutes
    public void updateNavId() {
        Map<String , String> requestBodyForToken = new HashMap<>();
        String secret = "4859ea5a-e9c8-4b4b-8f58-693701369c3f";
        requestBodyForToken.put("secret", secret);
        Client client = Client.create();
        UserToken token = getToken(requestBodyForToken, client);
        if(!ObjectUtils.isEmpty(token.getToken())){
            String bearerToken = "Bearer jwt " + token.getToken();
            Query queryToFindProppantDeliveryEntry = new Query();
            queryToFindProppantDeliveryEntry.addCriteria(Criteria.where("autoOrderId").exists(true).and("delivered").is(false));
            queryToFindProppantDeliveryEntry.fields().include("id").include("jobId").include("autoOrderId").include("organizationId").include("wtAmount");
            List<ProppantDeliveryEntry> proppantDeliveryEntries = mongoTemplate.find(queryToFindProppantDeliveryEntry, ProppantDeliveryEntry.class);
            for(ProppantDeliveryEntry proppantDeliveryEntry : proppantDeliveryEntries){
                if(!ObjectUtils.isEmpty(proppantDeliveryEntry.getAutoOrderId()) && proppantDeliveryEntry.getAutoOrderId() != 0){
                    BolResponse bolResponse = getBolRecord(secret, proppantDeliveryEntry.getAutoOrderId(), bearerToken, client);
                    if(!ObjectUtils.isEmpty(bolResponse.getMessage()) && bolResponse.getMessage().equals("Successfully retrieved bol.")) {
                        Query jobQuery = new Query();
                        jobQuery.addCriteria(Criteria.where("jobNumber").is(bolResponse.getInfo().getNavId()).and("organizationId").is(proppantDeliveryEntry.getOrganizationId()));
                        Job job = mongoTemplate.findOne(jobQuery, Job.class);
                        if (!ObjectUtils.isEmpty(job) && (!proppantDeliveryEntry.getJobId().equals(job.getId()))) {
                                Query query = new Query();
                                query.addCriteria(Criteria.where("jobId").is(proppantDeliveryEntry.getJobId()));
                                query.addCriteria(Criteria.where("autoOrderId").is(proppantDeliveryEntry.getAutoOrderId()));
                                Update update = new Update();
                                update.set("jobId", job.getId());
                                update.set("transferredFromJobId", proppantDeliveryEntry.getJobId());
                                update.set("status", "waiting");
                                update.set("bolQuantity", proppantDeliveryEntry.getWtAmount());
                                mongoTemplate.updateFirst(query, update, ProppantDeliveryEntry.class);
                        }
                    }
                }
            }
        }
    }

    @RequestMapping(value = "/mappingDesiredItemsToJob", method = RequestMethod.PUT)
    public Map<String, Map<String, String>> mappingDesiredItemsToJob() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            logger.info("Starting mappingDesiredItemsToJob API");
            Query jobQuery = new Query();
            jobQuery.addCriteria(Criteria.where("proposalId").is(null));
            List<Job> allJobs = mongoTemplate.find(jobQuery, Job.class);
            logger.info("Fetched {} jobs", allJobs.size());

            Set<String> organizationIds = allJobs.stream().map(Job::getOrganizationId).collect(Collectors.toSet());
            Map<String, List<Job>> jobsByOrg = allJobs.stream().collect(Collectors.groupingBy(Job::getOrganizationId));

            Query query = new Query();
            query.addCriteria(Criteria.where("priceBookName").is("XOPS Default Pricebook"));
            query.addCriteria(Criteria.where("organizationId").in(organizationIds));
            List<PriceBook> priceBooks = mongoTemplate.find(query, PriceBook.class);
            logger.info("Fetched {} priceBooks", priceBooks.size());

            List<String> defaultPriceBookIds = priceBooks.stream().map(PriceBook::getId).collect(Collectors.toList());

            Query priceBookComponentsQuery = new Query();
            priceBookComponentsQuery.addCriteria(Criteria.where("priceBookId").in(defaultPriceBookIds));
            priceBookComponentsQuery.addCriteria(Criteria.where("priceBookType").is(PriceBookTypeEnum.CHEMICAL));
            List<PriceBookComponents> allPriceBookComponents = mongoTemplate.find(priceBookComponentsQuery, PriceBookComponents.class);
            logger.info("Fetched {} priceBookComponents", allPriceBookComponents.size());

            Map<String, List<PriceBookComponents>> priceBookComponentsByOrg = allPriceBookComponents.stream()
                                                                                                    .collect(Collectors.groupingBy(PriceBookComponents::getOrganizationId));
            Map<String, Map<String, String>> organizationAndJobMap = new HashMap<>();
            for (String organizationId : organizationIds) {
                logger.info("Processing organization: {}", organizationId);
                List<Job> jobs = jobsByOrg.get(organizationId);
                List<PriceBookComponents> priceBookComponentsForOrganization = priceBookComponentsByOrg.get(organizationId);
                Map<String, String> jobIdAndJobNumberMap = new HashMap<>();
                if (!ObjectUtils.isEmpty(priceBookComponentsForOrganization)) {
                    for (Job job : jobs) {
                        try {
                            logger.info("Processing job: {}", job.getId());
                            boolean isChanged = false;
                            String desiredStrength = "";
                            String acidType = "";

                            if (!ObjectUtils.isEmpty(job.getWells())) {
                                for (Well well : job.getWells()) {
                                    if (!ObjectUtils.isEmpty(well.getInProgressChemicalStage()) && !ObjectUtils.isEmpty(well.getInProgressChemicalStage().getIsosTransport())) {
                                        for (Strap strap : well.getInProgressChemicalStage().getIsosTransport()) {
                                            if (!ObjectUtils.isEmpty(strap.getDesiredStrength())) {
                                                desiredStrength = strap.getDesiredStrength();

                                                if (ObjectUtils.isEmpty(strap.getDesireStrengthFromChemicalId()) && (!ObjectUtils.isEmpty(strap.getChemical()))) {
                                                    String chemicalId = strap.getChemical().getId();

                                                    for (PriceBookComponents component : priceBookComponentsForOrganization) {
                                                        if (component.getId().equals(chemicalId)) {
                                                            if (!ObjectUtils.isEmpty(component.getAcid())) {
                                                                acidType = component.getAcid();

                                                                for (PriceBookComponents priceBookComponent : priceBookComponentsForOrganization) {
                                                                    if (!ObjectUtils.isEmpty(priceBookComponent.getAcid()) && !ObjectUtils.isEmpty(priceBookComponent.getDilutionRate())) {
                                                                        if (priceBookComponent.getAcid().equals(acidType) && priceBookComponent.getDilutionRate().equals(desiredStrength)) {
                                                                            if (!ObjectUtils.isEmpty(priceBookComponent.getName()) && !ObjectUtils.isEmpty(priceBookComponent.getItemCode())) {
                                                                                strap.setDesireStrengthFromChemicalId(priceBookComponent.getId());
                                                                                strap.setDesiredDescription(priceBookComponent.getName());
                                                                                strap.setDesiredItemCode(priceBookComponent.getItemCode());
                                                                                isChanged = true;
                                                                                logger.info("Updated strap for job: {}", job.getId());
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isChanged) {
                                mongoTemplate.save(job);
                                jobIdAndJobNumberMap.put(job.getId(), job.getJobNumber());
                                logger.info("Successfully updated job: {}", job.getId());
                            }
                        } catch (Exception e) {
                            logger.error("Error processing job: {} - {}", job.getId(), e.getMessage(), e);
                        }
                    }
                }
                if(!jobIdAndJobNumberMap.isEmpty()) {
                    organizationAndJobMap.put(organizationId, jobIdAndJobNumberMap);
                }
            }
            logger.info("Completed mappingDesiredItemsToJob API");
            return organizationAndJobMap;
        } catch (Exception e) {
            logger.error("Error in mappingDesiredItemsToJob API: {}", e.getMessage(), e);
        }
        return Collections.emptyMap();
    }
}
