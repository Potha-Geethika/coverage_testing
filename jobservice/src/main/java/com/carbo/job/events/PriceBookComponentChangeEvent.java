package com.carbo.job.events;

import com.carbo.job.events.model.PriceBookComponentNameChangeModel;
import org.springframework.context.ApplicationEvent;

public class PriceBookComponentChangeEvent extends ApplicationEvent {

    private final PriceBookComponentNameChangeModel changeModel;

    public PriceBookComponentChangeEvent(Object source, PriceBookComponentNameChangeModel changeModel) {
        super(source);
        this.changeModel = changeModel;
    }

    public PriceBookComponentNameChangeModel getChangeModel() {
        return changeModel;
    }
}