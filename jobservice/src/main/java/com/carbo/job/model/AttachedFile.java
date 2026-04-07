package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AttachedFile {
    @Field("name")
    private String name;

    @Field("size")
    private Float size;

    public AttachedFile() {
    }

    public AttachedFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }
}
