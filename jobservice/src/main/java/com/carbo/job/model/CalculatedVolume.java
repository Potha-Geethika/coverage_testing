package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class CalculatedVolume {

    @Field("perStage")
    private Map<String, Double> perStage;

    @Field("perWell")
    private Map<String, Double> perWell;

    @Field("perPad")
    private Map<String, Double> perPad;

    public Map<String, Double> getPerStage() {
        return perStage;
    }

    public void setPerStage(Map<String, Double> perStage) {
        this.perStage = perStage;
    }

    public Map<String, Double> getPerWell() {
        return perWell;
    }

    public void setPerWell(Map<String, Double> perWell) {
        this.perWell = perWell;
    }

    public Map<String, Double> getPerPad() {
        return perPad;
    }

    public void setPerPad(Map<String, Double> perPad) {
        this.perPad = perPad;
    }
}
