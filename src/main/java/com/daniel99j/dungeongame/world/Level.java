package com.daniel99j.dungeongame.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.AdvancedObject;
import com.daniel99j.dungeongame.entity.StaticObject;
import org.lwjgl.Sys;

import java.util.ArrayList;

public class Level implements Disposable {
    private final World box2dWorld;
    private float activeTimer;
    private ArrayList<AdvancedObject> advancedObjects = new ArrayList<>();
    private ArrayList<StaticObject> staticObjects = new ArrayList<>();

    public Level() {
        this.box2dWorld = new World(new Vector2(0, -0.1f), true);
    }

    public void tick(float deltaTime) {
        activeTimer += deltaTime;

        if(activeTimer > GameConstants.SECONDS_PER_TICK) while ((activeTimer-=GameConstants.SECONDS_PER_TICK) > 0) {
            this.box2dWorld.step(GameConstants.SECONDS_PER_TICK, 1, 1);
            tickWorld();
        }

        box2dWorld.createBody(new BodyDef());
    }

    public void tickWorld() {
        for (AdvancedObject advancedObject : this.advancedObjects) {
            advancedObject.tick();
        }
    }

    public void render() {
        for (StaticObject staticObject : this.staticObjects) {
            staticObject.render();
        }
        for (AdvancedObject advancedObject : this.advancedObjects) {
            advancedObject.render();
        }
    }

    @Override
    public void dispose() {
        this.box2dWorld.dispose();
    }

    public World getBox2dWorld() {
        return box2dWorld;
    }

    public ArrayList<AdvancedObject> getAdvancedObjects() {
        return advancedObjects;
    }

    public ArrayList<StaticObject> getStaticObjects() {
        return staticObjects;
    }
}
