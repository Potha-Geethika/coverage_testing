package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ChannelConfig {
    @Field("maxSurfacePressure")
    private Float maxSurfacePressure;

    @Field("maxSlurryRate")
    private Float maxSlurryRate;

    @Field("maxPropConc")
    private Float maxPropConc;

    public Float getMaxSurfacePressure() {
        return maxSurfacePressure;
    }

    public void setMaxSurfacePressure(Float maxSurfacePressure) {
        this.maxSurfacePressure = maxSurfacePressure;
    }

    public Float getMaxSlurryRate() {
        return maxSlurryRate;
    }

    public void setMaxSlurryRate(Float maxSlurryRate) {
        this.maxSlurryRate = maxSlurryRate;
    }

    public Float getMaxPropConc() {
        return maxPropConc;
    }

    public void setMaxPropConc(Float maxPropConc) {
        this.maxPropConc = maxPropConc;
    }
}
