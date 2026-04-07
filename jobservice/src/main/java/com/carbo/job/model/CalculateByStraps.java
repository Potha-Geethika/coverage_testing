package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Document(collection = "calculate-by-straps")
@CompoundIndex(def = "{'jobId': 1, 'wellId': 1, 'stage': 1}", name = "job_id_well_id_stage_index", unique = true)
public class   CalculateByStraps {

    @Id
    private String id;

    @Field("jobId")
    @NotNull
    @Indexed(unique = false)
    private String jobId;

    @Field("wellId")
    @Indexed(unique = false)
    @NotNull
    private String wellId;

    @Field("stage")
    @NotNull
    private Float stage;

    @Field("CalculateByStraps")
    private Boolean calculateByStraps;

    @Field("organizationId")
    @NotNull
    @Indexed(unique = false)
    private String organizationId;

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

    public Float getStage() {
        return stage;
    }

    public void setStage(Float stage) {
        this.stage = stage;
    }

    public Boolean getCalculateByStraps() {
        return calculateByStraps;
    }

    public void setCalculateByStraps(Boolean calculateByStraps) {
        this.calculateByStraps = calculateByStraps;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
