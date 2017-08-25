package org.stepik.amorph.patches.model.serialize;

import com.google.gson.*;
import org.stepik.amorph.patches.model.DeletePatch;

import java.lang.reflect.Type;

public class DeletePatchSerializer implements JsonSerializer<DeletePatch> {
    @Override
    public JsonElement serialize(DeletePatch src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("delete"));
        result.add("start", new JsonPrimitive(src.getStart()));
        result.add("stop", new JsonPrimitive(src.getStop()));
        return result;
    }
}
