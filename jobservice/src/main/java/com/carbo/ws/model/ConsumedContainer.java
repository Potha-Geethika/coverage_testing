package com.carbo.ws.model;

import com.carbo.job.model.ContainerType;
import com.carbo.proppantstage.model.ConsumedBin;
import com.carbo.proppantstage.model.ConsumedBox;
import com.carbo.proppantstage.model.ConsumedSilo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConsumedBox.class, name="boxes"),
        @JsonSubTypes.Type(value = ConsumedSilo.class, name="silos"),
        @JsonSubTypes.Type(value = ConsumedBin.class, name="bins"),
})
public abstract class ConsumedContainer {
    @Id
    private String id;

    @Field("proppant")
    private String proppant;

    @Field("bol")
    private String bol;

    @Field("initialWtAmount")
    private Float initialWtAmount;

    @Field("wtAmount")
    private Float wtAmount;

    @Field("partial")
    private String partial;

    @Field("used")
    private Boolean used;

    @Field("type")
    protected ContainerType type;

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

    public String getProppant() {
        return proppant;
    }

    public void setProppant(String proppant) {
        this.proppant = proppant;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Float getInitialWtAmount() {
        return initialWtAmount;
    }

    public void setInitialWtAmount(Float initialWtAmount) {
        this.initialWtAmount = initialWtAmount;
    }

    public Float getWtAmount() {
        return wtAmount;
    }

    public void setWtAmount(Float wtAmount) {
        this.wtAmount = wtAmount;
    }

    public String getPartial() {
        return partial;
    }

    public void setPartial(String partial) {
        this.partial = partial;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public ContainerType getType() {
        return type;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }
}
