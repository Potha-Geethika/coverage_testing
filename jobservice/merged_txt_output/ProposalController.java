// ===== Imported from: com.carbo.job.storage.StorageService =====
package com.carbo.job.storage;

import com.carbo.job.exception.NotImplementedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(String folder, MultipartFile file) throws IOException;

    Stream<Path> loadAll() throws NotImplementedException;

    Path load(String fileName) throws NotImplementedException;

    Resource loadAsResource(String folder, String fileName) throws FileNotFoundException, NotImplementedException;

    FileSystemResource loadAsFileSystemResource(String folder, String fileName) throws FileNotFoundException;

    String loadFileAsString(String jobId) throws IOException;

    String loadFileAsBase64String(String jobId) throws IOException;

    void deleteAll() throws NotImplementedException;
}

// Unresolved import (framework/JDK): jakarta.servlet.http.HttpServlet
// Unresolved import (framework/JDK): org.slf4j.LoggerFactory
// Unresolved import (framework/JDK): java.util.stream.Collectors
// ===== Imported from: com.carbo.ws.model.ProppantStage =====
package com.carbo.ws.model;

import com.carbo.proppantstage.model.ProppantContainer;
import com.carbo.proppantstage.model.Silo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import java.util.*;

@Document(collection = "proppant-stages")
public class ProppantStage {
    @Id
    private String id;

    @Field("jobId")
    @NotNull
    @Indexed(unique = false)
    private String jobId;

    @Field("wellId")
    @NotNull
    @Indexed(unique = false)
    private String wellId;

    @Field("date")
    private Date date;

    @Field("well")
    private String well;

    @Field("stage")
    @NotNull
    private Float stage;

    @Field("silos")
    private Map<String, Silo> silos = new HashMap<>();

    @Field("runOrders")
    private List<ProppantContainer> runOrders = new ArrayList<>();

    @Field("blender")
    private String blender;

    @Field("diverter")
    private String diverter;

    @Field("diverterAmount")
    private Float diverterAmount;

    @Field("currentInSilos")
    private Map<String, Float> currentInSilos = new HashMap<>();

    @Field("organizationId")
    @NotNull
    @Indexed(unique = false)
    private String organizationId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("isMigrated")
    private Boolean isMigrated;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public Float getStage() {
        return stage;
    }

    public void setStage(Float stage) {
        this.stage = stage;
    }

    public Map<String, Silo> getSilos() {
        return silos;
    }

    public void setSilos(Map<String, Silo> silos) {
        this.silos = silos;
    }

    public String getBlender() {
        return blender;
    }

    public void setBlender(String blender) {
        this.blender = blender;
    }

    public String getDiverter() {
        return diverter;
    }

    public void setDiverter(String diverter) {
        this.diverter = diverter;
    }

    public Float getDiverterAmount() {
        return diverterAmount;
    }

    public void setDiverterAmount(Float diverterAmount) {
        this.diverterAmount = diverterAmount;
    }

    public Map<String, Float> getCurrentInSilos() {
        return currentInSilos;
    }

    public void setCurrentInSilos(Map<String, Float> currentInSilos) {
        this.currentInSilos = currentInSilos;
    }

    public List<ProppantContainer> getRunOrders() {
        return runOrders;
    }

    public void setRunOrders(List<ProppantContainer> runOrders) {
        this.runOrders = runOrders;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Boolean getMigrated() {
        return isMigrated;
    }

    public void setMigrated(Boolean migrated) {
        isMigrated = migrated;
    }

    public List<ProppantContainer> getAllSubmittedContainer(Boolean isBox) {
        List<ProppantContainer> result = new ArrayList<>();
        if (isBox) {
            if (this.runOrders != null && !this.runOrders.isEmpty()) {
                result.addAll(this.runOrders);
            }
        }
        else {
            if (this.silos != null && !this.silos.isEmpty()) {
                result.addAll(this.silos.values());
            }
        }

        return result;
    }
}

// Unresolved import (framework/JDK): java.io.FileNotFoundException
// ===== Imported from: com.carbo.job.model.well.Wells =====
package com.carbo.job.model.well;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "wells")
public class Wells {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    private String name;

