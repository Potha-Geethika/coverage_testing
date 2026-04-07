package com.carbo.job.model.widget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialData {

    @Field ("materialName")
    private String materialName;

    @Field ("neededPerUnit")
    private float neededPerUnit=0.0f;

    @Field ("neededTotal")
    private float neededTotal=0.0f;

    @Field ("materialType")
    private MaterialEnum materialType;

    @Field("unit")
    private UnitNeededEnum unit;

    @Field("code")
    private String code;
}
