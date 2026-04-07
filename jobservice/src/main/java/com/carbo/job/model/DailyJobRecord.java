package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "daily-job-records")
@CompoundIndex(def = "{'jobId': 1, 'date': 1}", name = "jobId_date_index", unique = true)
public class DailyJobRecord {
    public DailyJobRecord(){
    }
    @Id
    private String id;

    @Field("date")
    private Date date;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    private String jobId;

    @Field("fleet")
    private String fleet;

    @Field("operator")
    private String operator;

    @Field("targetStagePerDay")
    private Integer targetStagePerDay;

    @Field("actualStagePerDay")
    private Integer actualStagePerDay;

    @Field("targetHoursPerDay")
    private Float targetHoursPerDay;

    @Field("actualHoursPerDay")
    private Float actualHoursPerDay;

    @Field("nptHours")
    private Float nptHours;

    @Field("scheduledHours")
    private Float scheduledHours;

    @Field("sharedOrganizationId")
    private String sharedOrganizationId;

    @Field("pad")
    private String pad;

    public DailyJobRecord(String organizationId,
                          Date date,
                          String jobId,
                          String fleet, Integer targetStagePerDay,
                          Integer actualStagePerDay, Float targetHoursPerDay,
                          Float actualHoursPerDay, Float nptHours, Float scheduledHours, String pad) {
        this.organizationId = organizationId;
        this.date = date;
        this.jobId = jobId;
        this.fleet = fleet;
        this.targetStagePerDay = targetStagePerDay;
        this.actualStagePerDay = actualStagePerDay;
        this.targetHoursPerDay = targetHoursPerDay;
        this.actualHoursPerDay = actualHoursPerDay;
        this.nptHours = nptHours;
        this.scheduledHours = scheduledHours;
        this.pad =pad;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public Integer getTargetStagePerDay() {
        return targetStagePerDay;
    }

    public void setTargetStagePerDay(Integer targetStagePerDay) {
        this.targetStagePerDay = targetStagePerDay;
    }

    public Integer getActualStagePerDay() {
        return actualStagePerDay;
    }

    public void setActualStagePerDay(Integer actualStagePerDay) {
        this.actualStagePerDay = actualStagePerDay;
    }

    public Float getTargetHoursPerDay() {
        return targetHoursPerDay;
    }

    public void setTargetHoursPerDay(Float targetHoursPerDay) {
        this.targetHoursPerDay = targetHoursPerDay;
    }

    public Float getActualHoursPerDay() {
        return actualHoursPerDay;
    }

    public void setActualHoursPerDay(Float actualHoursPerDay) {
        this.actualHoursPerDay = actualHoursPerDay;
    }

    public Float getNptHours() {
        return nptHours;
    }

    public void setNptHours(Float nptHours) {
        this.nptHours = nptHours;
    }

    public Float getScheduledHours() {
        return scheduledHours;
    }

    public void setScheduledHours(Float scheduledHours) {
        this.scheduledHours = scheduledHours;
    }

    public String getSharedOrganizationId() {
        return sharedOrganizationId;
    }

    public void setSharedOrganizationId(String sharedOrganizationId) {
        this.sharedOrganizationId = sharedOrganizationId;
    }

    public String getPad() {return pad;}

    public void setPad(String pad) {
        this.pad = pad;
    }
}
