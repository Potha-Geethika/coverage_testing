package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class OperatorEndStageEmail extends Email {
    @Field("well")
    private String well;

    @Field("day")
    private Integer day;

    @Field("pad")
    private String pad;

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

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
    }

    public boolean sentBetween(long start, Long end) {
        if (end == null) {
            return this.getSentAt().getTime() >= start;
        }
        else {
            return this.getSentAt().getTime() >= start && this.getSentAt().getTime() < end;
        }
    }
}
