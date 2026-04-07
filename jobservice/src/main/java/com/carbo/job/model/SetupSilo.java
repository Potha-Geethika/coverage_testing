package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@JsonTypeName("silos")
public class SetupSilo extends SetupContainer {
//    @Field("proppant")
//    private Proppant proppant;
//
//    @Field("position")
//    private String position;

    @Field("name")
    private String name;

    @Field("maxCapacity")
    private Float maxCapacity;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public SetupSilo() {
        super();
    }

    //    public String getPosition() {
//        return position;
//    }
//
//    public void setPosition(String position) {
//        this.position = position;
//    }
//
//    public Proppant getProppant() {
//        return proppant;
//    }
//
//    public void setProppant(Proppant proppant) {
//        this.proppant = proppant;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMaxCapacity() { return maxCapacity; }

    public void setMaxCapacity(Float maxCapacity) { this.maxCapacity = maxCapacity; }
}
