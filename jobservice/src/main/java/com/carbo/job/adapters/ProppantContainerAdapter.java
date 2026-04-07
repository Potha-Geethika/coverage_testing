package com.carbo.job.adapters;

import com.carbo.proppantstage.model.Bin;
import com.carbo.proppantstage.model.Box;
import com.carbo.proppantstage.model.Silo;
import com.carbo.proppantstage.model.ProppantContainer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ProppantContainerAdapter implements JsonDeserializer<ProppantContainer> {
    @Override
    public ProppantContainer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String containerType = jsonElement.getAsJsonObject().get("type").getAsString();

        switch (containerType) {
            case "bins":
                return jsonDeserializationContext.deserialize(jsonElement, Bin.class);
            case "silos":
                return jsonDeserializationContext.deserialize(jsonElement, Silo.class);
            case "boxes":
                return jsonDeserializationContext.deserialize(jsonElement, Box.class);
            default:
                return jsonDeserializationContext.deserialize(jsonElement, type);
        }
    }
}
