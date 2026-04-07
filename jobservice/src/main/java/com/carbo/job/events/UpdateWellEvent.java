package com.carbo.job.events;

import com.carbo.job.events.model.WellChangeModel;

public class UpdateWellEvent {
    private WellChangeModel wellChangeModel;

    public UpdateWellEvent(WellChangeModel wellChangeModel) {
        this.wellChangeModel = wellChangeModel;
    }

    public WellChangeModel getWellChangeModel() {
        return wellChangeModel;
    }
}
