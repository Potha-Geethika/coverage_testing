package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Screen6 {

    @Field("usStandardSieveNo")
    private String usStandardSieveNo;

    @Field("weight")
    private String weight;

    @Field("weightRetained")
    private String weightRetained;

    public String getUsStandardSieveNo() {
        return usStandardSieveNo;
    }

    public void setUsStandardSieveNo(String usStandardSieveNo) {
        this.usStandardSieveNo = usStandardSieveNo;
    }

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
