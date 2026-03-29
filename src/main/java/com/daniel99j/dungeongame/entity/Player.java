package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.lwjgl.Sys;

public class Player extends AdvancedObject {
    @Override
    public void tick() {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) this.setVelocity(new Vector2(1, 0));
        super.tick();
    }

    @Override
    protected PhysicsSettings createPhysics() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 2.0f);
        return new PhysicsSettings(BodyDef.BodyType.DynamicBody, shape, 1.0f);
    }
}
