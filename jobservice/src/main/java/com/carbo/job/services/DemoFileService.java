package com.carbo.job.services;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.carbo.job.config.UploadConfig;
import com.carbo.job.config.WebClientConfig;
import com.carbo.job.exception.DemoDataException;
import com.carbo.job.ipc.client.ProposalServiceClient;
import com.carbo.job.model.*;
import com.carbo.job.model.Error.Error;
import com.carbo.job.model.Location;
import com.carbo.job.model.proposal.Proposal;
import com.carbo.job.model.widget.*;
import com.carbo.job.repository.OrganizationMongoDbRepository;
import com.carbo.job.utils.*;
import com.carbo.job.utils.ErrorConstants;
import com.opencsv.CSVReader;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.carbo.job.utils.Constants.*;
import static com.carbo.job.utils.ControllerUtil.getOrganizationId;
import static com.carbo.job.utils.ControllerUtil.getUserName;

@Service
public class DemoFileService {

    private final MongoTemplate mongoTemplate;
    private final SslContext sslContext;
    private final UploadConfig uploadConfig;
    private final WebClientConfig webClientConfig;
    private final OrganizationMongoDbRepository organizationMongoDbRepository;
    private final ProposalServiceClient proposalServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(DemoFileService.class);


    @Autowired
    public DemoFileService(MongoTemplate mongoTemplate, SslContext sslContext, UploadConfig uploadConfig, WebClientConfig webClientConfig, OrganizationMongoDbRepository organizationMongoDbRepository, ProposalServiceClient proposalServiceClient) {
        this.mongoTemplate = mongoTemplate;
        this.sslContext = sslContext;
        this.uploadConfig = uploadConfig;
        this.webClientConfig = webClientConfig;
        this.organizationMongoDbRepository=organizationMongoDbRepository;
        this.proposalServiceClient = proposalServiceClient;
    }

