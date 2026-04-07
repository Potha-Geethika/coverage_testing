package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonTypeName("bins")
public class SetupBin extends SetupContainer {
    @Field("moverNumber")
    private String moverNumber;

    public SetupBin() {
        super();
    }

    public String getMoverNumber() {
        return moverNumber;
    }

    public void setMoverNumber(String moverNumber) {
        this.moverNumber = moverNumber;
    }
}
