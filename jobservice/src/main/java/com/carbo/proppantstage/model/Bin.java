package com.carbo.proppantstage.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Bin extends ProppantContainer {
    @Field("total")
    private Float total;

    @Field("designVolume")
    private Float designVolume;

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getDesignVolume() {
        return designVolume;
    }

    public void setDesignVolume(Float designVolume) {
        this.designVolume = designVolume;
    }
}
