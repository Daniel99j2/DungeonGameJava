package com.daniel99j.dungeongame.level;

import box2dLight.ConeLight;
import box2dLight.Light;
import com.daniel99j.djutil.UsageLimited;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.UUID;

public final class LevelLight<T extends Light> {
    private final T light;
    private final SaveConfig saveConfig;
    private UUID uuid;

    public LevelLight(T light, SaveConfig saveConfig, UUID uuid) {
        this.light = light;
        this.saveConfig = saveConfig;
        this.uuid = uuid;
    }

    public JsonObject write() {
        JsonObject out = new JsonObject();
        out.addProperty("uuid", uuid.toString());
        out.addProperty("saveConfig", saveConfig.toString());
        out.addProperty("lightClass", light.getClass().toString());
        out.addProperty("colour", light.getColor().toString());
        out.addProperty("direction", light.getDirection());
        out.addProperty("distance", light.getDistance());
        out.addProperty("xray", light.isXray());
        out.addProperty("static", light.isStaticLight());
        out.addProperty("active", light.isActive());
        out.addProperty("soft", light.isSoft());
        out.addProperty("softShadowLength", light.getSoftShadowLength());
        out.addProperty("rays", light.getRayNum());
        out.addProperty("x", light.getX());
        out.addProperty("y", light.getY());

        if (light instanceof ConeLight coneLight) out.addProperty("coneDegree", coneLight.getConeDegree());

        return out;
    }

    public T light() {
        return light;
    }

    public SaveConfig saveConfig() {
        return saveConfig;
    }

    public UUID uuid() {
        return uuid;
    }

    @UsageLimited
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LevelLight) obj;
        return Objects.equals(this.light, that.light) &&
            Objects.equals(this.saveConfig, that.saveConfig) &&
            Objects.equals(this.uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(light, saveConfig, uuid);
    }

    @Override
    public String toString() {
        return "LevelLight[" +
            "light=" + light + ", " +
            "saveConfig=" + saveConfig + ", " +
            "uuid=" + uuid + ']';
    }

}
