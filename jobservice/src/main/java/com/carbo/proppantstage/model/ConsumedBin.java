package com.carbo.proppantstage.model;

import com.carbo.ws.model.ConsumedContainer;
import org.springframework.data.mongodb.core.mapping.Field;

public class ConsumedBin extends ConsumedContainer {
    @Field("moverNumber")
    private String moverNumber;

    @Field("binNumber")
    private String binNumber;

    public String getMoverNumber() {
        return moverNumber;
    }

    public void setMoverNumber(String moverNumber) {
        this.moverNumber = moverNumber;
    }

    public String getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }
}
