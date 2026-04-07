package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Document(collection = "operators")
@CompoundIndex(def = "{'organizationId': 1, 'name': 1}", name = "organization_name_index", unique = true)
public class Operator {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    String name;
    @Field("companyId")
    String companyId;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("ts")
    private Long ts;

    @Field("linkedOrganizationId")
    private String linkedOrganizationId;

    @Field("organizationId")
    private String organizationId;

    @Field("operatorId")
    private String operatorId;

    public String getId() {
        return id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
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

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getLinkedOrganizationId() {
        return linkedOrganizationId;
    }

    public void setLinkedOrganizationId(String linkedOrganizationId) {
        this.linkedOrganizationId = linkedOrganizationId;
    }

    public Long getCreated() {
        return created;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }


    public Operator() {
    }

    public Operator(String organizationId,String operatorName,String companyId,long timeStamp){
        this.organizationId=organizationId;
        this.name = operatorName;
        this.ts = timeStamp;
        this.companyId = companyId;
    }
}
