package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "scheduler-email-details")
public class SchedulerEmailDetail {

    @Id
    private String id;

    @Field("chemicalDeliveryId")
    @Indexed(unique = false)
    private String chemicalDeliveryId;

    @Field("counter")
    private int counter;

    @Field("bolQuantity")
    private float bolQuantity;

    @Field("receivedQuantity")
    private float receivedQuantity;

    @Field("jobId")
    private String jobId;

    @Field("organisationId")
    private String organisationId;
    @Field("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChemicalDeliveryId() {
        return chemicalDeliveryId;
    }

    public void setChemicalDeliveryId(String chemicalDeliveryId) {
        this.chemicalDeliveryId = chemicalDeliveryId;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public float getBolQuantity() {
        return bolQuantity;
    }

    public void setBolQuantity(float bolQuantity) {
        this.bolQuantity = bolQuantity;
    }

    public float getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(float receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
}
