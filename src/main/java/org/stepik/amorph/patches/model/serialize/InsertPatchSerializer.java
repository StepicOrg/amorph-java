package org.stepik.amorph.patches.model.serialize;

import com.google.gson.*;
import org.stepik.amorph.patches.model.InsertPatch;

import java.lang.reflect.Type;

public class InsertPatchSerializer implements JsonSerializer<InsertPatch> {
    @Override
    public JsonElement serialize(InsertPatch src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("insert"));
        result.add("pos", new JsonPrimitive(src.getPos()));
        result.add("text", new JsonPrimitive(src.getText()));
        return result;
    }
}
