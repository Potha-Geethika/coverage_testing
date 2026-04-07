package com.carbo.job.model;


import org.springframework.data.mongodb.core.mapping.Field;

public class Pan {
    @Field("weight")
    private String weight;

    @Field("weightRetained")
    private  String weightRetained;

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightRetained() {
        return weightRetained;
    }

    public void setWeightRetained(String weightRetained) {
        this.weightRetained = weightRetained;
    }
}
