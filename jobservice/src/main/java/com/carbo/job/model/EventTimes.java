package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class EventTimes {
    @Field("enRouteTime")
    private Long enRouteTime;
    @Field("onSiteTime")
    private Long onSiteTime;
    @Field("deliveredTime")
    private Long deliveredTime;
    @Field("acceptedIntoDeliveryTime")
    private Long acceptedIntoDeliveryTime;

    public Long getEnRouteTime() {
        return enRouteTime;
    }

    public void setEnRouteTime(Long enRouteTime) {
        this.enRouteTime = enRouteTime;
    }

    public Long getOnSiteTime() {
        return onSiteTime;
    }

    public void setOnSiteTime(Long onSiteTime) {
        this.onSiteTime = onSiteTime;
    }

    public Long getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Long deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public Long getAcceptedIntoDeliveryTime() {
        return acceptedIntoDeliveryTime;
    }

    public void setAcceptedIntoDeliveryTime(Long acceptedIntoDeliveryTime) {
        this.acceptedIntoDeliveryTime = acceptedIntoDeliveryTime;
    }
}

