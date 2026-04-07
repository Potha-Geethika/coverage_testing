package com.carbo.job.model.well;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WellResponse {

    @Field("name")
    private String name;

    @Field("api")
    private String api;

    @Field("afeNumber")
    private String afeNumber;

    @Field("longitude")
    private double longitude;

    @Field("latitude")
    private double latitude;

    @Field("totalStages")
    private int totalStages;

    @Field("operatorId")
    private String operatorId;

    @Field("padId")
    private String padId;

    @Field("fracproId")
    private int fracproId;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("ts")
    private Long ts;

    @Field("organizationId")
    private String organizationId;

    @Field("updatedStage")
    private StageInfo updatedStage;

    public StageInfo getUpdatedStage() {
        return updatedStage;
    }

    public void setUpdatedStage(StageInfo updatedStage) {
        this.updatedStage = updatedStage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAfeNumber() {
        return afeNumber;
    }

    public void setAfeNumber(String afeNumber) {
        this.afeNumber = afeNumber;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getTotalStages() {
        return totalStages;
    }

    public void setTotalStages(int totalStages) {
        this.totalStages = totalStages;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public int getFracproId() {
        return fracproId;
    }

    public void setFracproId(int fracproId) {
        this.fracproId = fracproId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getPadId() {
        return padId;
    }

    public void setPadId(String padId) {
        this.padId = padId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getCreated() {
        return created;
    }

}