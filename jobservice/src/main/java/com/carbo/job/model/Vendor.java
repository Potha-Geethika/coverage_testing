package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "vendors")
public class Vendor {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Deprecated
    @Field("poNumbers")
    private List<String> poNumbers;

    @Field("contacts")
    private List<Contact> contacts;

    @Field("type")
    private String type;

    @Field("poNumberValues")
    private String poNumberValues;

    @Field("poItems")
    private List<PoItem> poItems;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();
    @Field("organizationId")
    private String organizationId;

    @Field("ts")
    private Long ts;

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<String> getPoNumbers() {
        return poNumbers;
    }

    public void setPoNumbers(List<String> poNumbers) {
        this.poNumbers = poNumbers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoNumberValues() {
        return poNumberValues;
    }

    public void setPoNumberValues(String poNumberValues) {
        this.poNumberValues = poNumberValues;
    }

    public List<PoItem> getPoItems() {
        return poItems;
    }

    public void setPoItems(List<PoItem> poItems) {
        this.poItems = poItems;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