    public Map<String, Object> processFiles(MultipartFile files) throws Exception {
        Map<String, Object> data = new HashMap<>();
        String fileName = files.getOriginalFilename();
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            data.putAll(processExcelFile(files));
        } else if (fileName.endsWith(".csv")) {
            data.putAll(processCsvFile(files));
        }
        return data;
    }

    private Map<String, Object> processExcelFile(MultipartFile file) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            Cell keyCell = row.getCell(0);
            Cell valueCell = row.getCell(1);

            if (keyCell != null && valueCell != null) {
                String key = keyCell.getStringCellValue();
                if (valueCell.getCellType().equals(CellType.NUMERIC)) {
                    Double value = valueCell.getNumericCellValue();
                    data.put(key, value);
                } else {
                    String value = valueCell.getStringCellValue();
                    data.put(key, value);
                }
            }
        }
        workbook.close();
        return data;
    }


    private Map<String, Object> processCsvFile(MultipartFile file) throws Exception {
        Map<String, Object> data = new HashMap<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 2) {
                    String key = line[0];

                    String value = line[1];
                    try {
                        int intValue = Integer.parseInt(value);
                        data.put(key, (double) intValue);
                    } catch (NumberFormatException e) {
                        data.put(key, value);
                    }
                }
            }
        }
        return data;
    }

    public ResponseEntity<?> toCreateJob(HttpServletRequest request, String organizationId, Map<String, Object> result, String fileName) throws DemoDataException {
        List<String> requiredKeys = new ArrayList<>();
        Query orgQuery = new Query();
        orgQuery.addCriteria(Criteria.where(Constants.KEY_ID).is(organizationId));
        Organization organization = mongoTemplate.findOne(orgQuery, Organization.class);
        Map<Role, Boolean> mapRole = organization.getAccess();
        boolean flag = false;
        if (mapRole != null && mapRole.containsKey(Role.ROLE_SALES_USER)) {
            flag = mapRole.get(Role.ROLE_SALES_USER);
        }
        if (flag) {
            requiredKeys.add(Constants.FIELD_COORDINATOR_EMAIL);
        }

        DemoFile existedDemoFile = null;
        List<DemoFile> demoFiles = new ArrayList<>();
        Query queryForOrg = new Query();

        if (Objects.nonNull(fileName) && Objects.nonNull(organizationId)) {
            queryForOrg.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(getOrganizationId(request)));
            demoFiles = mongoTemplate.find(queryForOrg, DemoFile.class);

            if (!demoFiles.isEmpty()) {
                existedDemoFile = demoFiles.stream().filter(demoFile -> demoFile.getFileDesc().stream().anyMatch(fileDescription -> fileName.equals(fileDescription.getFileName()))).findFirst().orElse(null);
            }
        }
        Map<String, String> mapResponse = new HashMap<>();
        if (!ObjectUtils.isEmpty(result)) {
            requiredKeys.addAll(Arrays.asList(Constants.DISTRICT_NAME, Constants.FLEET_NAME, Constants.OPERATOR_NAME, Constants.LOCATION_NAME, Constants.PAD_NAME, Constants.TIME_ZONE, Constants.TARGET_TIME_PUMPER_DAY, Constants.JOB_NUMBER, Constants.WELL_NAME, Constants.AFE_NUMBER, Constants.LATITUDE, Constants.LONGITUDE, Constants.TOTAL_STAGES, Constants.WELL_API, Constants.VENDOR_WIRELINE_EMAIL, Constants.VENDOR_WIRELINE_NAME, Constants.VENDOR_WELLHEAD_NAME, Constants.VENDOR_WELLHEAD_EMAIL

            ));
            for (String key : requiredKeys) {
                if (!isKeyPresentAndNotNull(result, key)) {
                    mapResponse.put("message", "Key " + key + " is missing or null");
                    Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(mapResponse.get("message")).build();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
            }
            Query query = new Query();
            Object object = result.get(Constants.JOB_NUMBER);
            String strJobNumber = "";
            int intJobNumber = 0;
            if (object instanceof String) {
                strJobNumber = result.get(Constants.JOB_NUMBER).toString();
                query.addCriteria(Criteria.where(Constants.KEY_JOB_NUMBER).is(strJobNumber).and(Constants.ORGANIZATION_ID).is(organizationId));
            } else {
                Double doubleJobNumber = (double) result.get(Constants.JOB_NUMBER);
                intJobNumber = (int) Math.round(doubleJobNumber);
                query.addCriteria(Criteria.where(Constants.KEY_JOB_NUMBER).is(String.valueOf(intJobNumber)).and(Constants.ORGANIZATION_ID).is(organizationId));
            }
            if (!mongoTemplate.exists(query, Job.class)) {
                Operator operator = createOperator(request,result.get(Constants.OPERATOR_NAME), organizationId);
                District district;
                if (flag) {
                    district = createDistrict(result.get(Constants.DISTRICT_NAME), result.get(Constants.FIELD_COORDINATOR_EMAIL), organizationId);
                } else {
                    district = createDistrictForFalseFlag(result.get(Constants.DISTRICT_NAME), organizationId);
                }
                String proppantId = createPriceBookComponents(organizationId, result, request);
                Fleet fleet = createFleet(result.get(Constants.FLEET_NAME), district.getId(), organizationId);
                Location location = createLocation(result.get(Constants.LOCATION_NAME), organizationId);
                Pad pad = createPad(operator.getId(), result.get(Constants.PAD_NAME), result.get(Constants.TIME_ZONE), organizationId);
                Well well = createWell(result.get(Constants.WELL_NAME), result.get(Constants.WELL_API), pad.getId(), operator.getId(), result.get(Constants.AFE_NUMBER), result.get(Constants.TOTAL_STAGES), result.get(Constants.LATITUDE), result.get(Constants.LONGITUDE), organizationId);
                OnSiteEquipment onSiteEquipment = createOnSiteEquipment(result.get(Constants.EQUIPMENT_NAME), result.get(Constants.ONSITE_EQUIPMENT_TYPE), fleet.getId(), district.getName(), organizationId);
                String emailGroupId = createEmailGroup(result.get(Constants.EMAIL_GROUP_NAME), getUserName(request), organizationId, result.get(Constants.EMAIL_GROUP_EMAIL_NAME).toString());
                Vendor propVendor = createVendor(result.get(Constants.VENDOR_PROP_NAME), result.get(Constants.VENDOR_PROP_EMAIL), "Proppant", organizationId);
                Vendor chemVendor = createVendor(result.get(Constants.VENDOR_CHEM_NAME), result.get(Constants.VENDOR_CHEM_EMAIL), "Chemical", organizationId);
                Vendor wellHeadVendor = createVendor(result.get(Constants.VENDOR_WELLHEAD_NAME), result.get(Constants.VENDOR_WELLHEAD_EMAIL), "Wellhead", organizationId);
                Vendor wireLineVendor = createVendor(result.get(Constants.VENDOR_WIRELINE_NAME), result.get(Constants.VENDOR_WIRELINE_EMAIL), "Wireline", organizationId);
                List<Vendor> vendorList = new ArrayList<>();
                vendorList.add(propVendor);
                vendorList.add(chemVendor);
                vendorList.add(wellHeadVendor);
                vendorList.add(wireLineVendor);
                List<Well> wellList = new ArrayList<>();
                Map<String, List<Chemical>> additionalChemicalTypes = new HashMap<>();
                List<Chemical> acidAdditiveList = new ArrayList<>();
                Chemical chemicalAcid = new Chemical();
                chemicalAcid.setName(result.get(Constants.CHEMICAL_NAME).toString());
                chemicalAcid.setConcentration(result.get(Constants.CHEMICAL_DESIGN_CONCENTRATION).toString());
                chemicalAcid.setUom("gal");
                chemicalAcid.setSubtype("Acid");
                chemicalAcid.setCode(result.get(Constants.CHEMICAL_ITEM_CODE).toString());
                chemicalAcid.setVolumePerStage((float) ((double) result.get(Constants.CHEMICAL_DESIGNED_VOLUME_PER_STAGE)));
                acidAdditiveList.add(chemicalAcid);
                List<Integer> types = new ArrayList<Integer>();
                types.add(0);
                chemicalAcid.setTypes(types);
                additionalChemicalTypes.put("acidAdditives", acidAdditiveList);
                well.setAdditionalChemicalTypes(additionalChemicalTypes);

                List<Proppant> proppants = new ArrayList<>();
                Proppant proppant = new Proppant();
                proppant.setName(result.get(Constants.PROPANT_NAME).toString());
                proppant.setCode(result.get(Constants.PROPANT_ITEM_CODE).toString());
                proppant.setUom(result.get(Constants.PROPANT_UOM).toString());
                proppant.setVolumePerStage((float) ((double) result.get(Constants.PROPPANT_TOTAL_DESIGNED_VOLUME)));
                proppant.setPrice((float) ((double) result.get(Constants.PROPANT_PRICE)));
                proppant.setId(proppantId);
                proppants.add(proppant);
                well.setProppants(proppants);
                wellList.add(well);


                Job job = new Job();
                if (object instanceof String) {
                    job.setJobNumber(strJobNumber);
                } else {
                    job.setJobNumber(String.valueOf(intJobNumber));
                }
                job.setOrganizationId(organizationId);
                job.setVendors(vendorList);
                job.setWells(wellList);
                job.setDistrictId(district.getId());
                job.setFleet(fleet.getName());
                job.setOperator(operator.getName());
                job.setPad(pad.getName());
                job.setLocation(location.getName());
                job.setTargetDailyPumpTime((float) ((double) result.get(Constants.TARGET_TIME_PUMPER_DAY)));
                List<OnSiteEquipment> onSiteEquipmentList1 = new ArrayList<>();
                onSiteEquipmentList1.add(onSiteEquipment);
                job.setBlenders(onSiteEquipmentList1);
                job.setProppantSchematicType(job.getProppantSchematicType());
                job.setStatus(Constants.IN_PROGRESS);
                job.setTs(System.currentTimeMillis());
                mongoTemplate.save(job, "jobs");
                if (existedDemoFile != null) {
                    boolean fileNameExists = existedDemoFile.getFileDesc().stream().anyMatch(fileDescription -> fileName.equals(fileDescription.getFileName()));
                    if (fileNameExists) {
                        existedDemoFile.getFileDesc().forEach(fileDescription -> {
                            if (fileName.equals(fileDescription.getFileName())) {
                                fileDescription.setJobCreatedOrgId(organizationId);
                            }
                        });
                        demoFiles.get(0).setFileDesc(existedDemoFile.getFileDesc());
                        Update update = new Update().set("fileDesc", existedDemoFile.getFileDesc());
                        mongoTemplate.updateFirst(queryForOrg, update, DemoFile.class);
                    }
                }
                mapResponse.put(" Created Job with this jobId", job.getId());
                return ResponseEntity.ok(mapResponse);
            } else {
                mapResponse.put("message", " Please provide unique JobNumber");
                Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(mapResponse.get("message")).build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        mapResponse.put("message", "Data file is empty,please provide non-empty as well as required fields data file to create job");
        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(mapResponse.get("message")).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private District createDistrictForFalseFlag(Object districtName, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(districtName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));

        if (!mongoTemplate.exists(query, District.class)) {
            District district = new District();
            district.setOrganizationId(organizationId);
            district.setName(districtName.toString());
            district.setTs(System.currentTimeMillis());
            mongoTemplate.save(district, "districts");
            return district;
        } else {
            List<District> districtList = mongoTemplate.find(query, District.class);
            return districtList.get(0);
        }
    }

    private boolean isKeyPresentAndNotNull(Map<String, Object> map, String key) {
        if (!map.containsKey(key)) {
            return false;
        }
        Object value = map.get(key);
        if (value == null) {
            return false;
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private Vendor createVendor(Object vendorPropName, Object vendorPropEmail, String type, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(vendorPropName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));

        if (!mongoTemplate.exists(query, Vendor.class)) {
            Vendor vendor = new Vendor();
            vendor.setOrganizationId(organizationId);
            vendor.setName(vendorPropName.toString());
            List<Contact> contactList = new ArrayList<>();
            Contact contact = new Contact();
            contact.setEmail(vendorPropEmail.toString());
            contactList.add(contact);
            vendor.setContacts(contactList);
            vendor.setType(type);
            vendor.setTs(System.currentTimeMillis());
            mongoTemplate.save(vendor, "vendors");
            return vendor;
        } else {
            List<Vendor> vendorList = mongoTemplate.find(query, Vendor.class);
            return vendorList.get(0);
        }
    }

    private String createEmailGroup(Object emailGroupName, String userName, String organizationId, String emailGroupEmail) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(emailGroupName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));
        if (!mongoTemplate.exists(query, EmailGroup.class)) {
            EmailGroup emailGroup = new EmailGroup();
            emailGroup.setOrganizationId(organizationId);
            emailGroup.setName(emailGroupName.toString());
            List<Contact> contactList = new ArrayList<>();
            Contact contact = new Contact();
            contact.setName(userName);
            contactList.add(contact);
            contact.setEmail(emailGroupEmail);
            emailGroup.setContacts(contactList);
            emailGroup.setTs(System.currentTimeMillis());
            mongoTemplate.save(emailGroup, "email-groups");
            return emailGroup.getId();
        } else {
            List<EmailGroup> emailGroupList = mongoTemplate.find(query, EmailGroup.class);
            return emailGroupList.get(0).getId();
        }
    }

    private OnSiteEquipment createOnSiteEquipment(Object equipmentName, Object equipmentType, String fleetId, String districtName, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(equipmentName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));
        if (!mongoTemplate.exists(query, OnSiteEquipment.class)) {
            OnSiteEquipment onSiteEquipment = new OnSiteEquipment();
            onSiteEquipment.setOrganizationId(organizationId);
            onSiteEquipment.setName(equipmentName.toString());
            onSiteEquipment.setFleetId(fleetId);
            onSiteEquipment.setLocation(districtName);
            onSiteEquipment.setType(equipmentType.toString());
            onSiteEquipment.setTs(System.currentTimeMillis());
            mongoTemplate.save(onSiteEquipment, "on-site-equipments");
            return onSiteEquipment;
        } else {
            List<OnSiteEquipment> onSiteEquipmentList = mongoTemplate.find(query, OnSiteEquipment.class);
            return onSiteEquipmentList.get(0);
        }
    }

    private Well createWell(Object wellName, Object wellAPI, String id, String id1, Object afeNumber, Object totalStages, Object latitude, Object longitutde, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(wellName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));
        if (!mongoTemplate.exists(query, Well.class)) {
            Well well = new Well();
            well.setOrganizationId(organizationId);
            well.setName(wellName.toString());
            well.setApi(String.valueOf(wellAPI));
            well.setPadId(id);
            well.setOperatorId(id1);
            well.setAfeNumber(String.valueOf(afeNumber));
            well.setTotalStages(((Double) totalStages).intValue());
            well.setLatitude((double) latitude);
            well.setLongitude((double) longitutde);
            well.setTs(System.currentTimeMillis());
            mongoTemplate.save(well, "wells");
            return well;
        } else {
            List<Well> wellList = mongoTemplate.find(query, Well.class);
            return wellList.get(0);
        }
    }

    private Pad createPad(String id, Object padName, Object timezone, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(padName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));

        if (!mongoTemplate.exists(query, Pad.class)) {
            Pad pad = new Pad();
            pad.setOrganizationId(organizationId);
            pad.setOperatorId(id);
            pad.setName(padName.toString());
            pad.setTimezone(timezone.toString());
            pad.setTs(System.currentTimeMillis());
            mongoTemplate.save(pad, "pads");
            return pad;
        } else {
            List<Pad> padList = mongoTemplate.find(query, Pad.class);
            return padList.get(0);
        }
    }

    private Location createLocation(Object locationName, String organizationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(locationName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));

        if (!mongoTemplate.exists(query, Location.class)) {
            Location location = new Location();
            location.setOrganizationId(organizationId);
            location.setName(locationName.toString());
            location.setTs(System.currentTimeMillis());
            mongoTemplate.save(location, "locations");
            return location;
        } else {
            List<Location> locationList = mongoTemplate.find(query, Location.class);
            return locationList.get(0);
        }
    }

    private Operator createOperator(HttpServletRequest request,Object operatorName, String organizationId) throws DemoDataException {
            logger.info("Creating Operator with Name : {} ", operatorName);
            Query query = new Query();
            query.addCriteria(Criteria.where("name").regex("^"+operatorName.toString()+"$", "i").and(Constants.ORGANIZATION_ID).is(organizationId));
            if (!mongoTemplate.exists(query, Operator.class)) {
                /*
                 * If Organization has Sales Access and OperatorName / CompanyName does not exist
                 * then create Operator with Company Details
                 * else create new Operator Only.
                 */
                Organization organization= organizationMongoDbRepository.findById(organizationId)
                        .orElseThrow(()-> new DemoDataException("Organization Details not found"));
                if(organization.hasSalesAccess()) {
                    Query companyQuery = new Query();
                    companyQuery.addCriteria(Criteria.where("companyName").regex("^"+operatorName.toString()+"$", "i").and(ORGANIZATION_ID).is(organizationId));
                    Optional<Company> companyOpt =mongoTemplate.find(companyQuery, Company.class).stream().findFirst();
                    if (companyOpt.isPresent()){
                        logger.error("OperatorName used in company {}",companyOpt.get());
                        throw new DemoDataException("Operator "+operatorName.toString()+" used in Client Management");
                    }
                    Company company = new Company(operatorName.toString());
                    ResponseEntity<Company> companyResponse = this.proposalServiceClient.saveOrUpdateCompany(request,company);
                    if(companyResponse.getStatusCode().is2xxSuccessful() && !ObjectUtils.isEmpty(companyResponse.getBody()) && !StringUtils.isEmpty(companyResponse.getBody().getId())) {
                        logger.info("Company created Successfully {}",companyResponse.getBody());
                        String companyId = companyResponse.getBody().getId();
                        Operator operator = new Operator(organizationId,operatorName.toString(),companyId,System.currentTimeMillis());
                        return mongoTemplate.save(operator, "operators");
                    }else {
                        logger.error("Operator creation failed Sync Response {}",companyResponse);
                        throw new DemoDataException("Failed to create Operator.");
                    }
                }else{
                    logger.info("creating operator with name {}",operatorName);
                    Operator operator = new Operator(organizationId,operatorName.toString(),null,System.currentTimeMillis());
                    return mongoTemplate.save(operator, "operators");
                }
            } else {
                logger.error("operator already exists with name {}",operatorName);
                throw new DemoDataException("Operator "+operatorName.toString()+" Already Exists.");
            }

    }

    private Fleet createFleet(Object fleetName, String id, String organizationId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(fleetName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));

        if (!mongoTemplate.exists(query, Fleet.class)) {
            Fleet fleet = new Fleet();
            fleet.setOrganizationId(organizationId);
            fleet.setName(fleetName.toString());
            fleet.setDistrictId(id);
            fleet.setTs(System.currentTimeMillis());
            mongoTemplate.save(fleet, "fleets");
            return fleet;
        } else {
            List<Fleet> fleetList = mongoTemplate.find(query, Fleet.class);
            return fleetList.get(0);
        }
    }

    private District createDistrict(Object districtName, Object fieldCoordinatorEmail, String organizationId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(districtName.toString()).and(Constants.ORGANIZATION_ID).is(organizationId));

        if (!mongoTemplate.exists(query, District.class)) {
            District district = new District();
            district.setOrganizationId(organizationId);
            district.setName(districtName.toString());
            Set<String> setField = new HashSet<>();
            setField.add(fieldCoordinatorEmail.toString());
            district.setFieldCoordinatorEmail(setField);
            district.setTs(System.currentTimeMillis());
            mongoTemplate.save(district, "districts");
            return district;
        } else {
            List<District> districtList = mongoTemplate.find(query, District.class);
            return districtList.get(0);
        }
    }

    private String createPriceBookComponents(String organizationId, Map<String, Object> result, HttpServletRequest request) {
        Query query = new Query();
        String proppantId = "";
        Map<String, String> mapResponse = new HashMap<>();
        query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("priceBookName").is(Constants.PRICEBOOK_NAME));
        List<PriceBook> priceBooks = mongoTemplate.find(query, PriceBook.class);
        if (mongoTemplate.exists(query, PriceBook.class)) {
            String priceBookNameId = priceBooks.get(0).getId();
            List<String> requiredPropKeys = new ArrayList<>();
            requiredPropKeys.addAll(Arrays.asList(
                    Constants.PROPANT_NAME, Constants.PROPANT_ITEM_CODE, Constants.PROPANT_PRICE,
                    Constants.PROPANT_UOM, Constants.PROPANT_TYPE, Constants.PROPANT_SPECIFIC_GRAVITY
            ));
            boolean flagForProppant = false;
            for (String key : requiredPropKeys) {
                if (!isKeyPresentAndNotNull(result, key)) {
                    break;
                }
                flagForProppant = true;
            }
            Query queryForProppant = new Query();
            queryForProppant.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(result.get(Constants.PROPANT_NAME).toString()).and(Constants.PRICEBOOK_ID).is(priceBookNameId));
            if (mongoTemplate.exists(queryForProppant, PriceBookComponents.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide unique Proppant Name");
            }

            Query queryForProppants = new Query();
            queryForProppants.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("itemCode").is(result.get(Constants.PROPANT_ITEM_CODE).toString()).and(Constants.PRICEBOOK_ID).is(priceBookNameId));
            if (mongoTemplate.exists(queryForProppants, PriceBookComponents.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide unique Proppant Item Code");
            }
            Query queryForChemical = new Query();
            queryForChemical.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(result.get(Constants.CHEMICAL_NAME).toString()).and(Constants.PRICEBOOK_ID).is(priceBookNameId));
            if (mongoTemplate.exists(queryForChemical, PriceBookComponents.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide unique Chemical Name");
            }


            Query queryForChemicals = new Query();
            queryForChemicals.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("itemCode").is(result.get(Constants.CHEMICAL_ITEM_CODE).toString()).and(Constants.PRICEBOOK_ID).is(priceBookNameId));
            if (mongoTemplate.exists(queryForChemicals, PriceBookComponents.class)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide unique Chemical Item Code");
            }

            if (flagForProppant && !mongoTemplate.exists(queryForProppant, PriceBookComponents.class) && !mongoTemplate.exists(queryForProppants, PriceBookComponents.class)) {
                PriceBookComponents priceBookComponents = new PriceBookComponents();
                priceBookComponents.setPriceBookId(priceBookNameId);
                priceBookComponents.setPriceBookTypeEnum(PriceBookTypeEnum.PROPPANT);
                String typeEnum = result.get(Constants.PROPANT_TYPE).toString();
                Set<PriceBookComponentsTypeEnum> setTypeEnum = new HashSet<>();
                PriceBookComponentsTypeEnum enumValue = null;
                switch (typeEnum) {
                    case "Sand":
                        enumValue = PriceBookComponentsTypeEnum.SAND;
                        break;
                    case "Garnet":
                        enumValue = PriceBookComponentsTypeEnum.GARNET;
                        break;
                    case "Chrome":
                        enumValue = PriceBookComponentsTypeEnum.CHROME;
                        break;
                    case "Coolset":
                        enumValue = PriceBookComponentsTypeEnum.COOLSET;
                        break;
                    case "Econoprops":
                        enumValue = PriceBookComponentsTypeEnum.ECONOPROP;
                        break;
                    case "Hydroprop":
                        enumValue = PriceBookComponentsTypeEnum.HYDROPROP;
                        break;

                }
                setTypeEnum.add(enumValue);
                priceBookComponents.setType(setTypeEnum);
                priceBookComponents.setOrganizationId(organizationId);
                priceBookComponents.setName(result.get(Constants.PROPANT_NAME).toString());
                priceBookComponents.setItemCode(result.get(Constants.PROPANT_ITEM_CODE).toString());
                priceBookComponents.setPrice(result.get(Constants.PROPANT_PRICE).toString());
                priceBookComponents.setUom(result.get(Constants.PROPANT_UOM).toString());
                priceBookComponents.setSpecificGravity((double) result.get(Constants.PROPANT_SPECIFIC_GRAVITY));
                priceBookComponents.setAuditDetails(CommonUtils.setAuditDetails(request, priceBookComponents.getAuditDetails()));
                mongoTemplate.save(priceBookComponents, "pricebook-components-v2");
                proppantId = priceBookComponents.getId();
            }

            List<String> requiredChemKeys = new ArrayList<>();
            requiredChemKeys.addAll(Arrays.asList(
                    Constants.CHEMICAL_NAME, Constants.CHEMICAL_ITEM_CODE, Constants.CHEMICAL_PRICE,
                    Constants.CHEMICAL_UOM, Constants.CHEMICAL_TYPE, Constants.CHEMICAL_DESCRIPTION
            ));
            boolean flagForChemical = false;
            for (String key : requiredChemKeys) {
                if (!isKeyPresentAndNotNull(result, key)) {
                    break;
                }
                flagForChemical = true;
            }


            if (flagForChemical && !mongoTemplate.exists(queryForChemical, PriceBookComponents.class) && !mongoTemplate.exists(queryForChemicals, PriceBookComponents.class)) {
                PriceBookComponents priceBookComponents = new PriceBookComponents();
                priceBookComponents.setPriceBookId(priceBookNameId);
                priceBookComponents.setPriceBookTypeEnum(PriceBookTypeEnum.CHEMICAL);
                priceBookComponents.setOrganizationId(organizationId);
                String typeEnum = result.get(Constants.CHEMICAL_TYPE).toString();
                Set<PriceBookComponentsTypeEnum> setTypeEnum = new HashSet<>();
                PriceBookComponentsTypeEnum enumValue = null;
                switch (typeEnum) {
                    case "Acid_Additives":
                        enumValue = PriceBookComponentsTypeEnum.ACID_ADDITIVES;
                        break;
                    case "Linear_Gel":
                        enumValue = PriceBookComponentsTypeEnum.LINEAR_GEL;
                        break;
                    case "Slickwater":
                        enumValue = PriceBookComponentsTypeEnum.SLICKWATER;
                        break;
                    case "Diverter":
                        enumValue = PriceBookComponentsTypeEnum.DIVERTER;
                        break;
                }
                setTypeEnum.add(enumValue);
                priceBookComponents.setType(setTypeEnum);
                priceBookComponents.setName(result.get(Constants.CHEMICAL_NAME).toString());
                priceBookComponents.setItemCode(result.get(Constants.CHEMICAL_ITEM_CODE).toString());
                priceBookComponents.setPrice(result.get(Constants.CHEMICAL_PRICE).toString());
                priceBookComponents.setUom(result.get(Constants.CHEMICAL_UOM).toString());
                priceBookComponents.setDescription(result.get(Constants.CHEMICAL_DESCRIPTION).toString());
                if (result.get(Constants.CHEMICAL_DESCRIPTION).toString().equals("Acid")) {
                    priceBookComponents.setDilutionRate(result.get(Constants.DILUTION_RATE).toString());
                    priceBookComponents.setAcid(result.get(Constants.ACID_NAME).toString());
                }
                priceBookComponents.setAuditDetails(CommonUtils.setAuditDetails(request, priceBookComponents.getAuditDetails()));
                mongoTemplate.save(priceBookComponents, "pricebook-components-v2");
            }
        }
        return proppantId;
    }


    private static boolean isNumeric(String str) {
        // Check if the string is numeric using a regular expression
        return str != null && str.matches("\\d+");
    }

    /**
     * Delete Demo Data for the given organization and Demo File.
     *
     * @param fileName       for the getting file from azure.
     * @param organizationId in which demo data is being deleted.
     * @return A ResponseEntity containing  the delete  message with HttpStatus status,
     * or an Error object with HttpStatus.BAD_REQUEST or HttpStatus.INTERNAL_SERVER_ERROR.
     */
    public ResponseEntity deleteDemoData(String fileName, String organizationId, HttpServletRequest request) throws Exception {
        MultipartFile files = loadAsFileSystemResource(getOrganizationId(request), fileName);
        Map<String, Object> data = new HashMap<>();
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            data.putAll(processExcelFile(files));
        } else if (fileName.endsWith(".csv")) {
            data.putAll(processCsvFile(files));
        }

        if (!data.isEmpty()) {
            Object jobNumberValue = data.get(Constants.JOB_NUMBER);
            Object onSiteEquipmentName = data.get(Constants.EQUIPMENT_NAME);
            Object emailGroupName = data.get(Constants.EMAIL_GROUP_NAME);
            if (jobNumberValue != null) {
                String jobNumberString = jobNumberValue.toString();
                if (isNumeric(jobNumberString)) {
                    jobNumberValue = Integer.parseInt(jobNumberString);
                }
            }

            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_JOB_NUMBER).is(jobNumberValue));

            List<Job> jobs = mongoTemplate.find(query, Job.class);


            if (!jobs.isEmpty()) {
                Job job = jobs.get(0);
                String operatorName = job.getOperator();
                String fleetName = job.getFleet();
                String padName = job.getPad();
                String wellId = job.getWells().get(0).getId();
                List<Vendor> vendors = job.getVendors();
                String vendorId = null;
                String onSiteEquipment = onSiteEquipmentName.toString();
                String districtId = job.getDistrictId();
                String location = job.getLocation();
                // Get the emails map from the job
                String emailGroups = emailGroupName.toString();


                boolean flag = true;

                if (Objects.nonNull(organizationId) && Objects.nonNull(fleetName)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.kEY_FLEET_NAME).is(fleetName).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isFleetUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isFleetUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.FLEET_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(operatorName)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("operator").is(operatorName).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isOperatorUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isOperatorUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.OPERATOR_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(padName)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("pad").is(padName).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isPadUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isPadUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.PAD_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(location)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("location").is(location).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isLocationUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isLocationUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.LOCATION_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(districtId)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("districtId").is(districtId).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isDistrictUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isDistrictUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.DISTRICT_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(wellId)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("wells._id").is(wellId).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isWellUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isWellUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.WELL_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                for (Vendor vendor : vendors) {
                    vendorId = vendor.getId();
                    if (Objects.nonNull(organizationId) && Objects.nonNull(vendorId)) {
                        Query queryForData = new Query();
                        queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("vendors._id").is(vendorId).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                        List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                        boolean isVendorUsedInAnotherJob = !checkJobs.isEmpty();
                        if (isVendorUsedInAnotherJob) {
                            flag = false;
                            Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.VENDORS_USED_IN_ANOTHER_JOB).build();
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                        }
                    }

                }
                if (flag) {
                    //Delete Fleet
                    deleteFleet(organizationId, fleetName);
                    //Delete Operator
                    deleteOperator(organizationId, operatorName);
                    //Delete Pad
                    deletePad(organizationId, padName);
                    //Delete Location
                    deleteLocation(organizationId, location);
                    //Delete District
                    deleteDistrict(organizationId, districtId);
                    //Delete Well
                    deleteWell(organizationId, wellId);
                    //Delete Emails
                    deleteEmailGroup(organizationId, emailGroups);
                    //delete OnSiteEquipment
                    deleteOnSiteEquipment(organizationId, onSiteEquipment);

                    //Delete Vendors
                    for (Vendor vendor : vendors) {
                        vendorId = vendor.getId();
                        deleteVendor(organizationId, vendorId);
                    }
                    //Delete job
                    deleteJob(organizationId, (String) jobNumberValue);

                    return ResponseEntity.ok().build();
                }
            }
            Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(ErrorConstants.JOB_NOT_FOUND).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        return ResponseEntity.badRequest().body(Constants.JOB_NOT_FOUND_WITH_JOB_NUMBER_AND_ORGANIZATION);
    }


    //Remove Job
    public void deleteJob(String organizationId, String jobName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(jobName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_JOB_NUMBER).is(jobName));

            List<Job> jobs = mongoTemplate.find(query, Job.class);
            if (!jobs.isEmpty()) {
                Job job = jobs.get(0);
                // Delete the job
                mongoTemplate.remove(job);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if job is not present
        ResponseEntity.notFound().build();
    }

    //Remove OnSiteEquipment
    public void deleteOnSiteEquipment(String organizationId, String onSiteEquipmentName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(onSiteEquipmentName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(onSiteEquipmentName));

            List<OnSiteEquipment> onSiteEquipments = mongoTemplate.find(query, OnSiteEquipment.class);
            if (!onSiteEquipments.isEmpty()) {
                OnSiteEquipment onSiteEquipment = onSiteEquipments.get(0);
                // Delete the job
                mongoTemplate.remove(onSiteEquipment);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if OnSiteEquipment is not present
        ResponseEntity.notFound().build();
    }

    //Remove Fleet
    public void deleteFleet(String organizationId, String fleetName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(fleetName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(fleetName));
            List<Fleet> fleets = mongoTemplate.find(query, Fleet.class);

            if (!fleets.isEmpty()) {
                Fleet fleet = fleets.get(0);
                // Delete the fleet
                mongoTemplate.remove(fleet);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if fleet is not present
        ResponseEntity.notFound().build();
    }

    //Remove Operator(check)
    public void deleteOperator(String organizationId, String operatorName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(operatorName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(operatorName));

            List<Operator> operators = mongoTemplate.find(query, Operator.class);
            if (!operators.isEmpty()) {
                Operator operator = operators.get(0);
                // Delete the operator with company
                if(!StringUtils.isEmpty(operator.getCompanyId())) {
                    Query companyQuery = new Query();
                    companyQuery.addCriteria(Criteria.where("_id").is(new ObjectId(operator.getCompanyId())));
                    Optional<Company> companyOpt =mongoTemplate.find(companyQuery, Company.class).stream().findFirst();
                    if(companyOpt.isPresent()) {
                        String companyId = companyOpt.get().getCompanyId();
                        Query proposalQuery = new Query();
                        proposalQuery.addCriteria(Criteria.where("companyId").is(companyId).and(ORGANIZATION_ID).is(organizationId));
                        if(!mongoTemplate.exists(proposalQuery,"proposal-v2")) {
                            this.mongoTemplate.remove(proposalQuery, "company-details");
                            this.mongoTemplate.remove(companyQuery, Company.class);
                        }
                        this.mongoTemplate.remove(operator);
                    }
                }else{
                    this.mongoTemplate.remove(operator);
                }
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if operator is not present
        ResponseEntity.notFound().build();
    }

    //Remove Pad(check)
    public void deletePad(String organizationId, String padName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(padName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(padName));

            List<Pad> pads = mongoTemplate.find(query, Pad.class);
            if (!pads.isEmpty()) {
                Pad pad = pads.get(0);
                // Delete the pad
                mongoTemplate.remove(pad);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if pad is not present
        ResponseEntity.notFound().build();
    }

    //Remove Location
    public void deleteLocation(String organizationId, String location) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(location)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(location));

            List<Location> locations = mongoTemplate.find(query, Location.class);
            if (!locations.isEmpty()) {
                Location locati = locations.get(0);
                // Delete the location
                mongoTemplate.remove(locati);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if location is not present
        ResponseEntity.notFound().build();
    }

    //Remove District
    public void deleteDistrict(String organizationId, String districtId) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(districtId)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_ID).is(new ObjectId(districtId)));
            List<District> districts = mongoTemplate.find(query, District.class);
            if (districts.isEmpty()) {
                // If the first query returns no results, execute the second query
                Query queryForData = new Query();
                queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_ID).is(districtId));
                districts = mongoTemplate.find(queryForData, District.class);
            }
            if (!districts.isEmpty()) {
                District district = districts.get(0);
                // Delete the district
                mongoTemplate.remove(district);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if district is not present
        ResponseEntity.notFound().build();
    }

    //Remove well
    public void deleteWell(String organizationId, String wellId) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(wellId)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_ID).is(new ObjectId(wellId)));
            List<Well> wells = mongoTemplate.find(query, Well.class);
            if (wells.isEmpty()) {
                Query queryForData = new Query();
                queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_ID).is(wellId));
                wells = mongoTemplate.find(queryForData, Well.class);
            }
            if (!wells.isEmpty()) {
                Well well = wells.get(0);
                // Delete the well
                mongoTemplate.remove(well);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if well is not present
        ResponseEntity.notFound().build();
    }


    //Remove Vendor
    public void deleteVendor(String organizationId, String vendorId) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(vendorId)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_ID).is(new ObjectId(vendorId)));

            List<Vendor> vendors = mongoTemplate.find(query, Vendor.class);
            if (!vendors.isEmpty()) {
                Vendor vendor = vendors.get(0);
                // Delete the Vendor
                mongoTemplate.remove(vendor);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if Vendor is not present
        ResponseEntity.notFound().build();

    }

    //Remove emailGroups
    public void deleteEmailGroup(String organizationId, String emailGroupName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(emailGroupName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(emailGroupName));

            List<EmailGroup> emailGroups = mongoTemplate.find(query, EmailGroup.class);
            if (!emailGroups.isEmpty()) {
                EmailGroup emailGroup = emailGroups.get(0);
                // Delete the emailGroup
                mongoTemplate.remove(emailGroup);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if Email Group is not present
        ResponseEntity.notFound().build();
    }

    //Remove Email
    public void deleteEmails(String organizationId, String emailName) {
        if (Objects.nonNull(organizationId) && Objects.nonNull(emailName)) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(emailName));
            List<Email> emails = mongoTemplate.find(query, Email.class);
            if (!emails.isEmpty()) {
                Email email = emails.get(0);
                //delete the email
                mongoTemplate.remove(email);
                ResponseEntity.ok().build();
                return;
            }
        }
        // Return not found response if Email Group is not present
        ResponseEntity.notFound().build();
    }


    public ResponseEntity deleteWholeData(String fileName, String organizationId, HttpServletRequest request) throws Exception {
        Object jobNumberValue = null;
        Query query = new Query();
        try {
            MultipartFile files = loadAsFileSystemResource(getOrganizationId(request), fileName);
            Map<String, Object> data = new HashMap<>();
            if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                data.putAll(processExcelFile(files));
            } else if (fileName.endsWith(".csv")) {
                data.putAll(processCsvFile(files));
            }

            if (!data.isEmpty()) {
                jobNumberValue = data.get(Constants.JOB_NUMBER);
                if (jobNumberValue != null) {
                    String jobNumberString = jobNumberValue.toString();
                    if (isNumeric(jobNumberString)) {
                        jobNumberValue = Integer.parseInt(jobNumberString);
                    }
                }
            }


            query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_JOB_NUMBER).is(jobNumberValue));


            List<Job> jobs = mongoTemplate.find(query, Job.class);
            String jobId;

            if (!jobs.isEmpty()) {
                Job job = jobs.get(0);
                jobId = job.getId();
                String fleet = job.getFleet();
                String operatorName = job.getOperator();
                String fleetName = job.getFleet();
                String padName = job.getPad();
                String wellName = job.getWells().get(0).getName();
                String wellId = job.getWells().get(0).getId();
                List<Vendor> vendors = job.getVendors();
                String vendorId = null;
                String districtId = job.getDistrictId();
                String location = job.getLocation();


                boolean flag = true;

                if (Objects.nonNull(organizationId) && Objects.nonNull(fleetName)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.kEY_FLEET_NAME).is(fleetName).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isFleetUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isFleetUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.FLEET_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(operatorName)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("operator").is(operatorName).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isOperatorUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isOperatorUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.OPERATOR_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(padName)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("pad").is(padName).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isPadUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isPadUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.PAD_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(location)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("location").is(location).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isLocationUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isLocationUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.LOCATION_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(districtId)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("districtId").is(districtId).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isDistrictUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isDistrictUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.DISTRICT_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                if (Objects.nonNull(organizationId) && Objects.nonNull(wellId)) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("wells._id").is(wellId).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                    List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                    boolean isWellUsedInAnotherJob = !checkJobs.isEmpty();
                    if (isWellUsedInAnotherJob) {
                        flag = false;
                        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.WELL_USED_IN_ANOTHER_JOB).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                }
                for (Vendor vendor : vendors) {
                    vendorId = vendor.getId();
                    if (Objects.nonNull(organizationId) && Objects.nonNull(vendorId)) {
                        Query queryForData = new Query();
                        queryForData.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("vendors._id").is(vendorId).and(Constants.KEY_JOB_NUMBER).ne(jobNumberValue));

                        List<Job> checkJobs = mongoTemplate.find(queryForData, Job.class);

                        boolean isVendorUsedInAnotherJob = !checkJobs.isEmpty();
                        if (isVendorUsedInAnotherJob) {
                            flag = false;
                            Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(Constants.VENDORS_USED_IN_ANOTHER_JOB).build();
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                        }
                    }

                }
                if (flag) {
                    Query queryForData = new Query();
                    queryForData.addCriteria(Criteria.where("jobId").is(jobId).and(Constants.ORGANIZATION_ID).is(organizationId));
                    List<CheckList> checkList = mongoTemplate.find(queryForData, CheckList.class);
                    if (!checkList.isEmpty()) {
                        mongoTemplate.remove(checkList);
                    }
                    List<ActivityLogEntry> activityLogEntry = mongoTemplate.find(queryForData, ActivityLogEntry.class);
                    if (!activityLogEntry.isEmpty()) {
                        for (ActivityLogEntry activityLogEntry1 : activityLogEntry) {
                            mongoTemplate.remove(activityLogEntry1);

                        }
                    }

                    //Delete Iron Failure and Safety report
                    deleteSafetyReportsByJobId(jobId, request);

                    Query queryForProppant = new Query();
                    queryForProppant.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(data.get(Constants.PROPANT_NAME).toString()));
                    List<PriceBookComponents> proppantData = mongoTemplate.find(queryForProppant, PriceBookComponents.class);
                    if (!proppantData.isEmpty()) {
                        for (PriceBookComponents proppant : proppantData) {
                            mongoTemplate.remove(proppant);
                        }
                    }

                    Query queryForChemical = new Query();
                    queryForChemical.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and("name").is(data.get(Constants.CHEMICAL_NAME).toString()));
                    List<PriceBookComponents> chemicalData = mongoTemplate.find(queryForChemical, PriceBookComponents.class);
                    if (!chemicalData.isEmpty()) {
                        for (PriceBookComponents chemical : chemicalData) {
                            mongoTemplate.remove(chemical);
                        }
                    }

                    List<CalculateByStraps> calculateByStraps = mongoTemplate.find(queryForData, CalculateByStraps.class);
                    if (!calculateByStraps.isEmpty()) {
                        for (CalculateByStraps calculateByStraps1 : calculateByStraps) {
                            mongoTemplate.remove(calculateByStraps1);
                        }
                    }
                    List<Casing> casing = mongoTemplate.find(queryForData, Casing.class);
                    if (!casing.isEmpty()) {
                        for (Casing casing1 : casing) {
                            mongoTemplate.remove(casing1);
                        }
                    }
                    List<ChangeLogEntry> changeLogEntry = mongoTemplate.find(queryForData, ChangeLogEntry.class);
                    if (!changeLogEntry.isEmpty()) {
                        for (ChangeLogEntry changeLogEntry1 : changeLogEntry) {
                            mongoTemplate.remove(changeLogEntry1);
                        }
                    }
                    List<MaterialNeeded> materialNeeded = mongoTemplate.find(queryForData, MaterialNeeded.class);
                    if (!materialNeeded.isEmpty()) {
                        for (MaterialNeeded materialNeeded1 : materialNeeded) {
                            mongoTemplate.remove(materialNeeded1);
                        }
                    }
                    List<ChemicalStage> chemicalStage = mongoTemplate.find(queryForData, ChemicalStage.class);
                    if (!chemicalStage.isEmpty()) {
                        for (ChemicalStage chemicalStage1 : chemicalStage) {
                            mongoTemplate.remove(chemicalStage1);
                        }
                    }
                    List<ConsumableDeliveryEntry> consumableDeliveryEntry = mongoTemplate.find(queryForData, ConsumableDeliveryEntry.class);
                    if (!consumableDeliveryEntry.isEmpty()) {
                        for (ConsumableDeliveryEntry consumableDeliveryEntry1 : consumableDeliveryEntry) {
                            mongoTemplate.remove(consumableDeliveryEntry1);
                        }
                    }
                    List<Cost> cost = mongoTemplate.find(queryForData, Cost.class);
                    if (!cost.isEmpty()) {
                        for (Cost cost1 : cost) {
                            mongoTemplate.remove(cost1);
                        }
                    }
                    List<DailyJobRecord> dailyJobRecord = mongoTemplate.find(queryForData, DailyJobRecord.class);
                    if (!dailyJobRecord.isEmpty()) {
                        for (DailyJobRecord dailyJobRecord1 : dailyJobRecord) {
                            mongoTemplate.remove(dailyJobRecord1);
                        }
                    }
                    List<ProppantDeliveryEntry> proppantDeliveryEntry = mongoTemplate.find(queryForData, ProppantDeliveryEntry.class);
                    if (!proppantDeliveryEntry.isEmpty()) {
                        for (ProppantDeliveryEntry proppantDeliveryEntry1 : proppantDeliveryEntry) {
                            mongoTemplate.remove(proppantDeliveryEntry1);
                        }
                    }
                    List<EndStageEmail> endStageEmail = mongoTemplate.find(queryForData, EndStageEmail.class);
                    if (!endStageEmail.isEmpty()) {
                        for (EndStageEmail endStageEmail1 : endStageEmail) {
                            mongoTemplate.remove(endStageEmail1);
                        }
                    }
                    List<FieldTicket> fieldTicket = mongoTemplate.find(queryForData, FieldTicket.class);
                    if (!fieldTicket.isEmpty()) {
                        for (FieldTicket fieldTicket1 : fieldTicket) {
                            mongoTemplate.remove(fieldTicket1);
                        }
                    }
                    List<IronFailureReport> ironFailureReport = mongoTemplate.find(queryForData, IronFailureReport.class);
                    if (!ironFailureReport.isEmpty()) {
                        for (IronFailureReport ironFailureReport1 : ironFailureReport) {
                            mongoTemplate.remove(ironFailureReport1);
                        }
                    }
                    List<MaintenanceEntry> maintenanceEntry = mongoTemplate.find(queryForData, MaintenanceEntry.class);
                    if (!maintenanceEntry.isEmpty()) {
                        for (MaintenanceEntry maintenanceEntry1 : maintenanceEntry) {
                            mongoTemplate.remove(maintenanceEntry1);
                        }
                    }
                    List<MigrationStatusEntry> migrationStatusEntry = mongoTemplate.find(queryForData, MigrationStatusEntry.class);
                    if (!migrationStatusEntry.isEmpty()) {
                        for (MigrationStatusEntry migrationStatusEntry1 : migrationStatusEntry) {
                            mongoTemplate.remove(migrationStatusEntry1);
                        }
                    }
                    List<OperationsOverview> operationsOverview = mongoTemplate.find(queryForData, OperationsOverview.class);
                    if (!operationsOverview.isEmpty()) {
                        for (OperationsOverview operationsOverview1 : operationsOverview) {
                            mongoTemplate.remove(operationsOverview1);
                        }
                    }
                    List<PendingIronFailure> pendingIronFailure = mongoTemplate.find(queryForData, PendingIronFailure.class);
                    if (!pendingIronFailure.isEmpty()) {
                        for (PendingIronFailure pendingIronFailure1 : pendingIronFailure) {
                            mongoTemplate.remove(pendingIronFailure1);
                        }
                    }
                    List<PendingMaintenanceEntry> pendingMaintenanceEntry = mongoTemplate.find(queryForData, PendingMaintenanceEntry.class);
                    if (!pendingMaintenanceEntry.isEmpty()) {
                        for (PendingMaintenanceEntry pendingMaintenanceEntry1 : pendingMaintenanceEntry) {
                            mongoTemplate.remove(pendingMaintenanceEntry1);
                        }
                    }
                    List<Proposal> proposal = mongoTemplate.find(queryForData, Proposal.class);
                    if (!proposal.isEmpty()) {
                        for (Proposal proposal1 : proposal) {
                            mongoTemplate.remove(proposal1);
                        }
                    }
                    List<ProposalJobMapping> proposalJobMapping = mongoTemplate.find(queryForData, ProposalJobMapping.class);
                    if (!proposalJobMapping.isEmpty()) {
                        for (ProposalJobMapping proposalJobMapping1 : proposalJobMapping) {
                            mongoTemplate.remove(proposalJobMapping1);
                        }
                    }
                    List<PumpIssue> pumpIssue = mongoTemplate.find(queryForData, PumpIssue.class);
                    if (!pumpIssue.isEmpty()) {
                        for (PumpIssue pumpIssue1 : pumpIssue) {
                            mongoTemplate.remove(pumpIssue1);
                        }
                    }
                    List<PumpScheduleJobCfg> pumpScheduleJobCfg = mongoTemplate.find(queryForData, PumpScheduleJobCfg.class);
                    if (!pumpScheduleJobCfg.isEmpty()) {
                        for (PumpScheduleJobCfg pumpScheduleJobCfg1 : pumpScheduleJobCfg) {
                            mongoTemplate.remove(pumpScheduleJobCfg1);
                        }
                    }
                    List<PumpSchedule> pumpSchedule = mongoTemplate.find(queryForData, PumpSchedule.class);
                    if (!pumpSchedule.isEmpty()) {
                        for (PumpSchedule pumpSchedule1 : pumpSchedule) {
                            mongoTemplate.remove(pumpSchedule1);
                        }
                    }
                    List<SchedulerEmailDetail> schedulerEmailDetail = mongoTemplate.find(queryForData, SchedulerEmailDetail.class);
                    if (!schedulerEmailDetail.isEmpty()) {
                        for (SchedulerEmailDetail schedulerEmailDetail1 : schedulerEmailDetail) {
                            mongoTemplate.remove(schedulerEmailDetail1);
                        }
                    }
                    List<WaterAnalysisEntry> waterAnalysisEntry = mongoTemplate.find(queryForData, WaterAnalysisEntry.class);
                    if (!waterAnalysisEntry.isEmpty()) {
                        for (WaterAnalysisEntry waterAnalysisEntry1 : waterAnalysisEntry) {
                            mongoTemplate.remove(waterAnalysisEntry1);
                        }
                    }
                    List<WellInfo> wellInfo = mongoTemplate.find(queryForData, WellInfo.class);
                    if (!wellInfo.isEmpty()) {
                        for (WellInfo wellInfo1 : wellInfo) {
                            mongoTemplate.remove(wellInfo1);
                        }
                    }

                }
                Map<String, Object> response = new HashMap<>();
                response.put("message", "All the data for this job " + jobNumberValue + " is successfully deleted.");
                return ResponseEntity.ok(response);
            }
            Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(ErrorConstants.JOB_NOT_FOUND).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Error error = Error.builder().errorCode(Constants.DEMO_DATA_NOT_FOUND).errorMessage(Constants.ERROR_WHILE_GETTING_DEMO_DATA).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MultipartFile loadAsFileSystemResource(String organizationId, String fileName) throws IOException {

        String blobUri = uploadConfig.getAzureBlobUrl();
        String sasToken = uploadConfig.getSasToken();
        String containerName = uploadConfig.getContainerName();
        BlobClient blobClient = null;
        try {
            blobClient = new BlobClientBuilder().endpoint(blobUri).sasToken(sasToken).containerName(containerName).blobName(organizationId + "/" + fileName).buildClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream inputStream = blobClient.openInputStream();

        return convertInputStreamToMultipartFile(inputStream, fileName);
    }


    public MultipartFile convertInputStreamToMultipartFile(InputStream inputStream, String fileName) throws IOException {
        // Use the custom MultipartFile implementation
        return new CustomMultipartFile(inputStream, "file", fileName, "application/octet-stream");
    }

    public void deleteFile(String folderId, String fileName) {

        String blobUri = uploadConfig.getAzureBlobUrl();
        BlobClient blobClient = new BlobClientBuilder().endpoint(blobUri).sasToken(uploadConfig.getSasToken()).containerName(uploadConfig.getContainerName()).blobName(folderId + "/" + fileName).buildClient();
        try {
            blobClient.delete();
        } catch (Exception e) {
        }

    }

    //for New Api
    public ResponseEntity deleteJobData(HttpServletRequest request, String jobNum) throws Exception {
        String organizationId = getOrganizationId(request);
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.ORGANIZATION_ID).is(organizationId).and(Constants.KEY_JOB_NUMBER).is(jobNum));
        List<Job> jobs = mongoTemplate.find(query, Job.class);
        if (!jobs.isEmpty()) {
            String jobId = jobs.get(0).getId();

            //Delete Iron Failure and Safety report
            deleteSafetyReportsByJobId(jobId, request);

            Query queryForData = new Query();
            queryForData.addCriteria(Criteria.where("jobId").is(jobId).and(ORGANIZATION_ID).is(organizationId));
            List<CheckList> checkList = mongoTemplate.find(queryForData, CheckList.class);
            if (!checkList.isEmpty()) {
                mongoTemplate.remove(checkList);
            }
            List<ActivityLogEntry> activityLogEntry = mongoTemplate.find(queryForData, ActivityLogEntry.class);
            if (!activityLogEntry.isEmpty()) {
                for (ActivityLogEntry activityLogEntry1 : activityLogEntry) {
                    mongoTemplate.remove(activityLogEntry1);

                }
            }
            List<CalculateByStraps> calculateByStraps = mongoTemplate.find(queryForData, CalculateByStraps.class);
            if (!calculateByStraps.isEmpty()) {
                for (CalculateByStraps calculateByStraps1 : calculateByStraps) {
                    mongoTemplate.remove(calculateByStraps1);
                }
            }
            List<Casing> casing = mongoTemplate.find(queryForData, Casing.class);
            if (!casing.isEmpty()) {
                for (Casing casing1 : casing) {
                    mongoTemplate.remove(casing1);
                }
            }
            List<ChangeLogEntry> changeLogEntry = mongoTemplate.find(queryForData, ChangeLogEntry.class);
            if (!changeLogEntry.isEmpty()) {
                for (ChangeLogEntry changeLogEntry1 : changeLogEntry) {
                    mongoTemplate.remove(changeLogEntry1);
                }
            }

            List<MaterialNeeded> materialNeeded = mongoTemplate.find(queryForData, MaterialNeeded.class);
            if (!materialNeeded.isEmpty()) {
                for (MaterialNeeded materialNeeded1 : materialNeeded) {
                    mongoTemplate.remove(materialNeeded1);
                }
            }
            List<ChemicalStage> chemicalStage = mongoTemplate.find(queryForData, ChemicalStage.class);
            if (!chemicalStage.isEmpty()) {
                for (ChemicalStage chemicalStage1 : chemicalStage) {
                    mongoTemplate.remove(chemicalStage1);
                }
            }
            List<ConsumableDeliveryEntry> consumableDeliveryEntry = mongoTemplate.find(queryForData, ConsumableDeliveryEntry.class);
            if (!consumableDeliveryEntry.isEmpty()) {
                for (ConsumableDeliveryEntry consumableDeliveryEntry1 : consumableDeliveryEntry) {
                    mongoTemplate.remove(consumableDeliveryEntry1);
                }
            }
            List<Cost> cost = mongoTemplate.find(queryForData, Cost.class);
            if (!cost.isEmpty()) {
                for (Cost cost1 : cost) {
                    mongoTemplate.remove(cost1);
                }
            }
            List<DailyJobRecord> dailyJobRecord = mongoTemplate.find(queryForData, DailyJobRecord.class);
            if (!dailyJobRecord.isEmpty()) {
                for (DailyJobRecord dailyJobRecord1 : dailyJobRecord) {
                    mongoTemplate.remove(dailyJobRecord1);
                }
            }
            List<ProppantDeliveryEntry> proppantDeliveryEntry = mongoTemplate.find(queryForData, ProppantDeliveryEntry.class);
            if (!proppantDeliveryEntry.isEmpty()) {
                for (ProppantDeliveryEntry proppantDeliveryEntry1 : proppantDeliveryEntry) {
                    mongoTemplate.remove(proppantDeliveryEntry1);
                }
            }

            List<EndStageEmail> endStageEmail = mongoTemplate.find(queryForData, EndStageEmail.class);
            if (!endStageEmail.isEmpty()) {
                for (EndStageEmail endStageEmail1 : endStageEmail) {
                    mongoTemplate.remove(endStageEmail1);
                }
            }
            List<FieldTicket> fieldTicket = mongoTemplate.find(queryForData, FieldTicket.class);
            if (!fieldTicket.isEmpty()) {
                for (FieldTicket fieldTicket1 : fieldTicket) {
                    mongoTemplate.remove(fieldTicket1);
                }
            }
            List<IronFailureReport> ironFailureReport = mongoTemplate.find(queryForData, IronFailureReport.class);
            if (!ironFailureReport.isEmpty()) {
                for (IronFailureReport ironFailureReport1 : ironFailureReport) {
                    mongoTemplate.remove(ironFailureReport1);
                }
            }
            List<MaintenanceEntry> maintenanceEntry = mongoTemplate.find(queryForData, MaintenanceEntry.class);
            if (!maintenanceEntry.isEmpty()) {
                for (MaintenanceEntry maintenanceEntry1 : maintenanceEntry) {
                    mongoTemplate.remove(maintenanceEntry1);
                }
            }
            List<MigrationStatusEntry> migrationStatusEntry = mongoTemplate.find(queryForData, MigrationStatusEntry.class);
            if (!migrationStatusEntry.isEmpty()) {
                for (MigrationStatusEntry migrationStatusEntry1 : migrationStatusEntry) {
                    mongoTemplate.remove(migrationStatusEntry1);
                }
            }
            List<OperationsOverview> operationsOverview = mongoTemplate.find(queryForData, OperationsOverview.class);
            if (!operationsOverview.isEmpty()) {
                for (OperationsOverview operationsOverview1 : operationsOverview) {
                    mongoTemplate.remove(operationsOverview1);
                }
            }
            List<PendingIronFailure> pendingIronFailure = mongoTemplate.find(queryForData, PendingIronFailure.class);
            if (!pendingIronFailure.isEmpty()) {
                for (PendingIronFailure pendingIronFailure1 : pendingIronFailure) {
                    mongoTemplate.remove(pendingIronFailure1);
                }
            }
            List<PendingMaintenanceEntry> pendingMaintenanceEntry = mongoTemplate.find(queryForData, PendingMaintenanceEntry.class);
            if (!pendingMaintenanceEntry.isEmpty()) {
                for (PendingMaintenanceEntry pendingMaintenanceEntry1 : pendingMaintenanceEntry) {
                    mongoTemplate.remove(pendingMaintenanceEntry1);
                }
            }
            List<Proposal> proposal = mongoTemplate.find(queryForData, Proposal.class);
            if (!proposal.isEmpty()) {
                for (Proposal proposal1 : proposal) {
                    mongoTemplate.remove(proposal1);
                }
            }
            List<ProposalJobMapping> proposalJobMapping = mongoTemplate.find(queryForData, ProposalJobMapping.class);
            if (!proposalJobMapping.isEmpty()) {
                for (ProposalJobMapping proposalJobMapping1 : proposalJobMapping) {
                    mongoTemplate.remove(proposalJobMapping1);
                }
            }
            List<PumpIssue> pumpIssue = mongoTemplate.find(queryForData, PumpIssue.class);
            if (!pumpIssue.isEmpty()) {
                for (PumpIssue pumpIssue1 : pumpIssue) {
                    mongoTemplate.remove(pumpIssue1);
                }
            }
            List<PumpScheduleJobCfg> pumpScheduleJobCfg = mongoTemplate.find(queryForData, PumpScheduleJobCfg.class);
            if (!pumpScheduleJobCfg.isEmpty()) {
                for (PumpScheduleJobCfg pumpScheduleJobCfg1 : pumpScheduleJobCfg) {
                    mongoTemplate.remove(pumpScheduleJobCfg1);
                }
            }
            List<PumpSchedule> pumpSchedule = mongoTemplate.find(queryForData, PumpSchedule.class);
            if (!pumpSchedule.isEmpty()) {
                for (PumpSchedule pumpSchedule1 : pumpSchedule) {
                    mongoTemplate.remove(pumpSchedule1);
                }
            }
            List<SchedulerEmailDetail> schedulerEmailDetail = mongoTemplate.find(queryForData, SchedulerEmailDetail.class);
            if (!schedulerEmailDetail.isEmpty()) {
                for (SchedulerEmailDetail schedulerEmailDetail1 : schedulerEmailDetail) {
                    mongoTemplate.remove(schedulerEmailDetail1);
                }
            }
            List<WaterAnalysisEntry> waterAnalysisEntry = mongoTemplate.find(queryForData, WaterAnalysisEntry.class);
            if (!waterAnalysisEntry.isEmpty()) {
                for (WaterAnalysisEntry waterAnalysisEntry1 : waterAnalysisEntry) {
                    mongoTemplate.remove(waterAnalysisEntry1);
                }
            }
            List<WellInfo> wellInfo = mongoTemplate.find(queryForData, WellInfo.class);
            if (!wellInfo.isEmpty()) {
                for (WellInfo wellInfo1 : wellInfo) {
                    mongoTemplate.remove(wellInfo1);
                }
            }

            deleteJob(organizationId, (String) jobNum);
            Map<String, Object> response = new HashMap<>();

            response.put("message", "All the data for this job " + jobNum + " is successfully deleted.");
            return ResponseEntity.ok(response);
        }
        Error error = Error.builder().errorCode(HttpStatus.BAD_REQUEST.getReasonPhrase()).errorMessage(ErrorConstants.JOB_NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);


    }

    public void deleteSafetyReportsByJobId(String jobId, HttpServletRequest request) {
        Map<String, String> headerValueMap = ControllerUtil.getHeadersInfo(request);
        String url = webClientConfig.getBaseUrl();
        String authToken = headerValueMap.get("authorization");

        try {
            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
            WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).baseUrl(url)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, authToken).build();

            String deleteServiceUrl = WebClientUtils.DELETE_IRON_FAILURE_SAFETY_REPORT;
            String finalUrl = deleteServiceUrl + "?jobId=" + jobId;

            logger.info("finalUrl : {} ", finalUrl);

            Mono<Void> responseMono = webClient.delete().uri(finalUrl).accept(MediaType.APPLICATION_JSON).retrieve().onStatus(HttpStatusCode::isError,
                    response -> response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new RuntimeException("Error deleting reports: " + body)))).bodyToMono(Void.class);
            responseMono.block();
        } catch (Exception e) {
            logger.error("Error deleting reports: {}", e.getMessage());
            // Handle the error appropriately
        }
    }
}

