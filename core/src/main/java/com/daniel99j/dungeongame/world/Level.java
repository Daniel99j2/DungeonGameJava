package com.daniel99j.dungeongame.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.AbstractObject;
import com.daniel99j.dungeongame.entity.AdvancedObject;
import com.daniel99j.dungeongame.entity.StaticObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Level implements Disposable {
    private final World box2dWorld;
    private float activeTimer;
    private ArrayList<AdvancedObject> advancedObjects = new ArrayList<>();
    private ArrayList<StaticObject> staticObjects = new ArrayList<>();
    private int time;

    public Level() {
        this.box2dWorld = new World(new Vector2(0, 0), true);
    }

    public void tick(float deltaTime) {
        activeTimer += deltaTime;

        if(activeTimer > GameConstants.SECONDS_PER_TICK) while ((activeTimer-=GameConstants.SECONDS_PER_TICK) > 0) {
            tickWorld();
            this.box2dWorld.step(GameConstants.SECONDS_PER_TICK, 6, 2);
        }
    }

    public void tickWorld() {
        time++;
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

    public ArrayList<AbstractObject> getAllObjects() {
        ArrayList<AbstractObject> objects = new ArrayList<>();
        objects.addAll(getAdvancedObjects());
        objects.addAll(getStaticObjects());
        return objects;
    }

    public void addObject(AbstractObject object) {
        object.init(this);
    }

    public void completedLoad() {
        for (AbstractObject o : this.getAllObjects()) {
            o.markFromWorldLoad();
        }
    }

    public int getTime() {
        return time;
    }

    public @Nullable AbstractObject getObjectByUUID(UUID uuid) {
        return this.getAllObjects().stream().filter((object -> object.getUUID() == uuid)).findFirst().orElse(null);
    }
}
