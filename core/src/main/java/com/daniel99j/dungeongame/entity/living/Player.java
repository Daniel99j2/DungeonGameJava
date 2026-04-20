package com.daniel99j.dungeongame.entity.living;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.djutil.NumberUtils;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.*;
import com.daniel99j.dungeongame.sounds.SoundManager;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.GlobalRunnables;
import com.daniel99j.dungeongame.util.Logger;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.daniel99j.dungeongame.level.SaveConfig;
import com.daniel99j.dungeongame.util.ScheduledRunnables;
import com.google.gson.JsonObject;
import org.lwjgl.Sys;

public class Player extends AdvancedObject {
    public static float MAX_HEALTH = 100.0f;

    private PointLight light;
    public float health = MAX_HEALTH;

    @Override
    public void tick() {
        float speed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 6 : 4;
        float move = Math.max(speed-this.getVelocity().len(), 0);

        Vector2 movement = new Vector2(0, 0);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.add(0, 1);
        };
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.add(-1, 0);
        };
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.add(0, -1);
        };
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.add(1, 0);
        };

        //diagonal isnt faster
        movement.nor();

        if(Debuggers.isEnabled("freecam")) {
            float mul = 0.25f;
            Debuggers.freecam.add(new Vector2(movement.x*mul, movement.y*mul));
        }
        else if(movement.len() > 0) this.getPhysics().setLinearVelocity(movement.x*move, movement.y*move);
        super.tick();

        if(GameConstants.DEBUGGING) {
            this.getPhysics().getFixtureList().get(0).getFilterData().maskBits = (short) (Debuggers.isEnabled("noclip") ? 0 : -1);
        }

        if(NumberUtils.getRandomInt(0, 50) == 0) {
        float m = NumberUtils.getRandomFloat(0.9f, 1.1f);
        float r = NumberUtils.getRandomFloat(-0.1f, 0.1f);
        this.light.setColor(new Color(0x1f/255.0f,0x10/255.0f,0x06/255.0f,1).mul(m+r, m+(r/2), m, 1));
        }
    }

    @Override
    public void onAdd(boolean fromLoad) {
        super.onAdd(fromLoad);
        //#1F1006
        this.light = this.getLevel().addLight((handler) -> new PointLight(handler, 512, new Color(0x1f/255.0f,0x10/255.0f,0x06/255.0f,1), 9.315f,0,0), SaveConfig.NEVER).light();
        this.light.setStaticLight(false);
        this.light.setSoft(true);
        this.light.setSoftnessLength(2.34f);
        this.light.setContactFilter((short) 1, (short) 0, CollisionCategories.LIGHT_BLOCKING);
        this.light.attachToBody(this.getPhysics());
        GameConstants.player = this;
    }

    @Override
    public void dispose() {
        this.getLevel().removeLight(this.light);
        super.dispose();
    }

    @Override
    protected PhysicsSettings createPhysics() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 2.0f);
        return new PhysicsSettings(BodyDef.BodyType.DynamicBody, shape, 1.0f, 1.1f);
    }

    @Override
    public void writeAdditional(JsonObject object) {

    }

    public static Player read(JsonObject object) {
        return new Player();
    }

    public void damage(float amount) {
        health-=amount;
        SoundManager.getSound("hurt").play(1);
        if(health <= 0) {
            ScheduledRunnables.add(GlobalRunnables.FAIL_RUN);
        }
    }

    @Override
    public ObjectType<Player> getType() {
        return ObjectTypes.PLAYER;
    }

    @Override
    public float getLayer() {
        return RenderLayer.PLAYER;
    }

    @Override
    public String toString() {
        return "Player";
    }
}
