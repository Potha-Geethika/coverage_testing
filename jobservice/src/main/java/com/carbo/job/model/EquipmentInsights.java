package com.carbo.job.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "equipments-insights")
public class EquipmentInsights {

    @Id
    private String id;
    @Field("equipmentName")
    private String equipmentName;

    @Field("equipmentId")
    private String equipmentId;

    @Field("equipmentType")
    private String equipmentType;

    @Field("fleetName")
    private String fleetName;

    @Field("fleetId")
    private String fleetId;

    @Field("fleetType")
    private String fleetType;

    @Field("fleetStatus")
    private FleetStatusEnum fleetStatus;

    @Field("jobNumber")
    private String jobNumber;

    @Field("jobId")
    private String jobId;

    @Field("jobStatus")
    private String jobStatus;

    @Field("startDate")
    private Long startDate;

    @Field("endDate")
    private Long endDate;

    @Field("jobDays")
    private long jobDays;

    @Field("rentalStartDate")
    private Long rentalStartDate;

    @Field("rentalEndDate")
    private Long rentalEndDate;

    @Field("createdTime")
    private long createdTime=new Date().getTime();

    @Field("rental")
    private String rental;

    @Field("organizationId")
    private String organizationId;

    @Field("districtColor")
    private String districtColor;

    @Field("jobCompletionPercentage")
    private Double jobCompletionPercentage;

    @Field("jobStartDate")
    private Long jobStartDate;

    @Field("jobEndDate")
    private Long jobEndDate;

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getEquipmentName() {return equipmentName;}

    public void setEquipmentName(String equipmentName) {this.equipmentName = equipmentName;}

    public String getEquipmentId() {return equipmentId;}

    public void setEquipmentId(String equipmentId) {this.equipmentId = equipmentId;}

    public String getEquipmentType() {return equipmentType;}

    public void setEquipmentType(String equipmentType) {this.equipmentType = equipmentType;}

    public String getFleetName() {return fleetName;}

    public void setFleetName(String fleetName) {this.fleetName = fleetName;}

    public String getFleetId() {return fleetId;}

    public void setFleetId(String fleetId) {this.fleetId = fleetId;}

    public String getFleetType() {return fleetType;}

    public void setFleetType(String fleetType) {this.fleetType = fleetType;}

    public String getJobNumber() {return jobNumber;}

    public void setJobNumber(String jobNumber) {this.jobNumber = jobNumber;}

    public String getJobId() {return jobId;}

    public void setJobId(String jobId) {this.jobId = jobId;}

    public Long getStartDate() {return startDate;}

    public void setStartDate(Long startDate) {this.startDate = startDate;}

    public Long getEndDate() {return endDate;}

    public void setEndDate(Long endDate) {this.endDate = endDate;}

    public long getJobDays() {return jobDays;}

    public void setJobDays(long jobDays) {this.jobDays = jobDays;}

    public long getCreatedTime() {return createdTime;}

    public void setCreatedTime(long createdTime) {this.createdTime = createdTime;}

    public String getRental() {return rental;}

    public void setRental(String rental) {this.rental = rental;}

    public String getOrganizationId() {return organizationId;}

    public void setOrganizationId(String organizationId) {this.organizationId = organizationId;}

    public String getDistrictColor() {return districtColor;}

    public void setDistrictColor(String districtColor) {this.districtColor = districtColor;}

    public Double getJobCompletionPercentage() {return jobCompletionPercentage;}

    public void setJobCompletionPercentage(Double jobCompletionPercentage) {this.jobCompletionPercentage = jobCompletionPercentage;}

    public Long getJobStartDate() {return jobStartDate;}

    public void setJobStartDate(Long jobStartDate) {this.jobStartDate = jobStartDate;}

    public Long getJobEndDate() {return jobEndDate;}

    public void setJobEndDate(Long jobEndDate) {this.jobEndDate = jobEndDate;}

    public String getJobStatus() {return jobStatus;}

    public void setJobStatus(String jobStatus) {this.jobStatus = jobStatus;}

    public FleetStatusEnum getFleetStatus() {return fleetStatus;}

    public void setFleetStatus(FleetStatusEnum fleetStatus) {this.fleetStatus = fleetStatus;}

    public Long getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(Long rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public Long getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(Long rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }
}
