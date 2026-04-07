package com.carbo.job.model.well;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StageInfo {

    @Field("stage")
    private String stage;

    @Field("skipStage")
    private boolean skipStage;
    @Field("stageNumber")
    private String stageNumber;

    @Field("top")
    private Float top;

    @Field("bottom")
    private Float bottom;

    @Field("tvd")
    private Float tvd;

    @Field("plug")
    private Float plug;

    @Field("diesel")
    private Float diesel;

    @Field("fieldGas")
    private Float fieldGas;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("pumpTime")
    private List<String> pumpTime;

    @Field("timeStart")
    private Long timeStart;

    @Field("timeEnd")
    private Long timeEnd;

    @Field("cng")
    private Float cng;

    @Field("jobs")
    private List<String> jobs = new ArrayList<>();

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public boolean isSkipStage() {
        return skipStage;
    }

    public void setSkipStage(boolean skipStage) {
        this.skipStage = skipStage;
    }

    public Long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public List<String> getPumpTime() {
        return pumpTime;
    }

    public void setPumpTime(List<String> pumpTime) {
        this.pumpTime = pumpTime;
    }

    public String getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(String stageNumber) {
        this.stageNumber = stageNumber;
    }

    public Float getTop() {
        return top;
    }

    public void setTop(Float top) {
        this.top = top;
    }

    public Float getBottom() {
        return bottom;
    }

    public void setBottom(Float bottom) {
        this.bottom = bottom;
    }

    public Float getTvd() {
        return tvd;
    }

    public void setTvd(Float tvd) {
        this.tvd = tvd;
    }

    public Float getPlug() {
        return plug;
    }

    public void setPlug(Float plug) {
        this.plug = plug;
    }

    public  Float getDiesel() {return diesel;}

    public void setDiesel(Float diesel){this.diesel = diesel;}

    public Float getFieldGas() { return fieldGas;}

    public void setFieldGas(Float fieldGas) { this.fieldGas = fieldGas; }

    public Float getCng() {
        return cng;
    }

    public void setCng(Float cng) {
        this.cng = cng;
    }

    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
}
}


