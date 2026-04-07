package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;

public class ProppantSandSieve {
    @Id
    private String id;

    @Field("proppant")
    @NotEmpty(message = "proppant can not be empty")
    private String proppant;

    @Field("date")
    private String date;

    @Field("screen1")
    private Screen1 screen1;

    @Field("screen2")
    private Screen2 screen2;

    @Field("screen3")
    private Screen3 screen3;

    @Field("screen4")
    private Screen4 screen4;

    @Field("screen5")
    private Screen5 screen5;

    @Field("screen6")
    private Screen6 screen6;

    @Field("pan")
    private Pan pan;

    @Field("jobId")
    private String jobId;

    @Field("wellId")
    private String wellId;

    @Field("stage")
    private String stage;

    @Field("total")
    private String total;

    @Field("result")
    private String result;

    @Field("weightPercentage")
    private String weightPercentage;

    @Field("organizationId")
    private String organizationId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();
    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("label")
    public String label;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProppant() {
        return proppant;
    }

    public void setProppant(String proppant) {
        this.proppant = proppant;
    }

    public Screen1 getScreen1() {
        return screen1;
    }

    public void setScreen1(Screen1 screen1) {
        this.screen1 = screen1;
    }

    public Screen2 getScreen2() {
        return screen2;
    }

    public void setScreen2(Screen2 screen2) {
        this.screen2 = screen2;
    }

    public Screen3 getScreen3() {
        return screen3;
    }

    public void setScreen3(Screen3 screen3) {
        this.screen3 = screen3;
    }

    public Screen4 getScreen4() {
        return screen4;
    }

    public void setScreen4(Screen4 screen4) {
        this.screen4 = screen4;
    }

    public Screen5 getScreen5() {
        return screen5;
    }

    public void setScreen5(Screen5 screen5) {
        this.screen5 = screen5;
    }

    public Screen6 getScreen6() {
        return screen6;
    }

    public void setScreen6(Screen6 screen6) {
        this.screen6 = screen6;
    }

    public Pan getPan() {
        return pan;
    }

    public void setPan(Pan pan) {
        this.pan = pan;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(String weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
