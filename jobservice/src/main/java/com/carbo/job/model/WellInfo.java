package com.carbo.job.model;

import com.carbo.job.model.well.StageInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "well-infos")
public class WellInfo {
    @Id
    private String id;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("wellId")
    @NotEmpty(message = "well id can not be empty")
    private String wellId;

    @Field("perfSize")
    private Float perfSize;

    @Field("kop")
    private Float kop;

    @Field("heel")
    private Float heel;

    @Field("stages")
    private List<StageInfo> stages = new ArrayList<>();

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("organizationId")
    private String organizationId;

    @Field("ts")
    private Long ts;

    @Field("lastModifiedBy")
    private String lastModifiedBy;

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

    public Float getPerfSize() {
        return perfSize;
    }

    public void setPerfSize(Float perfSize) {
        this.perfSize = perfSize;
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

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Long getCreated() {
        return created;
    }

    public List<StageInfo> getStages() {
        return stages;
    }

    public void setStages(List<StageInfo> stages) {
        this.stages = stages;
    }

    public Float getKop() {
        return kop;
    }

    public void setKop(Float kop) {
        this.kop = kop;
    }

    public Float getHeel() {
        return heel;
    }

    public void setHeel(Float heel) {
        this.heel = heel;
    }
    
}
