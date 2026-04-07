package com.carbo.job.adapters;

import com.carbo.job.model.*;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SetupContainerAdapter implements JsonDeserializer<SetupContainer> {
    @Override
    public SetupContainer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String containerType = jsonElement.getAsJsonObject().get("type").getAsString();

        switch (containerType) {
            case "movers":
                return jsonDeserializationContext.deserialize(jsonElement, SetupMover.class);
            case "bins":
                return jsonDeserializationContext.deserialize(jsonElement, SetupBin.class);
            case "silos":
                return jsonDeserializationContext.deserialize(jsonElement, SetupSilo.class);
            default:
                return jsonDeserializationContext.deserialize(jsonElement, type);
        }
    }
}
