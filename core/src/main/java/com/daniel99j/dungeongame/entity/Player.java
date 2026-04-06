package com.daniel99j.dungeongame.entity;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.daniel99j.dungeongame.world.Level;
import com.google.gson.JsonObject;
import org.lwjgl.Sys;

public class Player extends AdvancedObject {
    private PointLight light;

    @Override
    public void tick() {
        float speed = 100;
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

        if(movement.len() == 1.5) move/=2;
        this.getPhysics().applyForceToCenter(movement.x*move, movement.y*move, true);
        super.tick();

        if(GameConstants.DEBUGGING) {
            this.getPhysics().getFixtureList().get(0).getFilterData().maskBits = (short) (Debuggers.isEnabled("noclip") ? 0 : -1);
        }
    }

    @Override
    public void init(Level level) {
        super.init(level);
        this.light = level.addLight((handler) -> new PointLight(handler, 512, new Color(1,1,1,1f), 50,0,0));
        this.light.setStaticLight(false);
        this.light.setSoft(true);
        this.light.setSoftnessLength(5f);
        this.light.setContactFilter((short) 1, (short) 0, CollisionCategories.LIGHT_BLOCKING);
        this.light.attachToBody(this.getPhysics());
    }

    @Override
    public void dispose() {
        super.dispose();
        this.light.dispose();
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

    @Override
    public String getType() {
        return "player";
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
