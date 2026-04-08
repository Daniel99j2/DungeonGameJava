package com.daniel99j.dungeongame.level;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.*;
import com.daniel99j.dungeongame.util.GsonUtil;
import com.daniel99j.dungeongame.util.PathUtil;
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
        levelObject.get("lights").getAsJsonArray().forEach((jsonElement -> {
            createLight(jsonElement.getAsJsonObject(), out);
        }));
        return out;
    }

    public static AbstractObject createObject(JsonObject data, Level out) {
        AbstractObject object;

        JsonObject customData = data.get("custom_data").getAsJsonObject();
        object = ObjectTypes.types.get(data.get("type").getAsString()).reader().apply(customData);
        out.addObjectFromLoad(object);

        AbstractObject.readBasic(object, data);

        object.onAdd(true);
        return object;
    }

    public static void createLight(JsonObject data, Level out) {
        String type = data.get("lightClass").getAsString().replace("class box2dLight.", "");
        UUID uuid = UUID.fromString(data.get("uuid").getAsString());
        Color colour = Color.valueOf(data.get("colour").getAsString());
        float direction = data.get("direction").getAsFloat();
        float distance = data.get("distance").getAsFloat();
        boolean xray = data.get("xray").getAsBoolean();
        boolean active = data.get("active").getAsBoolean();
        boolean _static = data.get("static").getAsBoolean();
        boolean soft = data.get("soft").getAsBoolean();
        float softShadowLength = data.get("softShadowLength").getAsFloat();
        int rays = data.get("rays").getAsInt();
        float x = data.get("x").getAsFloat();
        float y = data.get("y").getAsFloat();
        SaveConfig saveConfig = SaveConfig.valueOf(data.get("saveConfig").getAsString());

        LevelLight<?> levelLight;

        if(type.equals("PointLight")) {
            levelLight = out.addLight((rayHandler -> new PointLight(rayHandler, rays, colour, distance, x, y)), saveConfig);
        } else if(type.equals("ConeLight")) {
            levelLight = out.addLight((rayHandler -> new ConeLight(rayHandler, rays, colour, distance, x, y, direction, data.get("coneDegree").getAsFloat())), saveConfig);
        } else if(type.equals("DirectionalLight")) {
            levelLight = out.addLight((rayHandler -> new DirectionalLight(rayHandler, rays, colour, direction)), saveConfig);
        } else {
            throw new IllegalStateException("Unknown light type");
        }

        //noinspection usagelimited
        levelLight.setUuid(uuid);

        levelLight.light().setActive(active);
        levelLight.light().setXray(xray);
        levelLight.light().setSoft(soft);
        levelLight.light().setStaticLight(_static);
        levelLight.light().setSoftnessLength(softShadowLength);
    }

    public static String saveLevel(Level level) {
        JsonObject out = new JsonObject();

        out.addProperty("data_version", GameConstants.DATA_VERSION);

        JsonArray objects = new JsonArray();
        for (AbstractObject allObject : level.getAllObjects()) {
            objects.add(allObject.write());
        }

        out.add("objects", objects);

        JsonArray lights = new JsonArray();
        for (LevelLight<?> levelLight : level.getLights()) {
            //TODO: FIX
            if(levelLight.saveConfig().equals(SaveConfig.ALWAYS)) lights.add(levelLight.write());
        }

        out.add("lights", lights);

        return GsonUtil.PARSER.toJson(out);
    }
}
