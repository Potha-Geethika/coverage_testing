package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("movers")
public class SetupMover extends SetupContainer {
    @Field("moverNumber")
    private String moverNumber;

    @Field("bins")
    private List<SetupBin> bins = new ArrayList<>();

    public SetupMover() {
        super();
    }

    public String getMoverNumber() {
        return moverNumber;
    }

    public void setMoverNumber(String moverNumber) {
        this.moverNumber = moverNumber;
    }

    public List<SetupBin> getBins() {
        return bins;
    }

    public void setBins(List<SetupBin> bins) {
        this.bins = bins;
    }
}
