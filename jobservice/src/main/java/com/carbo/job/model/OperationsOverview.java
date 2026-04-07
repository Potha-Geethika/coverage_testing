package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "operations-overviews")
public class OperationsOverview {
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

    @Field("stationCount")
    @NotEmpty(message = "stationCount can not be empty")
    private Byte stationCount;

    @Field("activePumpsTop")
    private List<EquipmentOverview> activePumpsTop = new ArrayList<>();

    @Field("activePumpsBottom")
    private List<EquipmentOverview> activePumpsBottom = new ArrayList<>();

    @Field("standbyPumps")
    private List<EquipmentOverview> standbyPumps = new ArrayList<>();

    @Field("checkValves")
    private List<EquipmentOverview> checkValves = new ArrayList<>();

    @Field("sevenInchValves")
    private List<EquipmentOverview> sevenInchValves = new ArrayList<>();

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

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Byte getStationCount() {
        return stationCount;
    }

    public void setStationCount(Byte stationCount) {
        this.stationCount = stationCount;
    }

    public List<EquipmentOverview> getActivePumpsTop() {
        return activePumpsTop;
    }

    public void setActivePumpsTop(List<EquipmentOverview> activePumpsTop) {
        this.activePumpsTop = activePumpsTop;
    }

    public List<EquipmentOverview> getActivePumpsBottom() {
        return activePumpsBottom;
    }

    public void setActivePumpsBottom(List<EquipmentOverview> activePumpsBottom) {
        this.activePumpsBottom = activePumpsBottom;
    }

    public List<EquipmentOverview> getStandbyPumps() {
        return standbyPumps;
    }

    public void setStandbyPumps(List<EquipmentOverview> standbyPumps) {
        this.standbyPumps = standbyPumps;
    }

    public List<EquipmentOverview> getCheckValves() {
        return checkValves;
    }

    public void setCheckValves(List<EquipmentOverview> checkValves) {
        this.checkValves = checkValves;
    }

    public List<EquipmentOverview> getSevenInchValves() {
        return sevenInchValves;
    }

    public void setSevenInchValves(List<EquipmentOverview> sevenInchValves) {
        this.sevenInchValves = sevenInchValves;
    }
}
