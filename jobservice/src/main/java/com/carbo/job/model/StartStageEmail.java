package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class StartStageEmail extends Email {
    @Field("well")
    private String well;

    @Field("owTime")
    private String owTime;

    @Field("goToAccess")
    private String goToAccess;

    @Field("stage")
    private String stage;

    @Field("pad")
    private String pad;

    @Field("owPressure")
    private Float owPressure;

    @Field("pumpsOnline")
    private Integer pumpsOnline;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public String getOwTime() {
        return owTime;
    }

    public void setOwTime(String owTime) {
        this.owTime = owTime;
    }

    public String getGoToAccess() {
        return goToAccess;
    }

    public void setGoToAccess(String goToAccess) {
        this.goToAccess = goToAccess;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
    }

    public Float getOwPressure() {
        return owPressure;
    }

    public void setOwPressure(Float owPressure) {
        this.owPressure = owPressure;
    }

    public Integer getPumpsOnline() {
        return pumpsOnline;
    }

    public void setPumpsOnline(Integer pumpsOnline) {
        this.pumpsOnline = pumpsOnline;
    }
}
