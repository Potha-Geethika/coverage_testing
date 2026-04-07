package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

public class Strap {
    @Id
    private String id;

    @Field("chemical")
    private Chemical chemical;

    @Field("start")
    private Float start;

    @Field("end")
    private Float end;

    @Field("used")
    private Float used;

    @Field("rawUsed")
    private Float rawUsed;

    @Field("hasDigitalGauge")
    private Boolean hasDigitalGauge;

    @Field("transportType")
    private String transportType;

    @Field("laDaNumber")
    private String laDaNumber;

    @Field("side")
    private String side;

    @Field("desiredStrength")
    private String desiredStrength;

    @Field("desiredItemCode")
    private String desiredItemCode;

    @Field("desiredDescription")
    private String desiredDescription;

    @Field("desireStrengthFromChemicalId")
    private String desireStrengthFromChemicalId;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("isCustomerSupplied")
    private boolean isCustomerSupplied;

    public boolean getIsCustomerSupplied() {
        return isCustomerSupplied;
    }

    public void setIsCustomerSupplied(boolean isCustomerSupplied) {
        this.isCustomerSupplied = isCustomerSupplied;
    }

    @Field("omitFromFT")
    private boolean omitFromFT;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Chemical getChemical() {
        return chemical;
    }

    public void setChemical(Chemical chemical) {
        this.chemical = chemical;
    }

    public Float getStart() {
        return start;
    }

    public void setStart(Float start) {
        this.start = start;
    }

    public Float getEnd() {
        return end;
    }

    public void setEnd(Float end) {
        this.end = end;
    }

    public Float getUsed() {
        return used;
    }

    public void setUsed(Float used) {
        this.used = used;
    }

    public Float getRawUsed() {
        return rawUsed;
    }

    public void setRawUsed(Float rawUsed) {
        this.rawUsed = rawUsed;
    }

    public Boolean getHasDigitalGauge() {
        return hasDigitalGauge;
    }

    public void setHasDigitalGauge(Boolean hasDigitalGauge) {
        this.hasDigitalGauge = hasDigitalGauge;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getLaDaNumber() {
        return laDaNumber;
    }

    public void setLaDaNumber(String laDaNumber) {
        this.laDaNumber = laDaNumber;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getDesiredStrength() {
        return desiredStrength;
    }

    public void setDesiredStrength(String desiredStrength) {
        this.desiredStrength = desiredStrength;
    }

    public String getDesiredItemCode() {
        return desiredItemCode;
    }

    public void setDesiredItemCode(String desiredItemCode) {
        this.desiredItemCode = desiredItemCode;
    }

    public String getDesiredDescription() {
        return desiredDescription;
    }

    public void setDesiredDescription(String desiredDescription) {
        this.desiredDescription = desiredDescription;
    }

    public String getDesireStrengthFromChemicalId() {
        return desireStrengthFromChemicalId;
    }

    public void setDesireStrengthFromChemicalId(String desireStrengthFromChemicalId) {
        this.desireStrengthFromChemicalId = desireStrengthFromChemicalId;
    }

    public String getName() {
        if (desiredStrength == null) {
            return chemical.getName();
        }
        else {
            String[] split = chemical.getName().split("%");
            if (split.length == 2) {
                return desiredStrength + "%" + split[1];
            }
            else {
                return chemical.getName();
            }
        }
    }

    public boolean isOmitFromFT() {
        return omitFromFT;
    }

    public void setOmitFromFT(boolean omitFromFT) {
        this.omitFromFT = omitFromFT;
    }
}
