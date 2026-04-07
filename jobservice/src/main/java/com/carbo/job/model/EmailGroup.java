package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "email-groups")
public class EmailGroup {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    private String name;

    @Field("organizationId")
    private String organizationId;

    @Field("contacts")
    private List<Contact> contacts = new ArrayList<>();

    @Field("created")
    private Long created = (new Date().getTime()) / 1000;

    @Field("modified")
    private Long modified = (new Date().getTime()) / 1000;

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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String toEmailTo() {
        return contacts.stream().map(each -> each.getEmail()).collect(Collectors.joining(";"));
    }
}
