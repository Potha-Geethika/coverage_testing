package com.carbo.job.events;

import com.carbo.job.events.model.PadChangeModel;

public class UpdateJobStartDateEvent {
    private PadChangeModel padChangeModel;

    public UpdateJobStartDateEvent(PadChangeModel padChangeModel) { this.padChangeModel = padChangeModel; }

    public PadChangeModel getPadChangeModel() { return padChangeModel; }
}
