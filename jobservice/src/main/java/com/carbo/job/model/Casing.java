package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;

@Document(collection = "casings")
public class Casing {
    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("wellId")
    @NotEmpty(message = "wellId id can not be empty")
    private String wellId;

    @Field("stage")
    @NotEmpty(message = "stage can not be empty")
    private String stage;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("type")
    private String type;

    @Field("casingSpec")
    private String casingSpec;

    @Field("bottomTVD")
    private Float bottomTVD;

    @Field("bottomTMD")
    private Float bottomTMD;

    @Field("IDVal")
    private Float IDVal;

    @Field("drift")
    private Float drift;

    @Field("burst")
    private Float burst;

    @Field("collapse")
    private Float collapse;

    @Field("yieldValue")
    private Float yieldValue;

    @Field("jnt")
    private Float jnt;

    @Field("capacity")
    private Float capacity;

    @Field("displace")
    private Float displace;

    @Field("dvTool")
    private Float dvTool;

    @Field("sxsCmt")
    private String sxsCmt;

    @Field("holeSize")
    private String holeSize;

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

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
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


    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getCasingSpec() {
        return casingSpec;
    }

    public void setCasingSpec(String casingSpec) {
        this.casingSpec = casingSpec;
    }

    public String getSxsCmt() {
        return sxsCmt;
    }

    public void setSxsCmt(String sxsCmt) {
        this.sxsCmt = sxsCmt;
    }

    public String getHoleSize() {
        return holeSize;
    }

    public void setHoleSize(String holeSize) {
        this.holeSize = holeSize;
    }

    public Float getBottomTVD() {
        return bottomTVD;
    }

    public void setBottomTVD(Float bottomTVD) {
        this.bottomTVD = bottomTVD;
    }

    public Float getBottomTMD() {
        return bottomTMD;
    }

    public void setBottomTMD(Float bottomTMD) {
        this.bottomTMD = bottomTMD;
    }

    public Float getIDVal() {
        return IDVal;
    }

    public void setIDVal(Float IDVal) {
        this.IDVal = IDVal;
    }

    public Float getDrift() {
        return drift;
    }

    public void setDrift(Float drift) {
        this.drift = drift;
    }

    public Float getBurst() {
        return burst;
    }

    public void setBurst(Float burst) {
        this.burst = burst;
    }

    public Float getCollapse() { return collapse; }

    public void setCollapse(Float collapse) {
        this.collapse = collapse;
    }

    public Float getYieldValue() {
        return yieldValue;
    }

    public void setYieldValue(Float yieldValue) {
        this.yieldValue = yieldValue;
    }

    public Float getJnt() {
        return jnt;
    }

    public void setJnt(Float jnt) {
        this.jnt = jnt;
    }

    public Float getCapacity() {
        return capacity;
    }

    public void setCapacity(Float capacity) {
        this.capacity = capacity;
    }

    public Float getDisplace() {
        return displace;
    }

    public void setDisplace(Float displace) {
        this.displace = displace;
    }

    public Float getDvTool() {
        return dvTool;
    }

    public void setDvTool(Float dvTool) {
        this.dvTool = dvTool;
    }

}
