package org.stepik.amorph.actions.model.serialize;

import org.stepik.amorph.actions.model.Update;
import com.google.gson.*;

import java.lang.reflect.Type;

public class UpdateSerializer implements JsonSerializer<Update> {
    @Override
    public JsonElement serialize(Update src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("update"));
        result.add("node", new JsonPrimitive(src.getNode().getPk()));
        result.add("props", context.serialize(src.getProps()));
        return result;
    }
}
