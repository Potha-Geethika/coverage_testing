package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class UpdateEmail extends Email {
    @Field("additionalComments")
    private String additionalComments;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

}
