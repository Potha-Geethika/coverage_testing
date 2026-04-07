package com.carbo.job.model;


import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Field;

public class CheckItem {
    @Field("name")
    private String name;
    @Field("description")
    private String description;
    @Field("completed")
    private Boolean completed;
    @Field("modified")
    private Long modified = (new Date()).getTime();
    @Field("ts")
    private Long ts;
    @Field("lastModifiedBy")
    private String lastModifiedBy;

    public CheckItem() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
