// Unresolved import (framework/JDK): org.springframework.beans.factory.annotation.Autowired
// ===== Imported from: com.carbo.job.services.widget.WidgetService =====
package com.carbo.job.services.widget;

import com.carbo.job.model.*;
import com.carbo.job.model.widget.Error;
import com.carbo.job.model.widget.*;
import com.carbo.job.repository.*;
import com.carbo.job.utils.Constants;
import com.carbo.proppantstage.model.ProppantContainer;
import com.carbo.ws.model.ProppantStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.carbo.job.model.widget.ChemicalEnum.*;
import static com.carbo.job.model.widget.ErrorConstants.*;
import static com.carbo.job.utils.CommonUtils.roundValue;
import static com.carbo.job.utils.ControllerUtil.*;

@Service
public class WidgetService {
    private final MongoTemplate mongoTemplate;
    private final JobMongoDbRepository jobMongoDbRepository;
    private final ChemicalStageMongoDbRepository chemicalStageMongoDbRepository;
    private final ProppantStageMongoDbRepository proppantStageMongoDbRepository;
    private final ActivityLogMongoDbRepository activityLogMongoDbRepository;
    private final PumpScheduleMongoDbRepository pumpScheduleMongoDbRepository;
    private final EndStageEmailMongoDbRepository endStageEmailMongoDbRepository;
    private final ProppantDeliveryMongoDbRepository proppantDeliveryMongoDbRepository;
    private final MaterialNeededRepository materialNeededRepository;
    private final PriceBookDetailsMongoDbRepository priceBookDetailsMongoDbRepository;
    private final PriceBookMongoDbRepository priceBookMongoDbRepository;


    @Autowired
    public WidgetService(MongoTemplate mongoTemplate, JobMongoDbRepository jobMongoDbRepository, ChemicalStageMongoDbRepository chemicalStageMongoDbRepository, ProppantStageMongoDbRepository proppantStageMongoDbRepository,
                         ActivityLogMongoDbRepository activityLogMongoDbRepository, PumpScheduleMongoDbRepository pumpScheduleMongoDbRepository,
                         EndStageEmailMongoDbRepository endStageEmailMongoDbRepository, ProppantDeliveryMongoDbRepository proppantDeliveryMongoDbRepository,
                         MaterialNeededRepository materialNeededRepository, PriceBookDetailsMongoDbRepository priceBookDetailsMongoDbRepository, PriceBookMongoDbRepository priceBookMongoDbRepository) {
        this.mongoTemplate = mongoTemplate;
        this.jobMongoDbRepository = jobMongoDbRepository;
        this.chemicalStageMongoDbRepository = chemicalStageMongoDbRepository;
        this.proppantStageMongoDbRepository = proppantStageMongoDbRepository;
        this.activityLogMongoDbRepository = activityLogMongoDbRepository;
        this.pumpScheduleMongoDbRepository = pumpScheduleMongoDbRepository;
        this.endStageEmailMongoDbRepository = endStageEmailMongoDbRepository;
        this.proppantDeliveryMongoDbRepository = proppantDeliveryMongoDbRepository;
        this.materialNeededRepository = materialNeededRepository;
        this.priceBookDetailsMongoDbRepository = priceBookDetailsMongoDbRepository;
        this.priceBookMongoDbRepository = priceBookMongoDbRepository;
    }

