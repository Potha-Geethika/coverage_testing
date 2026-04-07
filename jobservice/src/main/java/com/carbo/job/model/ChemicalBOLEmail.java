package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class ChemicalBOLEmail extends Email {

    @Field("chemical")
    private String chemical;

    @Field("bol")
    private String bol;

    @Field("poNumber")
    private String poNumber;

    @Field("netReceived")
    private Float netReceived;

    @Field("bolQuantity")
    private Float bolQuantity;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

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

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Float getNetReceived() {
        return netReceived;
    }

    public void setNetReceived(Float netReceived) {
        this.netReceived = netReceived;
    }

    public Float getBolQuantity() {
        return bolQuantity;
    }

    public void setBolQuantity(Float bolQuantity) {
        this.bolQuantity = bolQuantity;
    }
}
