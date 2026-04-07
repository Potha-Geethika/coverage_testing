package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;

@Document(collection = "pending-iron-failures")
public class PendingIronFailure {
    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("equipment")
    @NotEmpty(message = "equipment can not be empty")
    private String equipment;

    @Field("pumpIssueId")
    private String pumpIssueId;

    @Field("date")
    private Long date;

    @Field("totalNpt")
    private Float totalNpt;

    @Field("startTime")
    private Long startTime;

    @Field("endTime")
    private Long endTime;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

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

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Float getTotalNpt() {
        return totalNpt;
    }

    public void setTotalNpt(Float totalNpt) {
        this.totalNpt = totalNpt;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getPumpIssueId() {
        return pumpIssueId;
    }

    public void setPumpIssueId(String pumpIssueId) {
        this.pumpIssueId = pumpIssueId;
    }
}
