package com.carbo.job.model.fracpro;

public class FracProTreatmentFluid {
    private Integer fracproTreatmentId;
    private String name;
    private Float volume;

    public Integer getFracproTreatmentId() {
        return fracproTreatmentId;
    }

    public void setFracproTreatmentId(Integer fracproTreatmentId) {
        this.fracproTreatmentId = fracproTreatmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }
}