    private static long calculateDuration(String startDate, String endDate) {

        if (startDate.trim().matches("^\\d{1,2}:\\d{2}$") && endDate.trim().matches("^\\d{1,2}:\\d{2}$")) {

            LocalDateTime startTime = LocalDateTime.parse(startDate.trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime endTime = LocalDateTime.parse(endDate.trim(), DateTimeFormatter.ofPattern("HH:mm"));

            Duration duration = Duration.between(startTime, endTime);
            return duration.toMinutes();
        } else {
            // Parse with date-time format (yyyyMMdd HH:mm)
            DateTimeFormatter formatterWithDate = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

            try {
                LocalDateTime startDateTime = LocalDateTime.parse(startDate.trim(), formatterWithDate);
                LocalDateTime endDateTime = LocalDateTime.parse(endDate.trim(), formatterWithDate);

                // Check if the end date-time is before the start date-time
                if (endDateTime.isBefore(startDateTime)) {
                    // If end date-time is before start date-time, consider it for the next day
                    endDateTime = endDateTime.plusDays(1); // Add a day to the end date-time
                }

                Duration duration = Duration.between(startDateTime, endDateTime);
                return duration.toMinutes();
            } catch (Exception e) {
                System.out.println("Error parsing date-time strings: " + e.getMessage());
                return -1; // Return a value to indicate an error
            }
        }
    }

    public static long convertHoursToMinutes(double hours) {
        // Convert hours to minutes
        return (long) (hours * 60);
    }

    public static double convertMinutesToHours(double minutes) {
        // Convert minutes to hours
        return minutes / 60;
    }

    private static Map<String, Double> createTimeConsume(double goalValue, double actualValue) {
        Map<String, Double> timeConsume = new HashMap<>();
        double percentageDiff = 0;
        if (goalValue != 0) {
            percentageDiff = calculatePercentage(goalValue, actualValue);
        }
        double minuteDiff = Math.abs(goalValue - actualValue);
        double roundOfMinuteDiff = Math.round(minuteDiff * 100.0) / 100.0;

        timeConsume.put("goal", goalValue);
        timeConsume.put("actual", actualValue);
        timeConsume.put("percentage", percentageDiff);
        timeConsume.put("minutes", roundOfMinuteDiff);
        return timeConsume;
    }

    private static double calculatePercentageDifference(double design, double actual) {
        double percentDifference = 0;
        if (design != 0) {
            double diff = design - actual;
            percentDifference = diff / design;
        } else {
            percentDifference = -1;
        }
        return percentDifference * 100;
    }

    private static double calculatePercentage(double design, double actual) {
        double percentage = 0;
        if (design != 0) {

            percentage = (actual/ design)*100;
        }
        return percentage;
    }

    public ResponseEntity getChemicalPlotData(HttpServletRequest request, String jobId, String wellId, Float stageNumber, ChemicalEnum chemicalEnum) {
        try {
            Map<String, Map<String, ChemicalResponse>> chemicalResponse = new HashMap<>();
            Optional<Job> jobList = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(jobList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = jobList.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            List<ChemicalStage> chemicalStageList = chemicalStageMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            if (ObjectUtils.isEmpty(chemicalStageList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.CHEMICAL_STAGE_DATA_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.CHEMICAL_STAGE_DATA_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Set<String> acids = new HashSet<>();

                for (Well well : job.getWells()) {
                    for (Chemical chemical : well.getAcidAdditives()) {
                        if (chemical.getSubtype().equals("Acid")) {
                            acids.add(chemical.getName());
                        }

                    }
                    for (Chemical chemical : well.getDiverters()) {
                        if (chemical.getSubtype().equals("Acid")) {
                            acids.add(chemical.getName());
                        }

                    }
                    for (Chemical chemical : well.getSlickwaters()) {
                        if (chemical.getSubtype().equals("Acid")) {
                            acids.add(chemical.getName());
                        }

                    }
                    for (Chemical chemical : well.getLinearGelCrosslinks()) {
                        if (chemical.getSubtype().equals("Acid")) {
                            acids.add(chemical.getName());
                        }
                    }
                    for (Map.Entry<String, List<Chemical>> entry : well.getAdditionalChemicalTypes().entrySet()) {
                        for (Chemical chemical : entry.getValue()) {
                            if (chemical.getSubtype().equals("Acid")) {
                                acids.add(chemical.getName());
                            }
                        }
                    }
                }
            Map<String, ChemicalResponse> chemicalResponseMap = new HashMap<>();
            chemicalResponseMap = getChemicalData(job, wellId, chemicalStageList, stageNumber, chemicalResponseMap, chemicalEnum, job.isNewWorkflow());
            for (Map.Entry<String, ChemicalResponse> entry : chemicalResponseMap.entrySet()) {
                PercentDifference percentDifference = (PercentDifference) entry.getValue();
                int count = percentDifference.getCount() != 0 ? percentDifference.getCount() : 1;
                percentDifference.setActual(percentDifference.getActual() / count);
                percentDifference.setDesign(percentDifference.getDesign() / count);
                percentDifference.setPercentDiff((float) roundValue((float) calculatePercentageDifference(percentDifference.getDesign(), percentDifference.getActual()), 0));
            }
            Map<String, ChemicalResponse> acidResponseMap = new HashMap<>();
            Map<String, ChemicalResponse> otherResponseMap = new HashMap<>();
            for (Map.Entry<String, ChemicalResponse> entry : chemicalResponseMap.entrySet()) {
                String chemicalName = entry.getKey();
                ChemicalResponse chemicalResponse1 = entry.getValue();

                if (acids.contains(chemicalName)) {
                    acidResponseMap.put(chemicalName, chemicalResponse1);
                } else {
                    otherResponseMap.put(chemicalName, chemicalResponse1);
                }
            }

            chemicalResponse.put(String.valueOf(ChemicalTypeEnum.ACID), acidResponseMap);
            chemicalResponse.put(String.valueOf(ChemicalTypeEnum.OTHER), otherResponseMap);
            return ResponseEntity.ok(chemicalResponse);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_PLOT_DATA_CODE)
                    .errorMessage(ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_PLOT_DATA_MESSAGE).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, ChemicalResponse> getChemicalData(Job job, String wellId, List<ChemicalStage> chemicalStageList, Float stageNumber, Map<String, ChemicalResponse> chemicalResponseMap, ChemicalEnum chemicalEnum, boolean isNewWorkFlow) {
        {
            if (!ObjectUtils.isEmpty(job.getWells())) {
                for (Well well : job.getWells()) {
                    if (((chemicalEnum == WELL || chemicalEnum == STAGE) && !ObjectUtils.isEmpty(wellId) && well.getId().equals(wellId)) || chemicalEnum == JOB) {
                        for (ChemicalStage chemicalStage : chemicalStageList) {
                            if (chemicalStage.getWellId().equals(well.getId())) {
                                if (chemicalEnum != STAGE || (!ObjectUtils.isEmpty(stageNumber) && chemicalStage.getStage().equals(stageNumber))) {
                                    if (!ObjectUtils.isEmpty(well.getAcidAdditives())) {
                                        for (Chemical chemical : well.getAcidAdditives()) {
                                            getMapChemicalResponse(chemical, chemicalStage, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                        }
                                    }
                                    if (!ObjectUtils.isEmpty(well.getDiverters())) {
                                        for (Chemical chemical : well.getDiverters()) {
                                            getMapChemicalResponse(chemical, chemicalStage, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                        }
                                    }
                                    if (!ObjectUtils.isEmpty(well.getSlickwaters())) {
                                        for (Chemical chemical : well.getSlickwaters()) {
                                            getMapChemicalResponse(chemical, chemicalStage, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                        }
                                    }
                                    if (!ObjectUtils.isEmpty(well.getLinearGelCrosslinks())) {
                                        for (Chemical chemical : well.getLinearGelCrosslinks()) {
                                            getMapChemicalResponse(chemical, chemicalStage, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                        }
                                    }
                                    if (!ObjectUtils.isEmpty(well.getAdditionalChemicalTypes())) {
                                        for (Map.Entry<String, List<Chemical>> entry : well.getAdditionalChemicalTypes().entrySet()) {
                                            for (Chemical chemical : entry.getValue()) {
                                                getMapChemicalResponse(chemical, chemicalStage, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return chemicalResponseMap;
    }

    public ResponseEntity getCostsTrackingData(HttpServletRequest request, String jobId, String wellId, Float stageNumber, ChemicalEnum chemicalEnum) {
        try {
            Map<String, Map<String, ChemicalResponse>> costTrackingMapResponse = new HashMap<>();
            Optional<Job> optionalJob = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(optionalJob)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = optionalJob.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            List<ChemicalStage> chemicalStageList = chemicalStageMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            if (ObjectUtils.isEmpty(chemicalStageList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.CHEMICAL_STAGE_DATA_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.CHEMICAL_STAGE_DATA_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            List<ProppantStage> proppantStageList = proppantStageMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
//            if (ObjectUtils.isEmpty(proppantStageList)) {
//                Error error = Error.builder()
//                        .errorCode(ErrorConstants.PROPPANT_STAGE_DATA_NOT_FOUND_CODE)
//                        .errorMessage(ErrorConstants.PROPPANT_STAGE_DATA_NOT_FOUND_MESSAGE)
//                        .build();
//                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
//            }
            Optional<PriceBook> optionalPriceBook = priceBookMongoDbRepository.findByPriceBookNameAndOrganizationId(Constants.PRICEBOOK_NAME, organizationId);
            if (ObjectUtils.isEmpty(optionalPriceBook)) {
                Error error = Error.builder()
                        .errorCode(ERROR_WHILE_FETCHING_DEFAULT_PRICEBOOK_CODE)
                        .errorMessage(ERROR_WHILE_FETCHING_DEFAULT_PRICEBOOK_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            List<com.carbo.job.model.widget.misc.Chemical> chemicalList = new ArrayList<>();
            List<Proppant> proppantList = new ArrayList<>();
            List<PriceBookComponents> priceBookComponents = priceBookDetailsMongoDbRepository.findByOrganizationIdAndPriceBookId(organizationId, optionalPriceBook.get().getId());
            for(PriceBookComponents components : priceBookComponents){
                com.carbo.job.model.widget.misc.Chemical chemical = new com.carbo.job.model.widget.misc.Chemical();
                Proppant proppant = new Proppant();
                if(components.getPriceBookTypeEnum().equals(PriceBookTypeEnum.CHEMICAL)){
                    chemical.setId(components.getId());
                    chemical.setName(components.getName());
                    chemical.setCode(components.getItemCode());
                    if(!ObjectUtils.isEmpty(components.getPrice())) {
                        chemical.setPrice(Float.valueOf(components.getPrice()));
                    } else{
                        chemical.setPrice(0.0f);
                    }
                    chemicalList.add(chemical);
                }else if(components.getPriceBookTypeEnum().equals(PriceBookTypeEnum.PROPPANT)){
                    proppant.setId(components.getId());
                    proppant.setName(components.getName());
                    proppant.setCode(components.getItemCode());
                    if(!ObjectUtils.isEmpty(components.getPrice())) {
                        proppant.setPrice(Float.valueOf(components.getPrice()));
                    } else{
                        proppant.setPrice(0.0f);
                    }
                    proppantList.add(proppant);
                }
            }

//            if (ObjectUtils.isEmpty(chemicalList) && ObjectUtils.isEmpty(proppantList)) {
//                Error error = Error.builder()
//                        .errorCode(ErrorConstants.CHEMICAL_LIST_DATA_NOT_FOUND_CODE)
//                        .errorMessage(ErrorConstants.CHEMICAL_LIST_DATA_NOT_FOUND_MESSAGE)
//                        .build();
//                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
//            }
            getChemicalCostData(job, wellId, chemicalStageList, stageNumber, chemicalList, costTrackingMapResponse, chemicalEnum, job.isNewWorkflow());
            getProppantCostsData(job, wellId, proppantStageList, stageNumber, costTrackingMapResponse, proppantList, chemicalEnum, job.isNewWorkflow());
            for (Map.Entry<String, Map<String, ChemicalResponse>> entry : costTrackingMapResponse.entrySet()) {
                for (Map.Entry<String, ChemicalResponse> innerEntry : entry.getValue().entrySet()) {
                    PercentDifference percentDifference = (PercentDifference) innerEntry.getValue();
                    percentDifference.setActual(percentDifference.getActual());
                    percentDifference.setDesign(percentDifference.getDesign());
                    percentDifference.setPercentDiff((float) roundValue((float) calculatePercentageDifference(percentDifference.getDesign(), percentDifference.getActual()), 0));
                }
            }
            return ResponseEntity.ok(costTrackingMapResponse);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_COST_TRACKING_DATA_CODE)
                    .errorMessage(ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_COST_TRACKING_DATA_MESSAGE)
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Map<String, ChemicalResponse>> getProppantCostsData(Job job,
                                                                            String wellId,
                                                                            List<ProppantStage> proppantStageList,
                                                                            Float stageNumber,
                                                                            Map<String, Map<String, ChemicalResponse>> costTrackingMapResponse,
                                                                            List<Proppant> proppantList,
                                                                            ChemicalEnum chemicalEnum, boolean isNewWorkFlow) {
        if (!ObjectUtils.isEmpty(job.getWells())) {
            for (Well well : job.getWells()) {
                if (((chemicalEnum == WELL || chemicalEnum == STAGE) && !ObjectUtils.isEmpty(wellId) && well.getId().equals(wellId)) || chemicalEnum == JOB) {
                    if (!ObjectUtils.isEmpty(proppantStageList)) {
                        for (ProppantStage proppantStage : proppantStageList) {
                            if (proppantStage.getWellId().equals(well.getId())) {
                                if (chemicalEnum != STAGE || (!ObjectUtils.isEmpty(stageNumber) && proppantStage.getStage().equals(stageNumber))) {
                                    Map<String, ChemicalResponse> chemicalResponseMap;
                                    if (!costTrackingMapResponse.containsKey(proppantStage.getStage() + "")) {
                                        costTrackingMapResponse.put(proppantStage.getStage() + "", new LinkedHashMap<>());
                                    }
                                    chemicalResponseMap = costTrackingMapResponse.get(proppantStage.getStage() + "");
                                    // Code for Proppant details
                                    if (!ObjectUtils.isEmpty(well.getProppants())) {
                                        for (Proppant proppant : well.getProppants()) {
                                            getMapProppantCostTracking(proppant, proppantStage, proppantList, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return costTrackingMapResponse;
    }

    private Map<String, Map<String, ChemicalResponse>> getChemicalCostData(Job job,
                                                                           String wellId,
                                                                           List<ChemicalStage> chemicalStageList,
                                                                           Float stageNumber,
                                                                           List<com.carbo.job.model.widget.misc.Chemical> chemicalList,
                                                                           Map<String, Map<String, ChemicalResponse>> costTrackingMapResponse,
                                                                           ChemicalEnum chemicalEnum, boolean isNewWorkFlow) {
        if (!ObjectUtils.isEmpty(job.getWells())) {
            for (Well well : job.getWells()) {
                if (((chemicalEnum == WELL || chemicalEnum == STAGE) && !ObjectUtils.isEmpty(wellId) && well.getId().equals(wellId)) || chemicalEnum == JOB) {
                    for (ChemicalStage chemicalStage : chemicalStageList) {
                        if (chemicalStage.getWellId().equals(well.getId())) {
                            if (chemicalEnum != STAGE || (!ObjectUtils.isEmpty(stageNumber) && chemicalStage.getStage().equals(stageNumber))) {
                                Map<String, ChemicalResponse> chemicalResponseMap;
                                if (!costTrackingMapResponse.containsKey(chemicalStage.getStage() + "")) {
                                    costTrackingMapResponse.put(chemicalStage.getStage() + "", new LinkedHashMap<>());
                                }
                                chemicalResponseMap = costTrackingMapResponse.get(chemicalStage.getStage() + "");
                                if (!ObjectUtils.isEmpty(well.getAcidAdditives())) {
                                    for (Chemical chemical : well.getAcidAdditives()) {
                                        getMapChemicalCostTrackingResponse(chemical, chemicalStage, chemicalList, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                    }
                                }
                                if (!ObjectUtils.isEmpty(well.getDiverters())) {
                                    for (Chemical chemical : well.getDiverters()) {
                                        getMapChemicalCostTrackingResponse(chemical, chemicalStage, chemicalList, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                    }
                                }
                                if (!ObjectUtils.isEmpty(well.getSlickwaters())) {
                                    for (Chemical chemical : well.getSlickwaters()) {
                                        getMapChemicalCostTrackingResponse(chemical, chemicalStage, chemicalList, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                    }
                                }
                                if (!ObjectUtils.isEmpty(well.getLinearGelCrosslinks())) {
                                    for (Chemical chemical : well.getLinearGelCrosslinks()) {
                                        getMapChemicalCostTrackingResponse(chemical, chemicalStage, chemicalList, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                    }
                                }
                                if (!ObjectUtils.isEmpty(well.getAdditionalChemicalTypes())) {
                                    for (Map.Entry<String, List<Chemical>> entry : well.getAdditionalChemicalTypes().entrySet()) {
                                        for (Chemical chemical : entry.getValue()) {
                                            getMapChemicalCostTrackingResponse(chemical, chemicalStage, chemicalList, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return costTrackingMapResponse;
    }

    private void getMapProppantCostTracking(Proppant proppant, ProppantStage proppantStage, List<Proppant> priceBookComponentsList, Map<String, ChemicalResponse> chemicalResponseMap, boolean isNewWorkFlow, ChemicalEnum chemicalEnum) {
        float design = 0.0f;
        float actual = 0.0f;
        int count = 0;
        if (!ObjectUtils.isEmpty(chemicalResponseMap) && chemicalResponseMap.containsKey(proppant.getName())) {
            ChemicalResponse singleChemicalData = chemicalResponseMap.get(proppant.getName());
            design = singleChemicalData.getDesign();
            actual = singleChemicalData.getActual();
            count = singleChemicalData.getCount();
        }
        if (!ObjectUtils.isEmpty(proppantStage.getRunOrders())) {
            for (ProppantContainer proppantContainer : proppantStage.getRunOrders()) {
                if (proppant.getCode().equals(proppantContainer.getProppant().getCode())) {
                    for (Proppant priceBookComponents : priceBookComponentsList) {
                        if (priceBookComponents.getCode().equals(proppant.getCode())) {
                            float rate = priceBookComponents.getPrice();
                            if(isNewWorkFlow){
                                double designPerWell = proppant.getCalculatedVolume().getPerWell().get(proppantStage.getWell());
                                int numberOfStages = proppant.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    int stage = Math.round(proppantStage.getStage());
                                    Double value = proppant.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    design = design + value.floatValue() * rate;
                                }else {
                                    design = (float) (design + (designPerWell/numberOfStages) * rate);
                                }
                            } else {
                                design = (design + (proppant.getVolumePerStage() == null ? 0.0f : proppant.getVolumePerStage()) * rate);
                            }
                            actual = (actual + proppantContainer.getActualRun() * rate);
                            count++;
                        }
                    }
                }
            }
        }
        chemicalResponseMap.put(proppant.getName(), aggregateChemical(design, actual, count));
    }

    private void getMapChemicalCostTrackingResponse(Chemical chemical,
                                                    ChemicalStage chemicalStage,
                                                    List<com.carbo.job.model.widget.misc.Chemical> priceBookComponentsList,
                                                    Map<String, ChemicalResponse> chemicalResponseMap,boolean isNewWorkFlow, ChemicalEnum chemicalEnum) {
        float design = 0.0f;
        float actual = 0.0f;
        int count = 0;
        if (!ObjectUtils.isEmpty(chemicalResponseMap) && chemicalResponseMap.containsKey(chemical.getName())) {
            ChemicalResponse singleChemicalData = chemicalResponseMap.get(chemical.getName());
            design = singleChemicalData.getDesign();
            actual = singleChemicalData.getActual();
            count = singleChemicalData.getCount();
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getChemicalAdditionUnit1())) {
            for (Strap strap : chemicalStage.getChemicalAdditionUnit1()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    for (com.carbo.job.model.widget.misc.Chemical priceBookComponents : priceBookComponentsList) {
                        if (priceBookComponents.getCode().equals(chemical.getCode())) {
                            float rate = priceBookComponents.getPrice();
                            if(isNewWorkFlow){
                                double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                                int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    int stage = Math.round(chemicalStage.getStage());
                                    Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    design = design + value.floatValue() * rate;
                                }else {
                                    design = (float) (design + (designPerWell/numberOfStages) * rate);
                                }
                            } else {
                                design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage) * rate;
                            }
                            actual = actual + strap.getRawUsed() * rate;
                            count++;
                        }
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getChemicalAdditionUnit2())) {
            for (Strap strap : chemicalStage.getChemicalAdditionUnit2()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    for (com.carbo.job.model.widget.misc.Chemical priceBookComponents : priceBookComponentsList) {
                        if (priceBookComponents.getCode().equals(chemical.getCode())) {
                            float rate = priceBookComponents.getPrice();
                            if(isNewWorkFlow){
                                double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                                int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    int stage = Math.round(chemicalStage.getStage());
                                    Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    design = design + value.floatValue() * rate;
                                }else {
                                    design = (float) (design + (designPerWell/numberOfStages) * rate);
                                }
                            } else {
                                design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage) * rate;
                            }
                            actual = actual + strap.getRawUsed() * rate;
                            count++;
                        }
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getDryAdd())) {
            for (Strap strap : chemicalStage.getDryAdd()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    for (com.carbo.job.model.widget.misc.Chemical priceBookComponents : priceBookComponentsList) {
                        if (priceBookComponents.getCode().equals(chemical.getCode())) {
                            float rate = priceBookComponents.getPrice();
                            if(isNewWorkFlow){
                                double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                                int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    int stage = Math.round(chemicalStage.getStage());
                                    Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    design = design + value.floatValue() * rate;
                                }else {
                                    design = (float) (design + (designPerWell/numberOfStages) * rate);
                                }
                            } else {
                                design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage) * rate;
                            }
                            actual = actual + strap.getRawUsed() * rate;
                            count++;
                        }
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getIsosTransport())) {
            for (Strap strap : chemicalStage.getIsosTransport()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    for (com.carbo.job.model.widget.misc.Chemical priceBookComponents : priceBookComponentsList) {
                        if (priceBookComponents.getCode().equals(chemical.getCode())) {
                            float rate = priceBookComponents.getPrice();
                            if(isNewWorkFlow){
                                double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                                int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    int stage = Math.round(chemicalStage.getStage());
                                    Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    design = design + value.floatValue() * rate;
                                }else {
                                    design = (float) (design + (designPerWell/numberOfStages) * rate);
                                }
                            } else {
                                design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage) * rate;
                            }
                            actual = actual + strap.getRawUsed() * rate;
                            count++;
                        }
                    }
                }
            }
        }
        chemicalResponseMap.put(chemical.getName(), aggregateChemical(design, actual, count));
    }

    private void getMapProppantMaterialUsagesResponse(Proppant proppant, ProppantStage proppantStage, Map<String, ChemicalResponse> chemicalResponseMap, boolean isNweWorkFlow, ChemicalEnum chemicalEnum) {
        float design = 0.0f;
        float actual = 0.0f;
        int count = 0;
        if (!ObjectUtils.isEmpty(proppantStage.getRunOrders())) {
            for (ProppantContainer proppantContainer : proppantStage.getRunOrders()) {
                if (!ObjectUtils.isEmpty(proppantContainer.getProppant())) {
                    if (!ObjectUtils.isEmpty(proppantContainer.getProppant().getCode())) {
                        if (proppant.getCode().equals(proppantContainer.getProppant().getCode())) {
                            if(isNweWorkFlow){
                                double designPerWell = proppant.getCalculatedVolume().getPerWell().get(proppantStage.getWell());
                                int numberOfStages = proppant.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    int stage = Math.round(proppantStage.getStage());
                                    Double value = proppant.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    design = value.floatValue();
                                }else {
                                    design = (float) (design + (designPerWell / numberOfStages));
                                }
                            } else {
                                design = (design + (proppant.getVolumePerStage() == null ? 0.0f : proppant.getVolumePerStage()));
                            }
                            actual = (actual + proppantContainer.getActualRun());
                            count++;
                        }
                    }
                }
            }
            chemicalResponseMap.put(proppant.getName(), aggregateChemical(design, actual, count));
        }
//        String proppantName = proppant.getName();
//        PercentDifference existingEntry = chemicalResponseMap.get(proppantName);
//
//        if (existingEntry != null) {
//            // Key already exists, update actual and design values
//            float newActual = existingEntry.getActual() + actual;
//            float newDesign = existingEntry.getDesign() + design;
//
//            // Calculate PercentDifference with updated values
//            double newPercentDifference = calculatePercentageDifference(newActual, newDesign);
//
//            // Update existing entry in the map
//            chemicalResponseMap.put(proppantName, new PercentDifference(newDesign, newActual, (float) newPercentDifference));
//        } else {
//            // Key doesn't exist, create a new entry
//            double percentDifference = calculatePercentageDifference(actual, design);
//            chemicalResponseMap.put(proppantName, new PercentDifference(design, actual, (float) percentDifference));
//        }
    }

    private void getMapChemicalMaterialUsagesResponse(Chemical chemical, ChemicalStage chemicalStage, Map<String, PercentDifference> chemicalResponseMap, int count, ChemicalEnum chemicalEnum) {
        float design = 0.0f;
        float actual = 0.0f;


        if (!ObjectUtils.isEmpty(chemicalStage.getChemicalAdditionUnit1())) {
            for (Strap strap : chemicalStage.getChemicalAdditionUnit1()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                    actual = actual + strap.getRawUsed();
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getChemicalAdditionUnit2())) {
            for (Strap strap : chemicalStage.getChemicalAdditionUnit2()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if (chemical.getCode().equals(strap.getChemical().getCode())) {
                        design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                        actual = actual + strap.getRawUsed();
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getDryAdd())) {
            for (Strap strap : chemicalStage.getDryAdd()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if (chemical.getCode().equals(strap.getChemical().getCode())) {
                        design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                        actual = actual + strap.getRawUsed();
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getIsosTransport())) {
            for (Strap strap : chemicalStage.getIsosTransport()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if (chemical.getCode().equals(strap.getChemical().getCode())) {
                        design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                        actual = actual + strap.getRawUsed();
                    }
                }
            }
        }
        if (chemicalEnum.equals(WELL)) {
            actual = actual / count;

        }
        String chemicalName = chemical.getName();
        PercentDifference existingEntry = chemicalResponseMap.get(chemicalName);

        if (existingEntry != null) {
            // Key already exists, update actual and design values
            float newActual = existingEntry.getActual() + actual;
            float newDesign = existingEntry.getDesign() + design;

            // Calculate PercentDifference with updated values
            double newPercentDifference = calculatePercentageDifference(newActual, newDesign);

            // Update existing entry in the map
            chemicalResponseMap.put(chemicalName, new PercentDifference(newDesign, newActual, (float) newPercentDifference));
        } else {
            // Key doesn't exist, create a new entry
            double percentDifference = calculatePercentageDifference(actual, design);
            chemicalResponseMap.put(chemicalName, new PercentDifference(design, actual, (float) percentDifference));
        }
    }

//    public ResponseEntity getMaterialUsagesData(HttpServletRequest request,
//                                                String jobId,
//                                                String wellId,
//                                                Float stageNumber,
//                                                ChemicalEnum chemicalEnum) {
//        Map<String, PercentDifference> costTrackingMapResponse = new HashMap<>();
//        Random random = new Random();
//
//        // Ensure at least 5 chemicals and 5 proppants
//        int numChemicals = Math.max(5, random.nextInt(10));
//        int numProppants = Math.max(5, random.nextInt(10));
//
//        for (int i = 0; i < numChemicals; i++) {
//            String chemicalName = "Chemical " + i;
//            float design = random.nextFloat() * 100;
//            float actual = random.nextFloat() * 100;
//            float percentageDifference = (actual - design) / design * 100;
//
//            // Ensure the percentage difference is between -100 and 100
//            percentageDifference = Math.max(-100, Math.min(100, percentageDifference));
//
//            PercentDifference chemicalResponse = new PercentDifference(design, actual, percentageDifference);
//            costTrackingMapResponse.put(chemicalName, chemicalResponse);
//        }
//
//        for (int i = 0; i < numProppants; i++) {
//            String proppantName = "Proppant " + i;
//            float design = random.nextFloat() * 100;
//            float actual = random.nextFloat() * 100;
//            float percentageDifference = (actual - design) / design * 100;
//
//            // Ensure the percentage difference is between -100 and 100
//            percentageDifference = Math.max(-100, Math.min(100, percentageDifference));
//
//            PercentDifference proppantResponse = new PercentDifference(design, actual, percentageDifference);
//            costTrackingMapResponse.put(proppantName, proppantResponse);
//        }
//        return new ResponseEntity<>(costTrackingMapResponse, HttpStatus.OK);
//    }


    public ResponseEntity getMaterialUsagesData(HttpServletRequest request, String jobId, String wellId, Float stageNumber, ChemicalEnum chemicalEnum) {
        try {
//            Map<String, PercentDifference> chemicalResponseMap = new HashMap<>();
            Optional<Job> jobList = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(jobList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = jobList.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            int wellCount = 0;
            for (Well well : job.getWells()) {
                wellCount++;
            }
            List<ChemicalStage> chemicalStageList = chemicalStageMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            if (ObjectUtils.isEmpty(chemicalStageList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.CHEMICAL_STAGE_DATA_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.CHEMICAL_STAGE_DATA_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            List<ProppantStage> proppantStageList = proppantStageMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
//            if (ObjectUtils.isEmpty(proppantStageList)) {
//                Error error = Error.builder()
//                        .errorCode(ErrorConstants.PROPPANT_STAGE_DATA_NOT_FOUND_CODE)
//                        .errorMessage(ErrorConstants.PROPPANT_STAGE_DATA_NOT_FOUND_MESSAGE)
//                        .build();
//                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
//            }
            Map<String, ChemicalResponse> chemicalResponseMap = new HashMap<>();
            chemicalResponseMap = getChemicalData(job, wellId, chemicalStageList, stageNumber, chemicalResponseMap, chemicalEnum, job.isNewWorkflow());
            chemicalResponseMap = getProppantData(job, wellId, proppantStageList, stageNumber, chemicalResponseMap, chemicalEnum, job.isNewWorkflow());
            for (Map.Entry<String, ChemicalResponse> entry : chemicalResponseMap.entrySet()) {
                PercentDifference percentDifference = (PercentDifference) entry.getValue();
                int count = percentDifference.getCount() != 0 ? percentDifference.getCount() : 1;
                percentDifference.setActual(percentDifference.getActual() / count);
                percentDifference.setDesign(percentDifference.getDesign() / count);
                percentDifference.setPercentDiff((float) roundValue((float) calculatePercentageDifference(percentDifference.getDesign(), percentDifference.getActual()), 0));
            }
            return ResponseEntity.ok(chemicalResponseMap);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_COST_TRACKING_DATA_CODE)
                    .errorMessage(ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_COST_TRACKING_DATA_MESSAGE)
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, ChemicalResponse> getProppantData(Job job, String wellId, List<ProppantStage> proppantStageList, Float stageNumber, Map<String, ChemicalResponse> chemicalResponseMap, ChemicalEnum chemicalEnum, boolean isNewWorkFlow) {
        if (!ObjectUtils.isEmpty(job.getWells())) {
            for (Well well : job.getWells()) {
                if (((chemicalEnum == WELL || chemicalEnum == STAGE) && !ObjectUtils.isEmpty(wellId) && well.getId().equals(wellId)) || chemicalEnum == JOB) {
                    if (!ObjectUtils.isEmpty(proppantStageList)) {
                        for (ProppantStage proppantStage : proppantStageList) {
                            if (proppantStage.getWellId().equals(well.getId())) {
                                if (chemicalEnum != STAGE || (!ObjectUtils.isEmpty(stageNumber) && proppantStage.getStage().equals(stageNumber))) {
                                    // Code for Proppant details
                                    if (well.getId().equals(wellId)) {
                                        if (!ObjectUtils.isEmpty(well.getProppants())) {
                                            for (Proppant proppant : well.getProppants()) {
                                                getMapProppantMaterialUsagesResponse(proppant, proppantStage, chemicalResponseMap, isNewWorkFlow, chemicalEnum);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return chemicalResponseMap;
    }

    public ResponseEntity getPerformanceParameter(HttpServletRequest request, String jobId, int day, boolean forDay) {

        try {
            Optional<Job> job1 = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(job1)) {
                Error error = Error.builder().errorCode(ErrorConstants.JOB_NOT_FOUND_CODE).errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE).build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = job1.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            float countOfStage = 0;
            long sumOfPumpTime = 0;
            long sumOfSwapOverTime = 0;
            long averageTime = 0;
            double totalSumPumpTime = 0;
            double totalSwapOverTime = 0;
            int count = 0;
            if (forDay) {
                List<ActivityLogEntry> activityLogEntryList = activityLogMongoDbRepository.findByOrganizationIdAndJobIdAndDay(organizationId, jobId,
                        day);
                if (activityLogEntryList.isEmpty()) {
                    Error error = Error.builder().errorCode(ErrorConstants.ERROR_WHILE_CALCULATING_DATA_CODE).errorMessage(ErrorConstants.ERROR_WHILE_CALCULATING_DATA_MESSAGE).build();
                    return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
                }

                for (ActivityLogEntry activityLogEntry : activityLogEntryList) {
                    if (activityLogEntry.getComplete()) {
                        countOfStage++;

                    }
                    if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(PUMP_TIME)) {
                        long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                        sumOfPumpTime += duration;

                    }

                    if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WELL_SWAP)) {
                        long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                        sumOfSwapOverTime += duration;

                    }
                }
                averageTime = (long) countOfStage;
                totalSumPumpTime = sumOfPumpTime;
                totalSwapOverTime = sumOfSwapOverTime;
            } else {
                List<ActivityLogEntry> activityLogEntryList = activityLogMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
                Set<Integer> daysList = activityLogEntryList.stream().map(ActivityLogEntry::getDay).sorted(Comparator.reverseOrder()).collect(
                        Collectors.toCollection(LinkedHashSet::new));
                int totalDays = 0;
                for (int days : daysList) {
                    totalDays += 1;
                    for (ActivityLogEntry activityLogEntry : activityLogEntryList) {
                        if (activityLogEntry.getDay().equals(days)) {
                            if (activityLogEntry.getComplete()) {
                                countOfStage++;
                            }
                            if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(PUMP_TIME)) {
                                long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                                sumOfPumpTime += duration;
                            }
                            if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WELL_SWAP)) {
                                long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                                sumOfSwapOverTime += duration;
                                count++;
                            }

                        }
                    }
                }
                count = count == 0 ? 1 : count;
                averageTime = (long) (countOfStage / totalDays);
                totalSumPumpTime = sumOfPumpTime / totalDays;
                totalSwapOverTime = (sumOfSwapOverTime) / count;
            }

            Map<String, ChemicalResponse> costTrackingResponseMap = new LinkedHashMap<>();
            // Generate dummy ChemicalResponse objects
            PercentDifference stagePerDay = new PercentDifference(job.getTargetStagesPerDay(), averageTime, 0.0f);
            //            double targetPumpTime=convertHoursToMinutes(job.getTargetDailyPumpTime());
            double totalPumpInHour = convertMinutesToHours(totalSumPumpTime);
            PercentDifference targetPumpHours = new PercentDifference(job.getTargetDailyPumpTime(), (float) totalPumpInHour, 0.0f);
            PercentDifference swapOverTime = new PercentDifference(job.getSwapOverTime(), (float) totalSwapOverTime, 0.0f);

            // Calculate percentage difference
            calculatePercentageDifference(stagePerDay);
            calculatePercentageDifference(targetPumpHours);
            calculatePercentageDifference(swapOverTime);

            // Populate the response map
            costTrackingResponseMap.put(ErrorConstants.STAGE_PER_DAY, stagePerDay);
            costTrackingResponseMap.put(ErrorConstants.TAREGT_PUMP_HOURS, targetPumpHours);
            costTrackingResponseMap.put(ErrorConstants.SWAP_OVER_TIME, swapOverTime);

            return ResponseEntity.ok(costTrackingResponseMap);
        } catch (Exception e) {
            Error error = Error.builder().errorCode(ErrorConstants.ERROR_WHILE_FETCHING_GRAPH_DATA_CODE)
                    .errorMessage(ErrorConstants.ERROR_WHILE_FETCHING_GRAPH_DATA_MESSAGE).build();
            return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // Method to calculate percentage difference for ChemicalResponse
    private void calculatePercentageDifference(PercentDifference response) {
        float design = response.getDesign();
        float actual = response.getActual();
        double percentDifference = 0;
        if (design != 0) {
            double diff = design - actual;
            percentDifference = diff / design;
        } else {
            percentDifference = -1;
        }
        response.setPercentDiff((float) (percentDifference * 100));
    }

    public ResponseEntity getTimeTracking(HttpServletRequest request, String jobId, Integer day) {
        try {
            Optional<Job> job1 = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(job1)) {
                Error error = Error.builder().errorCode(ErrorConstants.JOB_NOT_FOUND_CODE).errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE).build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = job1.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            List<ActivityLogEntry> activityLogEntryList = new ArrayList<>();
            if (!ObjectUtils.isEmpty(day)) {
                activityLogEntryList = activityLogMongoDbRepository.findByOrganizationIdAndJobIdAndDay(organizationId, jobId, day);
            } else {
                activityLogEntryList = activityLogMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            }
            Set<Integer> daysList = activityLogEntryList.stream().map(ActivityLogEntry::getDay).sorted(Comparator.reverseOrder()).collect(
                    Collectors.toCollection(LinkedHashSet::new));
            int totalDays = 0;
            double sumOfPumpTime = 0;
            double sumOfSwapOverTime = 0;
            double sumOfPlanedMaintenance = 0;
            double sumOfWireLineTime = 0;
            double totalCompleteStage = 0;
            int count = 0;
            int wireLineCount=0;
            for (int days : daysList) {
                totalDays += 1;
                for (ActivityLogEntry activityLogEntry : activityLogEntryList) {
                    if (activityLogEntry.getDay().equals(days)) {
                        if (activityLogEntry.getComplete()) {
                            totalCompleteStage += 1;
                        }
                        if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(PUMP_TIME)) {
                            long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                            sumOfPumpTime += duration;
                        }
                        if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WELL_SWAP)||activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WELLSWAP)||activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WELL_TRANSITION)||activityLogEntry.getEventOrNptCode().equalsIgnoreCase(FRAC_TRANSITION_TO_WL)||activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WL_TRANSITION_TO_FRAC)) {
                            long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                            sumOfSwapOverTime += duration;
                            count ++;
                        }
                        if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(PLANED_MAINTENANCE)|| activityLogEntry.getEventOrNptCode().equalsIgnoreCase(PUMP_MAINTENANCE)) {
                            long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                            sumOfPlanedMaintenance += duration;
                        }
                        if (activityLogEntry.getEventOrNptCode().equalsIgnoreCase(PLANED_WIRELINE)||activityLogEntry.getEventOrNptCode().equalsIgnoreCase(WIRELINE_RUN)) {
                            long duration = calculateDuration(activityLogEntry.getStart(), activityLogEntry.getEnd());
                            sumOfWireLineTime += duration;
                            wireLineCount++;
                        }

                    }
                }
            }
            count = count == 0 ? 1 : count;
            double totalTargetDailyPumpTime = convertHoursToMinutes(job.getTargetDailyPumpTime()) * totalDays;
            double totalSwapOverTime = sumOfSwapOverTime / count;
            double totalMaintenanceTime = job.getTargetMaintenanceTimePerDay() * totalDays;
            wireLineCount = wireLineCount == 0 ? 1 : wireLineCount;
            double  totalWireLineTime = sumOfWireLineTime / wireLineCount;
            Map<String, Double> fracTime = createTimeConsume(totalTargetDailyPumpTime, sumOfPumpTime);
            Map<String, Double> swapTime = createTimeConsume(job.getSwapOverTime(), totalSwapOverTime);
            Map<String, Double> maintenance = createTimeConsume(totalMaintenanceTime, sumOfPlanedMaintenance);
            Map<String, Double> wireline = createTimeConsume(job.getTargetWirelineTimePerStage(), totalWireLineTime);
            TimeTracking timeTracking = new TimeTracking();
                timeTracking.setFrac(fracTime);
            timeTracking.setWireline(wireline);
                timeTracking.setSwap(swapTime);
                timeTracking.setMaintenance(maintenance);
            return new ResponseEntity<>(timeTracking, HttpStatus.OK);

        } catch (Exception e) {
            Error error = Error.builder().errorCode(ErrorConstants.ERROR_WHILE_FETCHING_TIME_TRACKING_DATA_CODE)
                    .errorMessage(ErrorConstants.ERROR_WHILE_FETCHING_TIME_TRACKING_DATA_MESSAGE).build();
            return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity getCleanVolumeData(HttpServletRequest request, String jobId, String wellId, ChemicalEnum chemicalEnum) {
        try {
            Optional<Job> jobList = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(jobList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = jobList.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }

            List<PumpSchedule> pumpScheduleList = pumpScheduleMongoDbRepository.findByJobId(jobId);
            List<EndStageEmail> endStageEmails = endStageEmailMongoDbRepository.findByTypeAndJobId(EmailType.END_STAGE, jobId);
            Map<String, EndStageEmail> latestEmailsMap = new HashMap<>();
            for (EndStageEmail email : endStageEmails) {
                String key = email.getJobId() + "-" + email.getWell() + "-" + email.getStage();
                if (!latestEmailsMap.containsKey(key) || email.getCreated() > latestEmailsMap.get(key).getCreated()) {
                    latestEmailsMap.put(key, email);
                }
            }
            List<EndStageEmail> latestEndStageEmails = new ArrayList<>(latestEmailsMap.values());
            Map<String, ChemicalResponse> chemicalResponseMap = new HashMap<>();

            if (chemicalEnum.equals(ChemicalEnum.JOB)) {
                for (Well well : job.getWells()) {
                    ChemicalResponse chemicalResponse = new ChemicalResponse();
                    float design = 0;
                    float actual = 0;
                    for (EndStageEmail endStageEmail : latestEndStageEmails) {
                        if (endStageEmail.getWell().equals(well.getName())) {
                            actual += endStageEmail.getTotalCleanFluid()*42;
                        }
                    }
                    for (PumpSchedule pumpSchedule : pumpScheduleList) {
                        if (pumpSchedule.getWellId().equals(well.getId())) {
                            design += pumpSchedule.getCleanVol() * 42;
                        }
                    }
                    chemicalResponse.setActual(actual / well.getTotalStages());
                    chemicalResponse.setDesign(design / well.getTotalStages());
                    chemicalResponseMap.put(well.getName(), chemicalResponse);
                }
            } else if (chemicalEnum.equals(WELL)) {
                if (ObjectUtils.isEmpty(wellId)) {
                    Error error = Error.builder()
                            .errorCode(com.carbo.job.utils.ErrorConstants.WELL_ID_IS_MANDATORY_CODE)
                            .errorMessage(com.carbo.job.utils.ErrorConstants.WELL_ID_IS_MANDATORY_MESSAGE)
                            .build();
                    return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
                }
                Set<String> allStages = new HashSet<>();
                String wellName = "";
                for (Well well : job.getWells()) {
                    if (well.getId().equalsIgnoreCase(wellId)) {
                        for (int i = 1; i <= well.getTotalStages(); i++) {
                            allStages.add(Integer.toString(i));
                        }
                        allStages.addAll(well.getAdditionalFieldTicketNames());
                        wellName = well.getName();
                        break;
                    }
                }
                for (String stage : allStages) {
                    ChemicalResponse chemicalResponse = new ChemicalResponse();
                    float design = 0;
                    float actual = 0;
                    for (EndStageEmail endStageEmail : latestEndStageEmails) {
                        if (endStageEmail.getWell().equals(wellName) && endStageEmail.getStage().equals(stage)) {
                            actual += endStageEmail.getTotalCleanFluid()*42;
                        }
                    }
                    for (PumpSchedule pumpSchedule : pumpScheduleList) {
                        int stageNumber = Math.round(pumpSchedule.getStageNumber());
                        if (pumpSchedule.getWellId().equals(wellId) && (stageNumber + "").equalsIgnoreCase(stage)) {
                            design += pumpSchedule.getCleanVol() * 42;
                        }
                    }
                    chemicalResponse.setActual(actual);
                    chemicalResponse.setDesign(design);
                    chemicalResponseMap.put(stage, chemicalResponse);
                }
            }

            return ResponseEntity.ok(chemicalResponseMap);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_VOLUME_DATA_CODE)
                    .errorMessage(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_CHEMICAL_VOLUME_DATA_MESSAGE).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity getProppantPlaced(HttpServletRequest request, String jobId, String wellId, ChemicalEnum chemicalEnum) {
        try {
            if (ObjectUtils.isEmpty(wellId)) {
                Error error = Error.builder()
                        .errorCode(com.carbo.job.utils.ErrorConstants.WELL_ID_IS_MANDATORY_CODE)
                        .errorMessage(com.carbo.job.utils.ErrorConstants.WELL_ID_IS_MANDATORY_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
//         when we search by id then it will return single value not in list
            Optional<Job> jobList = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(jobList)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = jobList.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }

            List<ProppantDeliveryEntry> proppantDeliveryEntries = proppantDeliveryMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            Set<String> allStages = new HashSet<>();
            String wellName = "";
            for (Well well : job.getWells()) {
                if (well.getId().equalsIgnoreCase(wellId)) {
                    for (int i = 1; i <= well.getTotalStages(); i++) {
                        allStages.add(Integer.toString(i));
                    }
                    allStages.addAll(well.getAdditionalFieldTicketNames());
                    wellName = well.getName();
                    break;
                }
            }

            // here first String contains Stage and another string contains Proppant Name
            Map<String, Map<String, PercentDifference>> proppantPlacedMap = new HashMap<>();

            for (String stage : allStages) {
                Map<String, PercentDifference> proppants = new HashMap<>();
                for (Well well : job.getWells()) {
                    if (well.getId().equalsIgnoreCase(wellId)) {
                        for (Proppant proppant : well.getProppants()) {
                            String proppantName = proppant.getName();
                            float designVolume = 0.0f;
                            if(job.isNewWorkflow()){
                                double designPerWell = proppant.getCalculatedVolume().getPerWell().get(well.getName());
                                int numberOfStages = proppant.getCalculatedVolume().getPerStage().size();
                                if(chemicalEnum.equals(STAGE)){
                                    Double value = proppant.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                    designVolume = value.floatValue();
                                }else {
                                    designVolume = (float) (designPerWell/numberOfStages);
                                }
                            } else {
                                designVolume = proppant.getVolumePerStage() == null ? 0.0f : proppant.getVolumePerStage();
                            }
                            proppants.put(proppantName, new PercentDifference(designVolume, 0.0f, 0.0f));
                        }
                        Map<String, Double> proppantActualVolumes = new HashMap<>();
                        for (ProppantDeliveryEntry proppantDeliveryEntry : proppantDeliveryEntries) {
                            for (ProppantUsed proppantUsed : proppantDeliveryEntry.getUsedIn()) {
                                int stageNumber = Math.round(proppantUsed.getStage());
                                if (proppantUsed.getWell().equalsIgnoreCase(wellName) && (stageNumber + "").equalsIgnoreCase(stage)) {
                                    String proppantName = proppantDeliveryEntry.getProppant();
                                    double actualVolume = proppantUsed.getSubmittedAmount();
                                    // Check if proppant already exists in the map
                                    if (proppantActualVolumes.containsKey(proppantName)) {
                                        // If proppant exists, add the actual volume to the existing value
                                        double aggregatedVolume = proppantActualVolumes.get(proppantName) + actualVolume;
                                        proppantActualVolumes.put(proppantName, aggregatedVolume);
                                    } else {
                                        // If proppant is not in the map, add it with the current actual volume
                                        proppantActualVolumes.put(proppantName, actualVolume);
                                    }
                                }
                            }
                        }
                        for (Map.Entry<String, Double> entry : proppantActualVolumes.entrySet()) {
                            String proppantName = entry.getKey();
                            double aggregatedActualVolume = entry.getValue();
                                    if (proppants.containsKey(proppantName)) {
                                        PercentDifference chemicalResponse = proppants.get(proppantName);
                                chemicalResponse.setActual((float) aggregatedActualVolume);
                                        chemicalResponse.setPercentDiff((float) calculatePercentageDifference(chemicalResponse.getActual(), chemicalResponse.getDesign()));
                                    }
                                }
                            }
                        }

                // Put the proppants map for the current stage into the main map
                proppantPlacedMap.put(stage, proppants);
            }
            for (Map.Entry<String, Map<String, PercentDifference>> entry : proppantPlacedMap.entrySet()) {
                float totalActual = 0.0f;
                float totalDesign = 0.0f;

                // Calculate totalActual and totalDesign for the current stage
                for (Map.Entry<String, PercentDifference> innerMap : entry.getValue().entrySet()) {
                    totalActual += innerMap.getValue().getActual();
                    totalDesign += innerMap.getValue().getDesign();
                }

                // Calculate newPercentageDifference for the current stage
                float newPercentageDifference = (totalActual / totalDesign) * 100;

                // Set newPercentageDifference for each proppant in the current stage
                for (Map.Entry<String, PercentDifference> innerMap : entry.getValue().entrySet()) {
                    PercentDifference percentDifference = innerMap.getValue();

                    // Calculate and set the newPercentageDifference for each proppant
                    percentDifference.setPercentDiff(newPercentageDifference);
                }
            }
            Map<String, Map<String, PercentDifference>> filteredProppantPlacedMap = new HashMap<>();
            for (Map.Entry<String, Map<String, PercentDifference>> entry : proppantPlacedMap.entrySet()) {
                String stage = entry.getKey();
                Map<String, PercentDifference> proppants = entry.getValue();

                if (proppants.size() > 1) {
                    Map<String, PercentDifference> filteredProppants = new HashMap<>();
                    for (Map.Entry<String, PercentDifference> proppantEntry : proppants.entrySet()) {
                        PercentDifference percentDifference = proppantEntry.getValue();
                        if (percentDifference.getActual() != 0.0f) {
                            filteredProppants.put(proppantEntry.getKey(), percentDifference);
                        }
                    }

                    if (!filteredProppants.isEmpty()) {
                        filteredProppantPlacedMap.put(stage, filteredProppants);
                    }
                } else if (proppants.size() == 1) {
                    PercentDifference percentDifference = proppants.values().iterator().next();

                    if (percentDifference.getActual() != 0.0f) {
                        filteredProppantPlacedMap.put(stage, proppants);
                    }
                }
            }
            return ResponseEntity.ok(filteredProppantPlacedMap);

        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_PROPPANT_PLACED_DATA_CODE)
                    .errorMessage(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_PROPPANT_PLACED_DATA_MESSAGE).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void getMapChemicalResponse(Chemical chemical, ChemicalStage chemicalStage, Map<String, ChemicalResponse> chemicalResponseMap, boolean isNewWorkflow, ChemicalEnum chemicalEnum) {
        float design = 0.0f;
        float actual = 0.0f;
        int count = 0;
        if (!ObjectUtils.isEmpty(chemicalResponseMap) && chemicalResponseMap.containsKey(chemical.getName())) {
            ChemicalResponse singleChemicalData = chemicalResponseMap.get(chemical.getName());
            design = singleChemicalData.getDesign();
            actual = singleChemicalData.getActual();
            count = singleChemicalData.getCount();
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getChemicalAdditionUnit1())) {
            for (Strap strap : chemicalStage.getChemicalAdditionUnit1()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if (chemical.getCode().equals(strap.getChemical().getCode())) {
                        if(isNewWorkflow){
                            double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                            int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                            if(chemicalEnum.equals(STAGE)){
                                int stage = Math.round(chemicalStage.getStage());
                                Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                design = value.floatValue();
                            }else {
                                design = (float) (design + (designPerWell / numberOfStages));
                            }
                        } else {
                            design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                        }
                        actual = actual + strap.getRawUsed();
                        count++;
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getChemicalAdditionUnit2())) {
            for (Strap strap : chemicalStage.getChemicalAdditionUnit2()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if (chemical.getCode().equals(strap.getChemical().getCode())) {
                        if(isNewWorkflow){
                            double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                            int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                            if(chemicalEnum.equals(STAGE)){
                                int stage = Math.round(chemicalStage.getStage());
                                Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                design = value.floatValue();
                            }else {
                                design = (float) (design + (designPerWell / numberOfStages));
                            }
                        } else {
                            design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                        }
                        actual = actual + strap.getRawUsed();
                        count++;
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getDryAdd())) {
            for (Strap strap : chemicalStage.getDryAdd()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if (chemical.getCode().equals(strap.getChemical().getCode())) {
                        if(isNewWorkflow){
                            double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                            int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                            if(chemicalEnum.equals(STAGE)){
                                int stage = Math.round(chemicalStage.getStage());
                                Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                                design = value.floatValue();
                            }else {
                                design = (float) (design + (designPerWell / numberOfStages));
                            }
                        } else {
                            design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                        }
                        actual = actual + strap.getRawUsed();
                        count++;
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(chemicalStage.getIsosTransport())) {
            for (Strap strap : chemicalStage.getIsosTransport()) {
                if (chemical.getCode().equals(strap.getChemical().getCode())) {
                    if(isNewWorkflow){
                        double designPerWell = chemical.getCalculatedVolume().getPerWell().get(chemicalStage.getWell());
                        int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                        if(chemicalEnum.equals(STAGE)){
                            int stage = Math.round(chemicalStage.getStage());
                            Double value = chemical.getCalculatedVolume().getPerStage().get(String.valueOf(stage));
                            design = value.floatValue();
                        }else {
                            design = (float) (design + (designPerWell / numberOfStages));
                        }
                    } else {
                        design = design + (chemical.volumePerStage == null ? 0.0f : chemical.volumePerStage);
                    }
                    actual = actual + strap.getRawUsed();
                    count++;
                }
            }
        }
        chemicalResponseMap.put(chemical.getName(), aggregateChemical(design, actual, count));
    }

    private ChemicalResponse aggregateChemical(float design, float actual, int count) {
        PercentDifference response = new PercentDifference();
        response.setCount(count);
        response.setActual(actual);
        response.setDesign(design);
        return response;
    }

    public ResponseEntity getMaterialInventory(HttpServletRequest request, String jobId) {
        try {
            Optional<Job> optionalJob = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(optionalJob)) {
                Error error = Error.builder()
                        .errorCode(ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(ErrorConstants.JOB_NOT_FOUND_MESSAGE)
                        .build();
                return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
            }
            Job job = optionalJob.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            MaterialNeeded material = materialNeededRepository.findByJobIdAndOrganizationId(jobId, organizationId);
            if (ObjectUtils.isEmpty(material)) {
                Error error = Error.builder()
                        .errorCode(com.carbo.job.utils.ErrorConstants.UNABLE_TO_GET_CHEMICAL_NEEDED_DATA_CODE)
                        .errorMessage(com.carbo.job.utils.ErrorConstants.UNABLE_TO_GET_CHEMICAL_NEEDED_DATA_MESSAGE).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            List<ChemicalStage> chemicalStageList = chemicalStageMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            List<ProppantDeliveryEntry> proppantDeliveryEntryList = proppantDeliveryMongoDbRepository.findByOrganizationIdAndJobId(organizationId, jobId);
            Map<MaterialEnum, List<Map<String, String>>> response = new HashMap<>();
            List<Map<String, String>> chemicalData = new ArrayList<>();
            List<Map<String, String>> proppantData = new ArrayList<>();
            for (MaterialData materialData : material.getMaterialData()) {


                String materialName = materialData.getMaterialName();
                float value = materialData.getNeededPerUnit();
                if (materialData.getMaterialType().equals(MaterialEnum.CHEMICAL)) {
                    double rowUsed = 0;
                    for (ChemicalStage chemicalStage : chemicalStageList) {
                        List<Strap> strapAddition1List = chemicalStage.getChemicalAdditionUnit1();
                        if (!ObjectUtils.isEmpty(strapAddition1List)) {
                            for (Strap strap : strapAddition1List) {
                                String code = strap.getChemical().getCode();
                                if (code.equals(materialData.getCode())) {
                                    rowUsed += strap.getRawUsed();
                                }
                            }
                        }

                        List<Strap> strapAddition2List = chemicalStage.getChemicalAdditionUnit2();
                        if (!ObjectUtils.isEmpty(strapAddition2List)) {
                            for (Strap strap : strapAddition2List) {
                                String code = strap.getChemical().getCode();
                                if (code.equals(materialData.getCode())) {
                                    rowUsed += strap.getRawUsed();
                                }
                            }
                        }
                        List<Strap> strapDryAddList = chemicalStage.getDryAdd();
                        if (!ObjectUtils.isEmpty(strapDryAddList)) {
                            for (Strap strap : strapDryAddList) {
                                String code = strap.getChemical().getCode();
                                if (code.equals(materialData.getCode())) {
                                    rowUsed += strap.getRawUsed();
                                }
                            }
                        }
                        List<Strap> strapIsosList = chemicalStage.getIsosTransport();
                        if (!ObjectUtils.isEmpty(strapIsosList)) {
                            for (Strap strap : strapIsosList) {
                                String code = strap.getChemical().getCode();
                                if (code.equals(materialData.getCode())) {
                                    rowUsed += strap.getRawUsed();
                                }
                            }
                        }
                    }
                    Map<String, String> data = getResponse(materialName, value, rowUsed);
                    chemicalData.add(data);
                }
                if (materialData.getMaterialType().equals(MaterialEnum.PROPPANT)) {
                    double rowUsed = 0;
                    for (ProppantDeliveryEntry proppantDeliveryEntry : proppantDeliveryEntryList) {

                        rowUsed += proppantDeliveryEntry.getWtAmount();
                    }
                    Map<String, String> data = getResponse(materialName, value, rowUsed);
                    proppantData.add(data);
                }
            }
            response.put(MaterialEnum.CHEMICAL, chemicalData);
            response.put(MaterialEnum.PROPPANT, proppantData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_MATERIAL_INVENTORY_DATA_CODE)
                    .errorMessage(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_MATERIAL_INVENTORY_DATA_MESSAGE).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private Map<String, String> getResponse(String name, double design, double actual) {
        double percentageDiff = 0;
        if (design != 0) {
            percentageDiff = calculatePercentageDifference(design, actual);
        }
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("design", String.valueOf(design));
        data.put("actual", String.valueOf(actual));
        data.put("percentage", String.valueOf(percentageDiff));
        return data;
    }

    public ResponseEntity createMaterialNeeded(HttpServletRequest request, MaterialNeeded materialNeeded) {
        try {

            Optional<Job> job1 = jobMongoDbRepository.findById(materialNeeded.getJobId());
            if (ObjectUtils.isEmpty(job1)) {
                Error error = Error.builder()
                        .errorCode(com.carbo.job.utils.ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(com.carbo.job.utils.ErrorConstants.JOB_NOT_FOUND_MESSAGE).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            Job job = job1.get();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            MaterialNeeded materialNeeded1 = materialNeededRepository.findByJobIdAndOrganizationId(materialNeeded.getJobId(), organizationId);
            if (ObjectUtils.isEmpty(materialNeeded1)) {
                materialNeeded.setOrganizationId(organizationId);
                materialNeeded.setAuditDetails(setAuditDetails(request, materialNeeded.getAuditDetails()));
                MaterialNeeded response = materialNeededRepository.save(materialNeeded);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                materialNeeded1.setMaterialData(materialNeeded.getMaterialData());
                materialNeeded1.setAuditDetails(setAuditDetails(request, materialNeeded1.getAuditDetails()));
                MaterialNeeded response = materialNeededRepository.save(materialNeeded1);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(com.carbo.job.utils.ErrorConstants.UNABLE_TO_CREATE_CHEMICAL_NEEDED_DATA_CODE)
                    .errorMessage(com.carbo.job.utils.ErrorConstants.UNABLE_TO_CREATE_CHEMICAL_NEEDED_DATA_MESSAGE).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity getMaterialNeeded(HttpServletRequest request, String jobId, boolean isChemical) {
        try {
            Optional<Job> job1 = jobMongoDbRepository.findById(jobId);
            if (ObjectUtils.isEmpty(job1)) {
                Error error = Error.builder()
                        .errorCode(com.carbo.job.utils.ErrorConstants.JOB_NOT_FOUND_CODE)
                        .errorMessage(com.carbo.job.utils.ErrorConstants.JOB_NOT_FOUND_MESSAGE).build();
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            Job job = job1.get();
            boolean isNewWorkFlow = job.isNewWorkflow();
            String organizationId;
            String organizationType = getOrganizationType(request);
            if (organizationType.contentEquals("OPERATOR")) {
                organizationId = job.getOrganizationId();
            } else {
                organizationId = getOrganizationId(request);
            }
            MaterialNeeded material = materialNeededRepository.findByJobIdAndOrganizationId(jobId, organizationId);
//            Set<MaterialData> materialData = new HashSet<>();
            Comparator<MaterialData> materialNameComparator = Comparator.comparing(MaterialData::getMaterialName);

            // Create a TreeSet with the material name comparator
            Set<MaterialData> materialData = new TreeSet<>(materialNameComparator);
            if (isChemical) {
                List<MaterialData> filterData = new ArrayList<>();
                if (material != null && !material.getMaterialData().isEmpty()) {
                    filterData = material.getMaterialData().stream().filter(data -> MaterialEnum.CHEMICAL.equals(data.getMaterialType()))
                            .collect(Collectors.toList());
                }
                Map<String, List<Chemical>> checkAvg = new HashMap<>();
                boolean isChemicalFound = false;
                for (Well well : job.getWells()) {
                    if (!ObjectUtils.isEmpty(well.getAcidAdditives())) {
                        List<Chemical> acidAdditivesList = well.getAcidAdditives();
                        addMaterialDataForChemicalsAvg(acidAdditivesList, checkAvg);
                        isChemicalFound = true;
                    }
                    if (!ObjectUtils.isEmpty(well.getSlickwaters())) {
                        List<Chemical> slickwatersList = well.getSlickwaters();
                        addMaterialDataForChemicalsAvg(slickwatersList, checkAvg);
                        isChemicalFound = true;
                    }
                    if (!ObjectUtils.isEmpty(well.getLinearGelCrosslinks())) {
                        List<Chemical> linearGelCrosslinksList = well.getLinearGelCrosslinks();
                        addMaterialDataForChemicalsAvg(linearGelCrosslinksList, checkAvg);
                        isChemicalFound = true;
                    }
                    if (!ObjectUtils.isEmpty(well.getDiverters())) {
                        List<Chemical> divertersList = well.getDiverters();
                        addMaterialDataForChemicalsAvg(divertersList, checkAvg);
                        isChemicalFound = true;
                    }
                    if (!ObjectUtils.isEmpty(well.getAdditionalChemicalTypes())) {
                        for (Map.Entry<String, List<Chemical>> entry : well.getAdditionalChemicalTypes().entrySet()) {
                            List<Chemical> divertersList = entry.getValue();
                            addMaterialDataForChemicalsAvg(divertersList, checkAvg);
                            isChemicalFound = true;
                        }
                    }
                }
                if (!isChemicalFound) {
                    Error error = Error.builder()
                            .errorCode(com.carbo.job.utils.ErrorConstants.CHEMICAL_DATA_NOT_FOUND_CODE)
                            .errorMessage(com.carbo.job.utils.ErrorConstants.CHEMICAL_DATA_NOT_FOUND_MESSAGE).build();
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
                for (Map.Entry<String, List<Chemical>> entry : checkAvg.entrySet()) {
                    List<Chemical> dataList = entry.getValue();
                    // Calculate average for the MaterialData values in the list
                    double sum = dataList.stream()
                            .mapToDouble(chemical -> {
                                if (isNewWorkFlow) {
                                    Collection<Double> values = chemical.getCalculatedVolume().getPerWell().values();
                                    double designPerWell = values.stream().mapToDouble(Double::doubleValue).sum();
                                    int numberOfStages = chemical.getCalculatedVolume().getPerStage().size();
                                    return numberOfStages > 0 ? designPerWell / numberOfStages : 0.0;
                                } else {
                                    return chemical.getVolumePerStage() == null ? 0.0 : chemical.getVolumePerStage();
                                }
                            })
                            .sum();
                    double average = sum / dataList.size();
                    Chemical returnData = dataList.get(0);
                    Optional<MaterialData> foundData = Optional.empty();
                    if (material != null && !material.getMaterialData().isEmpty()) {
                        foundData = filterData.stream().filter(data -> returnData.getCode().equals(data.getCode()) && returnData.getName().equals(data.getMaterialName()))
                                .findFirst();
                    }

                    MaterialData setMaterialData;
                    if (foundData.isPresent()) {
                        MaterialData returnResponseData = foundData.get();
                        setMaterialData = updateMaterialData(returnResponseData.getUnit(), returnResponseData.getNeededPerUnit(), returnResponseData.getMaterialName(), returnResponseData.getCode(),
                                (float) average, job, MaterialEnum.CHEMICAL);
                    } else {
                        setMaterialData = updateMaterialData(null, 0, returnData.getName(), returnData.getCode(), (float) average, job, MaterialEnum.CHEMICAL);
                    }

                    materialData.add(setMaterialData);
                }

            } else {
                List<MaterialData> filterData = new ArrayList<>();
                if (material != null && !material.getMaterialData().isEmpty()) {
                    filterData = material.getMaterialData().stream().filter(data -> MaterialEnum.PROPPANT.equals(data.getMaterialType()))
                            .collect(Collectors.toList());
                }
                boolean isProppantFound = false;
                Map<String, List<Proppant>> checkAvg = new HashMap<>();
                for (Well well : job.getWells()) {
                    if (!ObjectUtils.isEmpty(well.getProppants())) {
                        List<Proppant> proppantList = well.getProppants();
                        addMaterialDataForProppantsAvg(proppantList, checkAvg);
                        isProppantFound = true;
                    }
                }
                if (!isProppantFound) {
                    Error error = Error.builder()
                            .errorCode(com.carbo.job.utils.ErrorConstants.PROPPANT_DATA_NOT_FOUND_CODE)
                            .errorMessage(com.carbo.job.utils.ErrorConstants.PROPPANT_DATA_NOT_FOUND_MESSAGE).build();
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }

                for (Map.Entry<String, List<Proppant>> entry : checkAvg.entrySet()) {
                    List<Proppant> dataList = entry.getValue();
                    // Calculate average for the MaterialData values in the list
                    double sum = dataList.stream()
                            .mapToDouble(proppant -> {
                                if (isNewWorkFlow) {
                                    Collection<Double> values = proppant.getCalculatedVolume().getPerWell().values();
                                    double designPerWell = values.stream().mapToDouble(Double::doubleValue).sum();
                                    int numberOfStages = proppant.getCalculatedVolume().getPerStage().size();
                                    return numberOfStages > 0 ? designPerWell / numberOfStages : 0.0;
                                } else {
                                    return proppant.getVolumePerStage() == null ? 0.0 : proppant.getVolumePerStage();
                                }
                            })
                            .sum();
                    double average = sum / dataList.size();
                    Proppant returnData = dataList.get(0);
                    Optional<MaterialData> foundData = Optional.empty();
                    if (material != null && !material.getMaterialData().isEmpty()) {
                        foundData = filterData.stream().filter(data -> returnData.getCode().equals(data.getCode()) && returnData.getName()
                                        .equals(data.getMaterialName()))
                                .findFirst();
                    }
                    MaterialData setMaterialData;
                    if (foundData.isPresent()) {
                        MaterialData returnResponseData = foundData.get();
                        setMaterialData = updateMaterialData(returnResponseData.getUnit(), returnResponseData.getNeededPerUnit(), returnResponseData.getMaterialName(), returnResponseData.getCode(),
                                (float) average, job, MaterialEnum.PROPPANT);
                    } else {
                        setMaterialData = updateMaterialData(null, 0, returnData.getName(), returnData.getCode(), (float) average, job, MaterialEnum.PROPPANT);
                    }

                    materialData.add(setMaterialData);
                }
            }
            if (material == null) {
                material = new MaterialNeeded();
                material.setJobId(jobId);
                material.setOrganizationId(organizationId);
            }
            material.setMaterialData(materialData);
            return new ResponseEntity<>(material, HttpStatus.OK);
        } catch (Exception e) {
            Error error = Error.builder()
                    .errorCode(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_MATERIAL_INVENTORY_DATA_CODE)
                    .errorMessage(com.carbo.job.utils.ErrorConstants.UNABLE_TO_FETCH_MATERIAL_INVENTORY_DATA_MESSAGE).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addMaterialDataForChemicalsAvg(List<Chemical> chemicals, Map<String, List<Chemical>> checkAvg) {
        for (Chemical chemical : chemicals) {
            // Check if the chemical name exists as a key in the map
            if (checkAvg.containsKey(chemical.getName())) {
                // If present, add the MaterialData to the existing list
                checkAvg.get(chemical.getName()).add(chemical);
            } else {
                // If not present, create a new list and put it into the map
                List<Chemical> newList = new ArrayList<>();
                newList.add(chemical);
                checkAvg.put(chemical.getName(), newList);
            }
        }
    }

    private void addMaterialDataForProppantsAvg(List<Proppant> proppants, Map<String, List<Proppant>> checkAvg) {
        for (Proppant proppant : proppants) {
            // Check if the chemical name exists as a key in the map
            if (checkAvg.containsKey(proppant.getName())) {
                // If present, add the MaterialData to the existing list
                checkAvg.get(proppant.getName()).add(proppant);
            } else {
                // If not present, create a new list and put it into the map
                List<Proppant> newList = new ArrayList<>();
                newList.add(proppant);
                checkAvg.put(proppant.getName(), newList);
            }
        }
    }


    private MaterialData updateMaterialData(UnitNeededEnum unit, float neededPerUnit, String materialName, String code, float volumePerStage, Job job, MaterialEnum materialEnum) {
        MaterialData setMaterialData = new MaterialData();
        setMaterialData.setMaterialName(materialName);
        setMaterialData.setMaterialType(materialEnum);
        setMaterialData.setCode(code);
        setMaterialData.setNeededPerUnit(neededPerUnit);
        setMaterialData.setUnit(unit);
        setMaterialData.setNeededTotal(checkUnitNeeded(unit, neededPerUnit, volumePerStage, job));
        return setMaterialData;
    }

    private float checkUnitNeeded(UnitNeededEnum unit, float neededPerUnit, float volumePerStage, Job job) {
        float totalNeeded = 0;
        if (unit != null && neededPerUnit != 0) {
            if (unit.equals(UnitNeededEnum.STAGES)) {
                totalNeeded = volumePerStage * neededPerUnit;
            } else if (unit.equals(UnitNeededEnum.DAYS)) {
                totalNeeded = job.getTargetStagesPerDay() * volumePerStage * neededPerUnit;

            } else {
                totalNeeded = neededPerUnit;
            }
        }
        return totalNeeded;
    }
}

// Unresolved import (framework/JDK): jakarta.servlet.http.HttpServletRequest
// Unresolved import (framework/JDK): org.springframework.http.ResponseEntity
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.*
// ===== Imported from: com.carbo.job.model.widget.MaterialNeeded =====
package com.carbo.job.model.widget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document (collection = "chemical-material-needed")
@CompoundIndex (name = "unique_jobId_index", def = "{'jobId': 1}", unique = true, sparse = true)
public class MaterialNeeded {
    @Id
    private String id;

    @Field ("jobId")
    private String jobId;

    @Field ("materialData")
    private Set<MaterialData> materialData;

    @Field("organizationId")
    private String organizationId;

    @Field("auditDetails")
    private AuditDetails auditDetails;

}

// ===== Imported from: com.carbo.job.model.widget.ChemicalEnum =====
package com.carbo.job.model.widget;

public enum ChemicalEnum {
    JOB,
    WELL,
    STAGE
}

// ===== Current file: src/main/java/com/carbo/job/controllers/widget/WidgetController.java =====
package com.carbo.job.controllers.widget;

import com.carbo.job.model.widget.ChemicalEnum;
import com.carbo.job.model.widget.MaterialNeeded;
import com.carbo.job.services.widget.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/widget")
public class WidgetController {
    private final WidgetService widgetService;

    @Autowired
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping("/chemical")
    public ResponseEntity getChemicalData(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false) String wellId, @RequestParam(required = false) Float stageNumber, @RequestParam(defaultValue = "JOB") ChemicalEnum chemicalEnum) {
        return widgetService.getChemicalPlotData(request, jobId, wellId, stageNumber, chemicalEnum);
    }

    @GetMapping("/cost-tracking")
    public ResponseEntity getChemicalCostData(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false) String wellId, @RequestParam(required = false) Float stageNumber, @RequestParam(defaultValue = "JOB") ChemicalEnum chemicalEnum) {
        return widgetService.getCostsTrackingData(request, jobId, wellId, stageNumber, chemicalEnum);
    }

    @GetMapping("/performanceParameter")
    public ResponseEntity getPerformanceParameter(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false, defaultValue = "1") String days, @RequestParam(required = false) boolean forDay) {
        return widgetService.getPerformanceParameter(request,jobId, Integer.parseInt(days),forDay);
    }

    @GetMapping("/proppantPlaced")
    public ResponseEntity getProppantPlaced(HttpServletRequest request,
                                            @RequestParam String jobId,
                                            @RequestParam String wellId,
                                            @RequestParam(defaultValue = "WELL") ChemicalEnum chemicalEnum) {
        return widgetService.getProppantPlaced(request, jobId, wellId, chemicalEnum);
    }

    @GetMapping("/cleanVolume")
    public ResponseEntity getCleanVolumeData(HttpServletRequest request,
                                             @RequestParam String jobId,
                                             @RequestParam(required = false) String wellId,
                                             @RequestParam(defaultValue = "JOB") ChemicalEnum chemicalEnum) {
        return widgetService.getCleanVolumeData(request, jobId, wellId, chemicalEnum);
    }

    @GetMapping("/material-usages")
    public ResponseEntity getMaterialData(HttpServletRequest request,
                                          @RequestParam String jobId,
                                          @RequestParam(required = false) String wellId,
                                          @RequestParam(required = false) Float stageNumber,
                                          @RequestParam(defaultValue = "WELL") ChemicalEnum chemicalEnum) {
        return widgetService.getMaterialUsagesData(request, jobId, wellId, stageNumber, chemicalEnum);
    }

    @GetMapping("/timeTracking")
    public ResponseEntity getTimeTracking(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false) Integer day) {
        return widgetService.getTimeTracking(request,jobId,day);
    }

    @GetMapping("/materialInventory")
    public ResponseEntity getMaterialInventory(HttpServletRequest request, @RequestParam String jobId) {
        return widgetService.getMaterialInventory(request,jobId);
    }

    @PostMapping ("/materialNeeded")
    public ResponseEntity createMaterialNeeded(HttpServletRequest request, @RequestBody MaterialNeeded materialNeeded) {
        return widgetService.createMaterialNeeded(request,materialNeeded);
    }

    @GetMapping("/get")
    public ResponseEntity getMaterialNeeded(HttpServletRequest request, @RequestParam String jobId,@RequestParam(required = false) boolean isChemical) {
        return widgetService.getMaterialNeeded(request,jobId,isChemical);
    }
}

