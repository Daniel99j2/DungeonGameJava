package com.daniel99j.dungeongame.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.util.GsonUtil;
import com.daniel99j.dungeongame.util.PathUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class NinePatchLoader {
    private static final Map<String, NinePatch> patches = new HashMap<>();

    public static NinePatch getNinePatch(String name) {
        if(patches.containsKey(name)) return patches.get(name);
        try {
            JsonObject data = GsonUtil.parse(Files.readString(Paths.get(PathUtil.asset("game/"+name+".json")).toAbsolutePath()));
            NinePatch patch = new NinePatch(GameConstants.atlas.findRegion(name), data.get("left").getAsInt(), data.get("right").getAsInt(), data.get("top").getAsInt(), data.get("bottom").getAsInt());
            patches.put(name, patch);
            return patch;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
