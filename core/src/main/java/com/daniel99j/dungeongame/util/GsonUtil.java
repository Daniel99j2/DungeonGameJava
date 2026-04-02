package com.daniel99j.dungeongame.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonUtil {
    public static final Gson PARSER = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static JsonObject parse(String data) {
        return JsonParser.parseString(data).getAsJsonObject();
    }
}
