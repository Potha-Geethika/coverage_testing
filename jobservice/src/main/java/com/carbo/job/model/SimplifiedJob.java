package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.*;

@Document(collection = "jobs")
@CompoundIndex(def = "{'_id': 1, 'users._id': 1}", name = "job_id_user_id_index", unique = true)
public class SimplifiedJob {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    private String name;

    @Field("jobNumber")
    @NotEmpty(message = "jobNumber can not be empty")
    @Size(max = 14, message = "jobNumber can not be more than 14 characters.")
    private String jobNumber;

    @Field("fleet")
    private String fleet;

    @Field("operator")
    private String operator;

    @Field("pad")
    private String pad;

    @Field("location")
    private String location;

    @Field("zipper")
    private Boolean zipper;

    @Field("wells")
    private List<SimplifiedWell> wells = new ArrayList<>();

    @Field("targetStagesPerDay")
    private int targetStagesPerDay;

    @Field("targetDailyPumpTime")
    private float targetDailyPumpTime;

    @Field("proppantSchematicType")
    private String proppantSchematicType = "silos";

    @Field("numberOfUnits")
    private Integer numberOfUnits = 3;

    @Field("coneLbs")
    private Float coneLbs = 1400.0f;

    @Field("blenders")
    private List<OnSiteEquipment> blenders = new ArrayList<>();

    @Field("hydrationUnits")
    private List<OnSiteEquipment> hydrationUnits = new ArrayList<>();

    @Field("pumps")
    private List<OnSiteEquipment> pumps = new ArrayList<>();

    @Field("chemAds")
    private List<OnSiteEquipment> chemAds = new ArrayList<>();

    @Field("ironManifolds")
    private List<OnSiteEquipment> ironManifolds = new ArrayList<>();

    @Field("dataVans")
    private List<OnSiteEquipment> dataVans = new ArrayList<>();

    @Field("silos")
    private List<OnSiteEquipment> silos = new ArrayList<>();

    @Field("startDate")
    private Long startDate;

    @Field("startDateStr")
    private String startDateStr;

    @Field("organizationId")
    @Indexed
    private String organizationId;

    @Field("status")
    private String status;

    @Field("wellheadCo")
    private String wellheadCo;

    @Field("wirelineCo")
    private String wirelineCo;

    @Field("includeToeStage")
    private Boolean includeToeStage;

    @Field("ts")
    private Long ts;

    @Field("rts")
    private Long rts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("backupDate")
    @Indexed
    private Date backupDate;

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("vendors")
    private List<Vendor> vendors;

    @Field("automatize")
    private boolean automatize;

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

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getFleet() {
        return fleet;
    }

    public void setFleet(String fleet) {
        this.fleet = fleet;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getZipper() {
        return zipper;
    }

    public void setZipper(Boolean zipper) {
        this.zipper = zipper;
    }

    public List<SimplifiedWell> getWells() {
        return wells;
    }

    public void setWells(List<SimplifiedWell> wells) {
        this.wells = wells;
    }

    public int getTargetStagesPerDay() {
        return targetStagesPerDay;
    }

    public void setTargetStagesPerDay(int targetStagesPerDay) {
        this.targetStagesPerDay = targetStagesPerDay;
    }

    public float getTargetDailyPumpTime() { return targetDailyPumpTime; }

    public void setTargetDailyPumpTime(float targetDailyPumpTime) { this.targetDailyPumpTime = targetDailyPumpTime; }

    public String getProppantSchematicType() {
        return proppantSchematicType;
    }

    public void setProppantSchematicType(String proppantSchematicType) {
        this.proppantSchematicType = proppantSchematicType;
    }

    public List<OnSiteEquipment> getBlenders() {
        return blenders;
    }

    public void setBlenders(List<OnSiteEquipment> blenders) {
        this.blenders = blenders;
    }

    public List<OnSiteEquipment> getHydrationUnits() {
        return hydrationUnits;
    }

    public void setHydrationUnits(List<OnSiteEquipment> hydrationUnits) {
        this.hydrationUnits = hydrationUnits;
    }

    public List<OnSiteEquipment> getPumps() {
        return pumps;
    }

    public void setPumps(List<OnSiteEquipment> pumps) {
        this.pumps = pumps;
    }

    public List<OnSiteEquipment> getChemAds() {
        return chemAds;
    }

    public void setChemAds(List<OnSiteEquipment> chemAds) {
        this.chemAds = chemAds;
    }

    public List<OnSiteEquipment> getIronManifolds() {
        return ironManifolds;
    }

    public void setIronManifolds(List<OnSiteEquipment> ironManifolds) {
        this.ironManifolds = ironManifolds;
    }

    public List<OnSiteEquipment> getDataVans() {
        return dataVans;
    }

    public void setDataVans(List<OnSiteEquipment> dataVans) {
        this.dataVans = dataVans;
    }

    public List<OnSiteEquipment> getSilos() { return silos; }

    public void setSilos(List<OnSiteEquipment> silos) { this.silos = silos; }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Long getRts() {
        return rts;
    }

    public void setRts(Long rts) {
        this.rts = rts;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public Long getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getConeLbs() {
        return coneLbs;
    }

    public void setConeLbs(Float coneLbs) {
        this.coneLbs = coneLbs;
    }

    public String getWellheadCo() {
        return wellheadCo;
    }

    public void setWellheadCo(String wellheadCo) {
        this.wellheadCo = wellheadCo;
    }

    public String getWirelineCo() {
        return wirelineCo;
    }

    public void setWirelineCo(String wirelineCo) {
        this.wirelineCo = wirelineCo;
    }

    public Long getModified() {
        return modified;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Boolean getIncludeToeStage() {
        return includeToeStage;
    }

    public void setIncludeToeStage(Boolean includeToeStage) {
        this.includeToeStage = includeToeStage;
    }

    public Date getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(Date backupDate) {
        this.backupDate = backupDate;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public boolean isAutomatize() { return automatize;}

    public void setAutomatize(boolean automatize) { this.automatize = automatize;}
}
