package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

public class ChildOpsActivityCode {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    private String name;

    private List<ChildOpsActivityCode> children;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

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

    public List<ChildOpsActivityCode> getChildren() {
        return children;
    }

    public void setChildren(List<ChildOpsActivityCode> children) {
        this.children = children;
    }
}
