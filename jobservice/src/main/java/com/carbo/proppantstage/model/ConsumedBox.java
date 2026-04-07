package com.carbo.proppantstage.model;

import com.carbo.ws.model.ConsumedContainer;
import org.springframework.data.mongodb.core.mapping.Field;

public class ConsumedBox extends ConsumedContainer {
    @Field("boxNumber")
    private String boxNumber;

    @Field("subBox")
    private String subBox;

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getSubBox() {
        return subBox;
    }

    public void setSubBox(String subBox) {
        this.subBox = subBox;
    }
}
