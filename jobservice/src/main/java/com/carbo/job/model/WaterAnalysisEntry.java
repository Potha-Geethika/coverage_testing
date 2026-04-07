package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;

@Document(collection = "water-analysis-entries")
public class WaterAnalysisEntry {
    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("wellId")
    @NotEmpty(message = "well id can not be empty")
    private String wellId;

    @Field("stage")
    @NotEmpty(message = "stage can not be empty")
    private Short stage;

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

    @Field("source")
    private String source;

    @Field("ph")
    private Float ph;

    @Field("specificGravity")
    private Float specificGravity;

    @Field("h2s")
    private String h2s;

    @Field("temp")
    private Float temp;

    @Field("peraceticAcid")
    private String peraceticAcid;

    @Field("siverNitrate")
    private Float siverNitrate;

    @Field("calciumVolumeTitr")
    private Float calciumVolumeTitr;

    @Field("calciumSampleSize")
    private Float calciumSampleSize;

    @Field("calciumFactor")
    private Float calciumFactor;

    @Field("magnesiumVolumeTitr")
    private Float magnesiumVolumeTitr;

    @Field("magnesiumSampleSize")
    private Float magnesiumSampleSize;

    @Field("magnesiumFactor")
    private Float magnesiumFactor;

    @Field("bicarbsVolumeTitr")
    private Float bicarbsVolumeTitr;

    @Field("bicarbsSampleSize")
    private Float bicarbsSampleSize;

    @Field("bicarbsFactor")
    private Float bicarbsFactor;

    @Field("carbonateVolumeTitr")
    private Float carbonateVolumeTitr;

    @Field("carbonateSampleSize")
    private Float carbonateSampleSize;

    @Field("carbonateFactor")
    private Float carbonateFactor;

    @Field("chlorideVolumeTitr")
    private Float chlorideVolumeTitr;

    @Field("chlorideSampleSize")
    private Float chlorideSampleSize;

    @Field("chlorideFactor")
    private Float chlorideFactor;

    @Field("ironResult")
    private Float ironResult;

    @Field("sulfateResult")
    private Float sulfateResult;

    @Field("bariumResult")
    private Float bariumResult;

    @Field("potassiumResult")
    private Float potassiumResult;

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

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public Short getStage() {
        return stage;
    }

    public void setStage(Short stage) {
        this.stage = stage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Float getPh() {
        return ph;
    }

    public void setPh(Float ph) {
        this.ph = ph;
    }

    public Float getSpecificGravity() {
        return specificGravity;
    }

    public void setSpecificGravity(Float specificGravity) {
        this.specificGravity = specificGravity;
    }

    public String getH2s() {
        return h2s;
    }

    public void setH2s(String h2s) {
        this.h2s = h2s;
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public String getPeraceticAcid() {
        return peraceticAcid;
    }

    public void setPeraceticAcid(String peraceticAcid) {
        this.peraceticAcid = peraceticAcid;
    }

    public Float getSiverNitrate() {
        return siverNitrate;
    }

    public void setSiverNitrate(Float siverNitrate) {
        this.siverNitrate = siverNitrate;
    }

    public Float getCalciumVolumeTitr() {
        return calciumVolumeTitr;
    }

    public void setCalciumVolumeTitr(Float calciumVolumeTitr) {
        this.calciumVolumeTitr = calciumVolumeTitr;
    }

    public Float getCalciumSampleSize() {
        return calciumSampleSize;
    }

    public void setCalciumSampleSize(Float calciumSampleSize) {
        this.calciumSampleSize = calciumSampleSize;
    }

    public Float getCalciumFactor() {
        return calciumFactor;
    }

    public void setCalciumFactor(Float calciumFactor) {
        this.calciumFactor = calciumFactor;
    }

    public Float getMagnesiumVolumeTitr() {
        return magnesiumVolumeTitr;
    }

    public void setMagnesiumVolumeTitr(Float magnesiumVolumeTitr) {
        this.magnesiumVolumeTitr = magnesiumVolumeTitr;
    }

    public Float getMagnesiumSampleSize() {
        return magnesiumSampleSize;
    }

    public void setMagnesiumSampleSize(Float magnesiumSampleSize) {
        this.magnesiumSampleSize = magnesiumSampleSize;
    }

    public Float getMagnesiumFactor() {
        return magnesiumFactor;
    }

    public void setMagnesiumFactor(Float magnesiumFactor) {
        this.magnesiumFactor = magnesiumFactor;
    }

    public Float getBicarbsVolumeTitr() {
        return bicarbsVolumeTitr;
    }

    public void setBicarbsVolumeTitr(Float bicarbsVolumeTitr) {
        this.bicarbsVolumeTitr = bicarbsVolumeTitr;
    }

    public Float getBicarbsSampleSize() {
        return bicarbsSampleSize;
    }

    public void setBicarbsSampleSize(Float bicarbsSampleSize) {
        this.bicarbsSampleSize = bicarbsSampleSize;
    }

    public Float getBicarbsFactor() {
        return bicarbsFactor;
    }

    public void setBicarbsFactor(Float bicarbsFactor) {
        this.bicarbsFactor = bicarbsFactor;
    }

    public Float getCarbonateVolumeTitr() {
        return carbonateVolumeTitr;
    }

    public void setCarbonateVolumeTitr(Float carbonateVolumeTitr) {
        this.carbonateVolumeTitr = carbonateVolumeTitr;
    }

    public Float getCarbonateSampleSize() {
        return carbonateSampleSize;
    }

    public void setCarbonateSampleSize(Float carbonateSampleSize) {
        this.carbonateSampleSize = carbonateSampleSize;
    }

    public Float getCarbonateFactor() {
        return carbonateFactor;
    }

    public void setCarbonateFactor(Float carbonateFactor) {
        this.carbonateFactor = carbonateFactor;
    }

    public Float getChlorideVolumeTitr() {
        return chlorideVolumeTitr;
    }

    public void setChlorideVolumeTitr(Float chlorideVolumeTitr) {
        this.chlorideVolumeTitr = chlorideVolumeTitr;
    }

    public Float getChlorideSampleSize() {
        return chlorideSampleSize;
    }

    public void setChlorideSampleSize(Float chlorideSampleSize) {
        this.chlorideSampleSize = chlorideSampleSize;
    }

    public Float getChlorideFactor() {
        return chlorideFactor;
    }

    public void setChlorideFactor(Float chlorideFactor) {
        this.chlorideFactor = chlorideFactor;
    }

    public Float getIronResult() {
        return ironResult;
    }

    public void setIronResult(Float ironResult) {
        this.ironResult = ironResult;
    }

    public Float getSulfateResult() {
        return sulfateResult;
    }

    public void setSulfateResult(Float sulfateResult) {
        this.sulfateResult = sulfateResult;
    }

    public Float getBariumResult() {
        return bariumResult;
    }

    public void setBariumResult(Float bariumResult) {
        this.bariumResult = bariumResult;
    }

    public Float getPotassiumResult() {
        return potassiumResult;
    }

    public void setPotassiumResult(Float potassiumResult) {
        this.potassiumResult = potassiumResult;
    }
}
