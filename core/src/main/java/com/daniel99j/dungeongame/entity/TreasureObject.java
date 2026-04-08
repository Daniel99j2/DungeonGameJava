package com.daniel99j.dungeongame.entity;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.level.SaveConfig;
import com.daniel99j.dungeongame.util.GlobalRunnables;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.google.gson.JsonObject;

public class TreasureObject extends AdvancedObject {
    private final Runnable onPickup;
    private final String sprite;
    private final Color colour;
    private PointLight glow;

    public TreasureObject(Runnable onPickup, String sprite, Color colour) {
        this.onPickup = onPickup;
        this.sprite = sprite;
        this.colour = colour;
        this.setSaveConfig(SaveConfig.BETWEEN_SESSIONS);
    }

    @Override
    public void setPos(Vector2 pos) {
        super.setPos(pos);
        if(this.glow != null) this.glow.setPosition(this.getPos().add(0.5f, 0.5f));
    }

    @Override
    public void onAdd(boolean fromLoad) {
        super.onAdd(fromLoad);
        this.glow = this.getLevel().addLight((handler) -> new PointLight(handler, 10, Color.valueOf("#FFAF0065"), 2.46f, this.getPos().x, this.getPos().y), SaveConfig.NEVER).light();
        this.glow.setStaticLight(false);
        this.glow.setXray(true);
    }

    @Override
    public void render() {
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion(sprite), this.getPos().x, this.getPos().y, 1, 1);
    }

    @Override
    public void writeAdditional(JsonObject object) {
        object.addProperty("sprite", sprite);
        object.addProperty("runnableName", GlobalRunnables.codeToName.get(onPickup));
        object.addProperty("colour", this.colour.toString());
    }

    public static TreasureObject read(JsonObject object) {
        return new TreasureObject(GlobalRunnables.nameToCode.get(object.get("runnableName").getAsString()), object.get("sprite").getAsString(), Color.valueOf(object.get("colour").getAsString()));
    }

    @Override
    public ObjectType<TreasureObject> getType() {
        return ObjectTypes.TREASURE;
    }

    @Override
    public float getLayer() {
        return RenderLayer.COLLECTABLES;
    }

    @Override
    public void dispose() {
        this.getLevel().removeLight(this.glow);
        super.dispose();
    }

    @Override
    protected PhysicsSettings createPhysics() {
        return null;
    }
}