    @Field("api")
    @NotEmpty(message = "api can not be empty")
    @Size(max = 14, message = "api can not be more than 14 characters.")
    private String api;

    @Field("afeNumber")
    @NotEmpty(message = "afeNumber can not be empty")
    @Size(max = 20, message = "afeNumber can not be more than 20 characters.")
    private String afeNumber;

    @Field("longitude")
    private double longitude;

    @Field("latitude")
    private double latitude;

    @Field("totalStages")
    private int totalStages;

    @Field("operatorId")
    @NotEmpty(message = "pad ID can not be empty")
    @Size(max = 100, message = "pad ID can not be more than 100 characters.")
    private String operatorId;

    @Field("padId")
    @NotEmpty(message = "pad ID can not be empty")
    @Size(max = 100, message = "pad ID can not be more than 100 characters.")
    private String padId;

    @Field("fracproId")
    private int fracproId;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("ts")
    private Long ts;

    @Field("organizationId")
    private String organizationId;

    @Field("stageInfo")
    private List<StageInfo> stageInfo =  new ArrayList<>();

    public List<StageInfo> getStageInfo() {
        return stageInfo;
    }

    public void setStageInfo(List<StageInfo> stageInfo) {
        this.stageInfo = stageInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAfeNumber() {
        return afeNumber;
    }

    public void setAfeNumber(String afeNumber) {
        this.afeNumber = afeNumber;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getTotalStages() {
        return totalStages;
    }

    public void setTotalStages(int totalStages) {
        this.totalStages = totalStages;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public int getFracproId() {
        return fracproId;
    }

    public void setFracproId(int fracproId) {
        this.fracproId = fracproId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getPadId() {
        return padId;
    }

    public void setPadId(String padId) {
        this.padId = padId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getCreated() {
        return created;
    }

}
// Unresolved import (framework/JDK): java.io.IOException
// Unresolved import (framework/JDK): jakarta.servlet.http.HttpServletRequest
// ===== Imported from: com.carbo.job.model.proposal.Proposal =====
package com.carbo.job.model.proposal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "proposal")
@CompoundIndex(def = "{'_id': 1, 'job': 1}", name = "job_id_user_id_index", unique = true)
public class Proposal {
    @Id
    private String id;

    @Field("jobId")
    private String jobId;

    @Field("internalPropsal")
    private Integer internalPropsal;
    
    @Field("equipmentCharges")
    private List<FieldTicketLineItemProposal> equipmentCharges;

    @Field("otherCharges")
    private List<OtherCharges> otherCharges;

    @Field("discounts")
    private List<ChemicalDiscount> discounts;

    @Field("fileName")
    private String fileName;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field ("wellId")
    private String wellId;

    public Proposal(String jobId, Integer internalPropsal) {
        this.jobId = jobId;
        this.internalPropsal = internalPropsal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getInternalPropsal() {
        return internalPropsal;
    }

    public void setInternalPropsal(Integer internalPropsal) {
        this.internalPropsal = internalPropsal;
    }

    public List<FieldTicketLineItemProposal> getEquipmentCharges() {
        return equipmentCharges;
    }

    public void setEquipmentCharges(List<FieldTicketLineItemProposal> equipmentCharges) {
        this.equipmentCharges = equipmentCharges;
    }

    public List<OtherCharges> getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(List<OtherCharges> otherCharges) {
        this.otherCharges = otherCharges;
    }

    public List<ChemicalDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<ChemicalDiscount> discounts) {
        this.discounts = discounts;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.*
// Unresolved import (framework/JDK): org.springframework.transaction.annotation.Propagation
// Unresolved import (framework/JDK): com.carbo.job.model.*
// Unresolved import (framework/JDK): org.springframework.web.multipart.MultipartFile
// ===== Imported from: com.carbo.job.model.proposal.ProposalData =====
package com.carbo.job.model.proposal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.carbo.job.model.Chemical;
import com.carbo.job.model.Proppant;

public class ProposalData {
    private int internal;
    private int totalStages;
    private double totalCleanVolume;
    private String proposalId;
    private String organizationId;
    private List<Chemical> additives;
    private List<Proppant> proppants;
    private Map<String, BigInteger> fluids;
    private List<PumpScheduleStage> pumpSchedules;
    private Map<String, List<Chemical>> fluidTypeChemicalMap;
    private List<ChemicalDiscount> discounts;
    private List<FieldTicketLineItemProposal> equipmentCharges;
    private List<OtherCharges> otherCharges;

    public ProposalData() {
    }

    public ProposalData(List<Proposal> proposal, String organizationId, int totalStages, double totalCleanVolume, List<Chemical> additives, List<Proppant> proppants) {
        this.organizationId = organizationId;
        this.internal = proposal !=null ? proposal.get(0).getInternalPropsal(): 0;
        this.totalStages = totalStages;
        this.totalCleanVolume = totalCleanVolume;
        this.additives = additives;
        this.proppants = proppants;
        this.equipmentCharges =  proposal!=null ? getEquipmentChargesList(proposal):null;
        this.otherCharges =  proposal!=null ? getOtherChargesList(proposal):null;
        this.discounts = proposal!=null ? proposal.iterator().next().getDiscounts():null;
        this.proposalId =  proposal!=null ? proposal.iterator().next().getId():null;
    }

    public ProposalData(Proposal proposal, String organizationId, int totalStages, double totalCleanVolume, List<Chemical> additives, List<Proppant> proppants) {
        this.organizationId = organizationId;
        this.internal = proposal !=null ? proposal.getInternalPropsal(): 0;
        this.totalStages = totalStages;
        this.totalCleanVolume = totalCleanVolume;
        this.additives = additives;
        this.proppants = proppants;
        this.equipmentCharges =  proposal!=null ? proposal.getEquipmentCharges():null;
        this.otherCharges =  proposal!=null ? proposal.getOtherCharges():null;
        this.discounts = proposal!=null ? proposal.getDiscounts():null;
        this.proposalId =  proposal!=null ? proposal.getId():null;
    }

    public int getTotalStages() {
        return totalStages;
    }
    public void setTotalStages(int totalStages) {
        this.totalStages = totalStages;
    }
    public String getProposalId() {
        return proposalId;
    }
    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }
    public double getTotalCleanVolume() {
        return totalCleanVolume;
    }
    public void setTotalCleanVolume(double totalCleanVolume) {
        this.totalCleanVolume = totalCleanVolume;
    }
    public List<Chemical> getAdditives() {
        return additives;
    }
    public void setAdditives(List<Chemical> additives) {
        this.additives = additives;
    }
    public List<Proppant> getProppants() {
        return proppants;
    }
    public void setProppants(List<Proppant> proppants) {
        this.proppants = proppants;
    }
    public List<FieldTicketLineItemProposal> getEquipmentCharges() {
        return equipmentCharges;
    }
    public void setEquipmentCharges(List<FieldTicketLineItemProposal> equipmentCharges) {
        this.equipmentCharges = equipmentCharges;
    }
    public List<OtherCharges> getOtherCharges() {
        return otherCharges;
    }
    public void setOtherCharges(List<OtherCharges> otherCharges) {
        this.otherCharges = otherCharges;
    }

    public int getInternal() {
        return internal;
    }

    public void setInternal(int internal) {
        this.internal = internal;
    }
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Map<String, BigInteger> getFluids() {
        return fluids;
    }

    public void setFluids(Map<String, BigInteger> fluids) {
        this.fluids = fluids;
    }

    public Map<String, List<Chemical>> getFluidTypeChemicalMap() {
        return fluidTypeChemicalMap;
    }

    public void setFluidTypeChemicalMap(Map<String, List<Chemical>> fluidTypeChemicalMap) {
        this.fluidTypeChemicalMap = fluidTypeChemicalMap;
    }

    public List<PumpScheduleStage> getPumpSchedules() {
        return pumpSchedules;
    }

    public void setPumpSchedules(List<PumpScheduleStage> pumpSchedules) {
        this.pumpSchedules = pumpSchedules;
    }
    
    @Override
    public String toString() {
        return "ProposalData [additives=" + additives + ", equipmentCharges=" + equipmentCharges + ", internal="
                + internal + ", otherCharges=" + otherCharges + ", proppants=" + proppants + ", totalCleanVolume="
                + totalCleanVolume + ", totalStages=" + totalStages + "]";
    }

    public List<ChemicalDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<ChemicalDiscount> discounts) {
        this.discounts = discounts;
    }

    public List<FieldTicketLineItemProposal> getEquipmentChargesList(List<Proposal> proposals){
        List<FieldTicketLineItemProposal> equipmentCharges = new ArrayList<>();
        for(int i=0; i<proposals.size(); i++){
            for(int j=0; j<proposals.get(i).getEquipmentCharges().size(); j++) {
                equipmentCharges.add(proposals.get(i).getEquipmentCharges().get(j));
            }
        }
        return equipmentCharges;
    }

    public List<OtherCharges> getOtherChargesList(List<Proposal> proposals){
        List<OtherCharges> otherCharges = new ArrayList<>();
        for(int i=0; i<proposals.size(); i++){
            for(int j=0; j<proposals.get(i).getOtherCharges().size(); j++) {
                otherCharges.add(proposals.get(i).getOtherCharges().get(j));
            }
        }
        return otherCharges;
    }
}

// Unresolved import (framework/JDK): java.util.Base64
// Unresolved import (framework/JDK): org.slf4j.Logger
// Unresolved import (framework/JDK): com.carbo.job.services.*
// Unresolved import (framework/JDK): org.springframework.http.HttpStatus
// Unresolved import (framework/JDK): org.springframework.web.server.ResponseStatusException
// Unresolved import (framework/JDK): org.springframework.beans.factory.annotation.Autowired
// Unresolved import (framework/JDK): java.util.ArrayList
// Unresolved import (framework/JDK): java.util.List
// Unresolved import (framework/JDK): java.util.Optional
// ===== Current file: src/main/java/com/carbo/job/controllers/ProposalController.java =====
package com.carbo.job.controllers;

import com.carbo.job.model.*;
import com.carbo.job.model.proposal.ProposalData;
import com.carbo.job.model.well.Wells;
import com.carbo.job.services.*;
import com.carbo.job.storage.StorageService;
import com.carbo.job.model.proposal.Proposal;
import com.carbo.ws.model.ProppantStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.carbo.job.utils.ControllerUtil.getOrganizationId;
import static com.carbo.job.utils.ProposalUtil.calculateProposal;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "v1/jobs/proposal")
public class ProposalController {

    private static final Logger logger = LoggerFactory.getLogger(ProposalController.class);

    private final JobService jobService;

    private final PumpScheduleService pumpScheduleService;
    private final PumpScheduleJobCfgService pumpScheduleJobCfgService;

    private final ProposalService proposalService;

    private final StorageService storageService;

    @Autowired
    public ProposalController(JobService jobService, PumpScheduleService pumpScheduleService, ProposalService proposalService, StorageService storageService, PumpScheduleJobCfgService pumpScheduleJobCfgService) {
        this.jobService = jobService;
        this.pumpScheduleService = pumpScheduleService;
        this.proposalService = proposalService;
        this.storageService = storageService;
        this.pumpScheduleJobCfgService = pumpScheduleJobCfgService;
    }

    @RequestMapping(value = "/job/{jobId}", method = RequestMethod.GET)
    public Object getJobProposal(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        logger.debug("Looking up data for job {}", jobId);
        String organizationId = getOrganizationId(request);
        List<Proposal> proposal = null;
        Optional<Job> OptionalJob = jobService.getJob(jobId);
        if (OptionalJob.isPresent()) {
            Job job = OptionalJob.get();
            List<PumpSchedule> pumpSchedules = pumpScheduleService.getByJobId(job.getId());
            PumpScheduleJobCfg pumpScheduleJobCfg = pumpScheduleJobCfgService.getByJobId(request, jobId);
            List<Proposal> dbproposal = proposalService.getByJobId(request, jobId);
            if (!dbproposal.isEmpty())
                proposal = dbproposal;
            return calculateProposal(proposal, organizationId, job, pumpSchedules, pumpScheduleJobCfg);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    @RequestMapping(value = "/job/{jobId}/{wellId}", method = RequestMethod.GET)
    public Object getJobProposal(HttpServletRequest request, @PathVariable("jobId") String jobId, @PathVariable("wellId") String wellId) {
        logger.debug("Looking up data for job {}", jobId);
        String organizationId = getOrganizationId(request);
        Proposal proposal = null;
        Optional<Job> OptionalJob = jobService.getJob(jobId);
        if (OptionalJob.isPresent()) {
            Job job = OptionalJob.get();
            List<PumpSchedule> pumpSchedules = pumpScheduleService.getByJobIdAndWellId(request, job.getId(), wellId);
            PumpScheduleJobCfg pumpScheduleJobCfg = pumpScheduleJobCfgService.getByJobId(request, jobId);
            List<Proposal> dbproposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
            if (!dbproposal.isEmpty())
                proposal = dbproposal.get(0);
            ProposalData proposalData = null;
            return calculateProposal(proposalData, proposal, organizationId, job, pumpSchedules, pumpScheduleJobCfg, wellId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    @RequestMapping(value = "/jobPdf/{jobId}", method = RequestMethod.POST)
    public Object getJobProposalForMergedData(HttpServletRequest request, @PathVariable("jobId") String jobId, @RequestBody List<WellIdContainer> wellIds) {
        logger.debug("<-------------Inside jobPdf------------------>");
        logger.debug("Looking up data for job {}", jobId);
        List<String> ids = new ArrayList();
        for (WellIdContainer id : wellIds) {
            ids.add(id.getId());
        }
        String organizationId = getOrganizationId(request);
        Proposal proposal = null;
        Optional<Job> optionalJob = jobService.getJob(jobId);
        List<ProposalData> responseList = new ArrayList<>();
        if (optionalJob.isPresent()) {
            for (String wellId : ids) {
                Job job = optionalJob.get();
                List<PumpSchedule> pumpSchedules = pumpScheduleService.getByJobIdAndWellId(request, job.getId(), wellId);
                PumpScheduleJobCfg pumpScheduleJobCfg = pumpScheduleJobCfgService.getByJobId(request, jobId);
                List<Proposal> dbproposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
                if (!dbproposal.isEmpty())
                    proposal = dbproposal.get(0);
                else
                    proposal = null;
                ProposalData proposalData = null;
                ProposalData pd = calculateProposal(proposalData, proposal, organizationId, job, pumpSchedules, pumpScheduleJobCfg, wellId);
                ProposalData objProposalData = new ProposalData();
                objProposalData.setProposalId(pd.getProposalId());
                if (pd.getAdditives() != null)
                    objProposalData.setAdditives(pd.getAdditives().stream().collect(Collectors.toList()));
                objProposalData.setFluids(pd.getFluids());
                if (pd.getDiscounts() != null)
                    objProposalData.setDiscounts(pd.getDiscounts().stream().collect(Collectors.toList()));
                objProposalData.setInternal(pd.getInternal());
                if (pd.getProppants() != null)
                    objProposalData.setProppants(copyPropants(pd.getProppants()));
                if (pd.getEquipmentCharges() != null)
                    objProposalData.setEquipmentCharges(pd.getEquipmentCharges().stream().collect(Collectors.toList()));
                objProposalData.setFluidTypeChemicalMap(pd.getFluidTypeChemicalMap());
                objProposalData.setOrganizationId(pd.getOrganizationId());
                if (pd.getPumpSchedules() != null)
                    objProposalData.setPumpSchedules(pd.getPumpSchedules().stream().collect(Collectors.toList()));
                objProposalData.setTotalCleanVolume(pd.getTotalCleanVolume());
                objProposalData.setTotalStages(pd.getTotalStages());
                if (pd.getOtherCharges() != null)
                    objProposalData.setOtherCharges(pd.getOtherCharges().stream().collect(Collectors.toList()));
                responseList.add(objProposalData);
            }
            return responseList;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    private List copyPropants(List propants) {
        List newList = new ArrayList();
        for (int i = 0; i < propants.size(); i++) {
            Proppant p = (Proppant) propants.get(i);
            newList.add(new Proppant(p));
        }
        return newList;

    }

    @RequestMapping(value = "/{proposalId}", method = RequestMethod.GET)
    public Proposal getProposalById(HttpServletRequest request, @PathVariable("proposalId") String proposalId) {
        logger.debug("Looking up data for proposal {}", proposalId);

        Proposal proposal = proposalService.getById(request, proposalId).get();
        return proposal;
    }

    @RequestMapping(value = "/{proposalId}", method = RequestMethod.PUT)
    public void updateProposal(HttpServletRequest request, @PathVariable("proposalId") String proposalId, @RequestBody Proposal proposal) {
        proposalService.updateProposal(request, proposal);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void saveProposal(HttpServletRequest request, @RequestBody Proposal proposal) {
        List<Proposal> proposals = proposalService.getByJobIdAndWellId(request, proposal.getJobId(), proposal.getWellId());
        if (!proposals.isEmpty()) {
            List<Proposal> p = proposals;
            proposal.setId(p.get(0).getId());
            proposalService.updateProposal(request, proposal);
        } else
            proposalService.saveProposal(request, proposal);
    }

    @RequestMapping(value = "/{proposalId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProposal(HttpServletRequest request, @PathVariable("proposalId") String proposalId) {
        proposalService.deleteProposal(request, proposalId);
    }

    @RequestMapping(value = "/pdf/blob/{jobId}/{wellId}", method = RequestMethod.POST)
    public JobProposalPbfBlobResponse getJobProposalPbfBlob(HttpServletRequest request, @PathVariable("jobId") String jobId, @PathVariable("wellId") String wellId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        logger.debug("Looking up data for job {}", jobId);

        // by byte code
        byte[] bytes = multipartFile.getBytes();
        // save this string
        storageService.store(jobId, multipartFile);
        //saving fileName
        List<Proposal> proposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
        Proposal p = proposal.get(0);
        p.setFileName(multipartFile.getOriginalFilename());
        proposalService.updateProposal(request, p);

        JobProposalPbfBlobResponse pdfResponse = new JobProposalPbfBlobResponse();
        pdfResponse.setString(Base64.getEncoder().encodeToString(bytes));
        pdfResponse.setFileName(multipartFile.getOriginalFilename());
        return pdfResponse;
    }

    @RequestMapping(value = "/pdf/blob/{jobId}/{wellId}", method = RequestMethod.GET)
    public ProposalPdfResponse getJobProposalPbf(HttpServletRequest request, @PathVariable("jobId") String jobId, @PathVariable("wellId") String wellId) throws IOException {
        logger.debug("Looking up data for job {}", jobId);
        ProposalPdfResponse proposalPdfResponse = new ProposalPdfResponse();
        List<Proposal> proposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
        try {
            proposalPdfResponse.setUploadedPdf(storageService.loadFileAsBase64String(jobId));
            proposalPdfResponse.setFileName(proposal.get(0).getFileName());
            proposalPdfResponse.setWellId(wellId);
            proposalPdfResponse.setJobId(jobId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File is not Found for Terms & Conditions");
        }
        return proposalPdfResponse;
    }

    @RequestMapping(value = "/copyFieldTicket", method = RequestMethod.PUT)
    public void copyFieldTicket(HttpServletRequest request,
                                @RequestParam(name = "jobId") String jobId,
                                @RequestParam(name = "well") String well,
                                @RequestParam(name = "name") String name, @RequestBody FieldTicket fieldTicket) {
        proposalService.copyFieldTicket(request, jobId, well, name, fieldTicket);
    }




}
