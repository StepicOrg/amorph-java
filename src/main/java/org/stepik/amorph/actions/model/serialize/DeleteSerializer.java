package org.stepik.amorph.actions.model.serialize;

import org.stepik.amorph.actions.model.Delete;
import com.google.gson.*;

import java.lang.reflect.Type;

public class DeleteSerializer implements JsonSerializer<Delete> {
    @Override
    public JsonElement serialize(Delete src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("delete"));
        result.add("node", new JsonPrimitive(src.getNode().getPk()));
        return result;
    }
}
