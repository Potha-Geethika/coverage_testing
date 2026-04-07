package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SetupSilo.class, name = "silos"),
        @JsonSubTypes.Type(value = SetupMover.class, name = "movers"),
        @JsonSubTypes.Type(value = SetupBin.class, name = "bins")
})
public abstract class SetupContainer {
    @Field("position")
    protected String position;

    @Field("proppant")
    protected Proppant proppant;

    @Field("proppants")
    protected List< Proppant> proppants;

    @Field("proppantMesh")
    protected Map<String,Float> proppantMesh;

    @Field("type")
    protected ContainerType type;

    public SetupContainer() {
    }
}
