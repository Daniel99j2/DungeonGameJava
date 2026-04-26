package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.daniel99j.djutil.NumberUtils;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.level.SaveConfig;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.GlobalRunnables;
import com.daniel99j.dungeongame.util.PathUtil;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.daniel99j.dungeongame.util.RenderUtil;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

public class TreasureSpawnerObject extends StaticObject {
    private final float chance;
    private final String spawnType;

    public TreasureSpawnerObject(float chance, String spawnType) {
        this.chance = chance;
        this.spawnType = spawnType;
    }

    public void fire() {
        if(this.spawnType.equals("treasure") && NumberUtils.getRandomFloat(0, 1) <= this.chance) {
            TreasureObject treasure = new TreasureObject(GlobalRunnables.COLLECT_TREASURE, "coin", Color.valueOf("#fcb603"));
            treasure.setPos(this.getPos().add(NumberUtils.getRandomFloat(-0.2f, 0.2f), NumberUtils.getRandomFloat(-0.2f, 0.2f)));
            this.getLevel().addObject(treasure);

            ParticleEffect effect = new ParticleEffect();
            effect.load(Gdx.files.internal(PathUtil.asset("game/test.p")), GameConstants.atlas);
            effect.setEmittersCleanUpBlendFunction(false);
            effect.scaleEffect(0.01f);
            effect.start();

            effect.setPosition(treasure.getPos().x, treasure.getPos().y);

            this.getLevel().particles.add(effect);
        }
    }

    @Override
    protected PhysicsSettings createPhysics() {
        return null;
    }

    @Override
    public void render() {

    }

    @Override
    public void writeAdditional(JsonObject object) {

    }

    @Override
    public ObjectType<?> getType() {
        return ObjectTypes.TREASURE_SPAWNER;
    }

    @Override
    public float getLayer() {
        return 0;
    }

    public static TreasureSpawnerObject read(JsonObject object) {
        return new TreasureSpawnerObject(object.get("chance").getAsFloat(), object.get("spawn_type").getAsString());
    }

    public String getSpawnType() {
        return spawnType;
    }
}
