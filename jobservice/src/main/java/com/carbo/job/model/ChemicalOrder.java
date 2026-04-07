package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class ChemicalOrder extends Email {
    @Field("vendor")
    private String vendor;

    @Field("chemical")
    private String chemical;

    @Field("poNumber")
    private String poNumber;

    @Field("deliveredAt")
    private String deliveredAt;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getChemical() {
        return chemical;
    }

    public void setChemical(String chemical) {
        this.chemical = chemical;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(String deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
