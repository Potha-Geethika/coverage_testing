package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "maintenance-entries")
public class MaintenanceEntry {
    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("date")
    @NotEmpty(message = "date can not be empty")
    private Date date;

    @Field("shift")
    @NotEmpty(message = "shift can not be empty")
    private String shift;

    @Field("crew")
    @NotEmpty(message = "crew can not be empty")
    private String crew;

    @Field("equipment")
    @NotEmpty(message = "equipment can not be empty")
    private String equipment;

    @Field("consumable")
    @NotEmpty(message = "consumable can not be empty")
    private String consumable;

    @Field("holes")
    @NotEmpty(message = "holes can not be empty")
    private Integer[] holes;

    @Field("hour")
    @NotEmpty(message = "hour can not be empty")
    private Float hour;

    @Field("remainingHour")
    private Float remainingHour;

    @Field("suctionHoles")
    @NotEmpty(message = "suctionHoles can not be empty")
    private Integer[] suctionHoles;

    @Field("dischargeHoles")
    @NotEmpty(message = "dischargeHoles can not be empty")
    private Integer[] dischargeHoles;

    @Field("issueCategory")
    private String issueCategory;

    @Field("issue")
    private String issue;

    @Field("maintenanceHours")
    private Map<ConsumableTypesEnum, Map<HolesTypeEnum, Map<Integer, Double>>> maintenanceHours;

    @Field("cleanHour")
    private Float cleanHour;

    @Field("dirtyHour")
    private Float dirtyHour;

    @Field("standByHour")
    private Float standByHour;

    @Field("currentHour")
    private Float currentHour;

    @Field("holesGroup")
    private List<String> holesGroup = new ArrayList<>();

    @Field("size")
    private float size;

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
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

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Integer[] getHoles() {
        return holes;
    }

    public void setHoles(Integer[] holes) {
        this.holes = holes;
    }

    public Integer[] getSuctionHoles() {
        return suctionHoles;
    }

    public void setSuctionHoles(Integer[] suctionHoles) {
        this.suctionHoles = suctionHoles;
    }

    public Integer[] getDischargeHoles() {
        return dischargeHoles;
    }

    public void setDischargeHoles(Integer[] dischargeHoles) {
        this.dischargeHoles = dischargeHoles;
    }

    public Float getHour() {
        return hour;
    }

    public void setHour(Float hour) {
        this.hour = hour;
    }

    public String getConsumable() {
        return consumable;
    }

    public void setConsumable(String consumable) {
        this.consumable = consumable;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getIssueCategory() {
        return issueCategory;
    }

    public void setIssueCategory(String issueCategory) {
        this.issueCategory = issueCategory;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public Float getCleanHour() {
        return cleanHour;
    }

    public void setCleanHour(Float cleanHour) {
        this.cleanHour = cleanHour;
    }

    public Float getDirtyHour() {
        return dirtyHour;
    }

    public void setDirtyHour(Float dirtyHour) {
        this.dirtyHour = dirtyHour;
    }

    public Float getStandByHour() {
        return standByHour;
    }

    public void setStandByHour(Float standByHour) {
        this.standByHour = standByHour;
    }

    public Float getRemainingHour() {
        return remainingHour;
    }

    public void setRemainingHour(Float remainingHour) {
        this.remainingHour = remainingHour;
    }

    public Float getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(Float currentHour) {
        this.currentHour = currentHour;
    }

    public List<String> getHolesGroup() {
        return holesGroup;
    }

    public void setHolesGroup(List<String> holesGroup) {
        this.holesGroup = holesGroup;
    }

    public Map<ConsumableTypesEnum, Map<HolesTypeEnum, Map<Integer, Double>>> getMaintenanceHours() {
        return maintenanceHours;
    }

    public void setMaintenanceHours(Map<ConsumableTypesEnum, Map<HolesTypeEnum, Map<Integer, Double>>> maintenanceHours) {
        this.maintenanceHours = maintenanceHours;
    }
}
