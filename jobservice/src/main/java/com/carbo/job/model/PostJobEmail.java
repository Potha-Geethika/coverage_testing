package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class PostJobEmail extends Email {
    @Field("well")
    private String well;

    @Field("stage")
    private String stage;

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

    public boolean sentBetween(long start, Long end) {
        if (end == null) {
            return this.getSentAt().getTime() >= start;
        }
        else {
            return this.getSentAt().getTime() >= start && this.getSentAt().getTime() < end;
        }
    }
}
