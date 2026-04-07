package com.carbo.job.model.threed;

import org.springframework.data.mongodb.core.mapping.Field;

public class RockType {

    @Field("tvd")
    private Float tvd;

    @Field("value")
    private Float value;

    @Field("name")
    private String name;

    public Float getTvd() {
        return tvd;
    }

    public void setTvd(Float tvd) {
        this.tvd = tvd;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
