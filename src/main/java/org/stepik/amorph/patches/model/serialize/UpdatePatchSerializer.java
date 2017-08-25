package org.stepik.amorph.patches.model.serialize;

import com.google.gson.*;
import org.stepik.amorph.patches.model.UpdatePatch;

import java.lang.reflect.Type;

public class UpdatePatchSerializer implements JsonSerializer<UpdatePatch> {
    @Override
    public JsonElement serialize(UpdatePatch src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("update"));
        result.add("start", new JsonPrimitive(src.getStart()));
        result.add("stop", new JsonPrimitive(src.getStop()));
        result.add("value", new JsonPrimitive(src.getValue()));
        return result;
    }
}
