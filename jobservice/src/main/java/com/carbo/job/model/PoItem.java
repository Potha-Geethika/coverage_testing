package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class PoItem {
    @Field("poNumber")
    private String poNumber;

    @Field("proppantId")
    private String proppantId;

    @Field("mileageItemCode")
    private String mileageItemCode;

    @Field("mileageValue")
    private String mileageValue;

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getProppantId() {
        return proppantId;
    }

    public void setProppantId(String proppantId) {
        this.proppantId = proppantId;
    }

    public String getMileageItemCode() {return mileageItemCode;}

    public void setMileageItemCode(String mileageItemCode) {this.mileageItemCode = mileageItemCode;}

    public String getMileageValue() {return mileageValue;}

    public void setMileageValue(String mileageValue) {this.mileageValue = mileageValue;}
}
