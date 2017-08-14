package org.stepik.amorph.actions.model.serialize;

import org.stepik.amorph.actions.model.Move;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MoveSerializer implements JsonSerializer<Move> {
    @Override
    public JsonElement serialize(Move src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive("move"));
        result.add("node", new JsonPrimitive(src.getNode().getPk()));
        result.add("parent", new JsonPrimitive(src.getParent().getPk()));
        result.add("pos", new JsonPrimitive(src.getPosition()));
        return result;
    }
}
