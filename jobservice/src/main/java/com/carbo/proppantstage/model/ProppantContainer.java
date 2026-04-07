package com.carbo.proppantstage.model;

import com.carbo.ws.model.ConsumedContainer;
import com.carbo.job.model.ContainerType;
import com.carbo.job.model.Proppant;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Box.class, name="boxes"),
        @JsonSubTypes.Type(value = Silo.class, name="silos"),
        @JsonSubTypes.Type(value = Bin.class, name="bins"),
})
public abstract class ProppantContainer {
    @Id
    protected String id;

    @Field("proppant")
    protected Proppant proppant;

    protected List<Proppant> proppants;

    @Field("proppantMesh")
    protected Map<String,Float> proppantMesh;

    @Field("actualRun")
    protected Float actualRun;

    @Field("consumedContainer")
    protected ConsumedContainer consumedContainer;

    @Field("position")
    protected Integer position;

    @Field("type")
    protected ContainerType type;

    @Field("created")
    protected Long created = new Date().getTime();

    @Field("modified")
    protected Long modified  = new Date().getTime();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Proppant getProppant() {
        return proppant;
    }

    public void setProppant(Proppant proppant) {
        this.proppant = proppant;
    }

    public Float getActualRun() {
        return actualRun;
    }

    public void setActualRun(Float actualRun) {
        this.actualRun = actualRun;
    }

    public ConsumedContainer getConsumedContainer() {
        return consumedContainer;
    }

    public void setConsumedContainer(ConsumedContainer consumedContainer) {
        this.consumedContainer = consumedContainer;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public ContainerType getType() {
        return type;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }

    public List<Proppant> getProppants() {
        return proppants;
    }

    public void setProppants(List<Proppant> proppants) {
        this.proppants = proppants;
    }

    public Map<String, Float> getProppantMesh() {
        return proppantMesh;
    }

    public void setProppantMesh(Map<String, Float> proppantMesh) {
        this.proppantMesh = proppantMesh;
    }

}
