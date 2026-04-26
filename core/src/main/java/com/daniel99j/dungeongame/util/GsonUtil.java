package com.daniel99j.dungeongame.util;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.*;

public class GsonUtil {
    public static final Gson PARSER = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static JsonObject parse(String data) {
        return JsonParser.parseString(data).getAsJsonObject();
    }

    public static JsonObject toJson(Vector2 v) {
        JsonObject out = new JsonObject();
        out.addProperty("x", v.x);
        out.addProperty("y", v.y);
        return out;
    }

    public static Vector2 fromJson(JsonElement jsonElement) {
        return new Vector2(jsonElement.getAsJsonObject().get("x").getAsFloat(), jsonElement.getAsJsonObject().get("y").getAsFloat());
    }
}
