package org.stepik.amorph.actions.model.serialize;

import org.stepik.amorph.actions.model.Insert;
import com.google.gson.*;

import java.lang.reflect.Type;

public class InsertSerializer implements JsonSerializer<Insert> {
    @Override
    public JsonElement serialize(Insert src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("insert"));
        result.add("node", new JsonPrimitive(src.getNode().getPk()));
        result.add("parent", new JsonPrimitive(src.getParent().getPk()));
        result.add("pos", new JsonPrimitive(src.getPosition()));
        return result;
    }
}
