package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.ObjectUtils;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.Map;

@Document(collection = "organizations")
public class Organization {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    String name;

    @Field("logoId")
    String logoId;

    @Field("access")
    Map<Role,Boolean> access;

    @Field("logoFileName")
    String logoFileName;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();


    public Map<Role, Boolean> getAccess() {
        return access;
    }

    public void setAccess(Map<Role, Boolean> access) {
        this.access = access;
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

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    public String getLogoFileName() {
        return logoFileName;
    }

    public void setLogoFileName(String logoFileName) {
        this.logoFileName = logoFileName;
    }

    public boolean hasSalesAccess(){
        if(!ObjectUtils.isEmpty(this.access) && !this.access.isEmpty() && !ObjectUtils.isEmpty(this.access.get(Role.ROLE_SALES_USER))){
            return this.access.get(Role.ROLE_SALES_USER);
        }
        return false;
    }
}
