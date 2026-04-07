package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class BucketTestItem {
    @Id
    private String id;

    @Field("chemAddPump")
    private String chemAddPump;

    @Field("chemical")
    private String chemical;

    @Field("testedCleanRate")
    private Float testedCleanRate;

    @Field("concentration")
    private Float concentration;

    @Field("targetGallons")
    private Float targetGallons;

    @Field("targetRate")
    private Float targetRate;

    @Field("designTimeSecs")
    private Float designTimeSecs;

    @Field("actualTimeSecs")
    private Float actualTimeSecs;

    @Field("startPumpFactorPpu")
    private Float startPumpFactorPpu;

    @Field("calculatedAdjustmentPercent")
    private Float calculatedAdjustmentPercent;

    @Field("adjustedPumpFactorPpu")
    private Float adjustedPumpFactorPpu;

    @Field("comments")
    private String comments;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getChemAddPump() {
        return chemAddPump;
    }

    public void setChemAddPump(String chemAddPump) {
        this.chemAddPump = chemAddPump;
    }

    public String getChemical() {
        return chemical;
    }

    public void setChemical(String chemical) {
        this.chemical = chemical;
    }

    public Float getTestedCleanRate() {
        return testedCleanRate;
    }

    public void setTestedCleanRate(Float testedCleanRate) {
        this.testedCleanRate = testedCleanRate;
    }

    public Float getConcentration() {
        return concentration;
    }

    public void setConcentration(Float concentration) {
        this.concentration = concentration;
    }

    public Float getTargetGallons() {
        return targetGallons;
    }

    public void setTargetGallons(Float targetGallons) {
        this.targetGallons = targetGallons;
    }

    public Float getTargetRate() {
        return targetRate;
    }

    public void setTargetRate(Float targetRate) {
        this.targetRate = targetRate;
    }

    public Float getDesignTimeSecs() {
        return designTimeSecs;
    }

    public void setDesignTimeSecs(Float designTimeSecs) {
        this.designTimeSecs = designTimeSecs;
    }

    public Float getActualTimeSecs() {
        return actualTimeSecs;
    }

    public void setActualTimeSecs(Float actualTimeSecs) {
        this.actualTimeSecs = actualTimeSecs;
    }

    public Float getStartPumpFactorPpu() {
        return startPumpFactorPpu;
    }

    public void setStartPumpFactorPpu(Float startPumpFactorPpu) {
        this.startPumpFactorPpu = startPumpFactorPpu;
    }

    public Float getCalculatedAdjustmentPercent() {
        return calculatedAdjustmentPercent;
    }

    public void setCalculatedAdjustmentPercent(Float calculatedAdjustmentPercent) {
        this.calculatedAdjustmentPercent = calculatedAdjustmentPercent;
    }

    public Float getAdjustedPumpFactorPpu() {
        return adjustedPumpFactorPpu;
    }

    public void setAdjustedPumpFactorPpu(Float adjustedPumpFactorPpu) {
        this.adjustedPumpFactorPpu = adjustedPumpFactorPpu;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
