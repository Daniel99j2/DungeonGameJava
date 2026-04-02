package com.daniel99j.dungeongame.util;

import com.badlogic.gdx.math.Vector2;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.AbstractObject;
import com.daniel99j.dungeongame.entity.StaticObject;
import com.daniel99j.dungeongame.world.Level;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class LevelLoader {
    public static Level loadFromData(String name) {
        try {
            String data = Files.readString(Paths.get(PathUtil.data("maps/"+name+".map")).toAbsolutePath());
            Level out = load(data);
            out.completedLoad();
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Level loadFromSave() {
        return null;
    }

    public static Level load(String string) {
        Level out = new Level();
        JsonObject levelObject = GsonUtil.parse(string);
        if(levelObject.get("data_version").getAsInt() != GameConstants.DATA_VERSION) throw new IllegalStateException("TODO");
        levelObject.get("objects").getAsJsonArray().forEach((jsonElement -> {
            createObject(jsonElement.getAsJsonObject(), out);

        }));
        return out;
    }

    public static AbstractObject createObject(JsonObject data, Level out) {
        String type = data.get("type").getAsString();
        AbstractObject object = null;

        JsonObject customData = data.get("custom_data").getAsJsonObject();
        if(type.equals("sprite")) {
            object = new StaticObject(customData.get("sprite").getAsString(), customData.get("scale").getAsFloat());
        } else if(type.equals("monster")) {

        }else if(type.equals("rar")) {

        } else if(type.equals("player")) {
            return null;
        } else {
            throw new IllegalStateException("Invalid type '"+type+"'");
        }

        object.init(out);

        object.setPos(new Vector2(data.get("x").getAsFloat(), data.get("y").getAsFloat()));
        object.setUuid(UUID.fromString(data.get("uuid").getAsString()));
        return object;
    }

    public static String saveLevel(Level level) {
        JsonObject out = new JsonObject();

        out.addProperty("data_version", GameConstants.DATA_VERSION);

        JsonArray objects = new JsonArray();
        for (AbstractObject allObject : level.getAllObjects()) {
            objects.add(allObject.write());
        }

        out.add("objects", objects);

        return GsonUtil.PARSER.toJson(out);
    }
}
