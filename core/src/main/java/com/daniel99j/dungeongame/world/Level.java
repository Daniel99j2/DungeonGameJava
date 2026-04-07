package com.daniel99j.dungeongame.world;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

public class Level implements Disposable {
    private final World box2dWorld;
    private float activeTimer;
    private final ArrayList<AdvancedObject> advancedObjects = new ArrayList<>();
    private final ArrayList<StaticObject> staticObjects = new ArrayList<>();
    private int time;
    public RayHandler rayHandler;
    private final ArrayList<LevelLight> lights = new ArrayList<>();

    public Level() {
        this.box2dWorld = new World(new Vector2(0, 0), true);
        RayHandler.setGammaCorrection(true);
        this.rayHandler = new RayHandler(this.getBox2dWorld());
        this.rayHandler.setAmbientLight(0.001f);
        this.rayHandler.setBlurNum(3);
        RayHandler.useDiffuseLight(true);
        this.rayHandler.setShadows(true);
    }

    public void tick(float deltaTime) {
        activeTimer += deltaTime;

        if(activeTimer > GameConstants.SECONDS_PER_TICK) while ((activeTimer-=GameConstants.SECONDS_PER_TICK) > 0) {
            tickWorld();
            //this.box2dWorld.step(GameConstants.SECONDS_PER_TICK, 6, 2);
        }
        this.box2dWorld.step(deltaTime, 6, 2);
    }

    public void tickWorld() {
        time++;
        for (AdvancedObject advancedObject : this.advancedObjects) {
            advancedObject.tick();
        }
    }

    public void render() {
        ArrayList<AbstractObject> objects = getAllObjects();
        objects.sort((one, two) -> {
            float layer1 = one.getLayer();
            float layer2 = two.getLayer();
            if(layer1 == layer2) return 0;
            return Float.compare(layer1, layer2);
        });
        objects.forEach(AbstractObject::renderInternal);
    }

    @Override
    public void dispose() {
        for (AbstractObject allObject : this.getAllObjects()) {
            allObject.dispose();
        }
        for (LevelLight<?> light : this.getLights()) {
            light.light().dispose();
        }
        this.rayHandler.dispose();
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
        //noinspection usagelimited
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

    public ArrayList<LevelLight> getLights() {
        return this.lights;
    }

    public void removeObject(AbstractObject object) {
        object.dispose();
        if(object instanceof AdvancedObject) this.advancedObjects.remove(object);
        if(object instanceof StaticObject) this.staticObjects.remove(object);
    }

    public <T extends Light> LevelLight<T> addLight(Function<RayHandler, T> function, SaveConfig saveConfig) {
        T light = function.apply(this.rayHandler);
        light.setContactFilter((short) 1, (short) 0, CollisionCategories.LIGHT_BLOCKING);
        LevelLight<T> levelLight = new LevelLight<>(light, saveConfig, UUID.randomUUID());
        this.lights.add(levelLight);
        return levelLight;
    }

    public void removeLight(Light light) {
        for (LevelLight<?> levelLight : this.lights) {
            if(levelLight.light().equals(light)) {
                removeLight(levelLight);
            }
        }
    }

    public void removeLight(LevelLight<?> light) {
        this.lights.remove(light);
        light.light().remove();
    }
}
