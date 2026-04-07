package com.carbo.job.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class Standby {
    @Id
    private String id;

    @Field("chemical")
    @NotEmpty(message = "chemical can not be empty")
    private String chemical;

    @Field("size")
    private Float size;

    @Field("full")
    private Float full;

    @Field("partial")
    private Float partial;

    @Field("isTote")
    private Boolean isTote;

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

    public String getChemical() {
        return chemical;
    }

    public void setChemical(String chemical) {
        this.chemical = chemical;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Float getFull() {
        return full;
    }

    public void setFull(Float full) {
        this.full = full;
    }

    public Float getPartial() {
        return partial;
    }

    public void setPartial(Float partial) {
        this.partial = partial;
    }

    public Boolean getTote() {
        return isTote;
    }

    public void setTote(Boolean tote) {
        isTote = tote;
    }
}
