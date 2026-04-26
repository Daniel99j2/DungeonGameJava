package com.daniel99j.dungeongame.level;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.*;
import com.daniel99j.dungeongame.mechanic.RunManager;
import com.daniel99j.dungeongame.sounds.SoundManager;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class Level implements Disposable {
    private final World box2dWorld;
    private float activeTimer;
    private float tickTimer = 0;
    private final ArrayList<AdvancedObject> advancedObjects = new ArrayList<>();
    private final ArrayList<StaticObject> staticObjects = new ArrayList<>();
    private int time;
    public RayHandler rayHandler;
    private final ArrayList<LevelLight> lights = new ArrayList<>();
    protected final ArrayList<Vector2> treasurePositions = new ArrayList<>();
    protected final ArrayList<Vector2> frostPositions = new ArrayList<>();
    protected final ArrayList<Vector2> goalPositions = new ArrayList<>();
    private RunManager runManager = new RunManager();
    public final ArrayList<ParticleEffect> particles = new ArrayList<>();

    public Level() {
        this.box2dWorld = new World(new Vector2(0, 0), true);
        RayHandler.setGammaCorrection(true);
        this.rayHandler = new RayHandler(this.getBox2dWorld());
        this.rayHandler.setAmbientLight(0.001f);
        this.rayHandler.setBlurNum(3);
        RayHandler.useDiffuseLight(true);
        this.rayHandler.setShadows(true);

        SoundManager.getSound("test").playSingle(1, 1, 0, () -> {
            Logger.info("hi");
        });
        runManager.setTreasureToGenerate(100);
    }

    public void tick(float deltaTime) {
        activeTimer += deltaTime;

        if (activeTimer > GameConstants.SECONDS_PER_PHYSICS_TICK)
            while ((activeTimer -= GameConstants.SECONDS_PER_PHYSICS_TICK) > 0) {
                tickTimer+=GameConstants.SECONDS_PER_PHYSICS_TICK;
                if(tickTimer >= GameConstants.SECONDS_PER_TICK) {
                    tickWorld();
                    tickTimer = 0;
                }
                this.box2dWorld.step(GameConstants.SECONDS_PER_PHYSICS_TICK, 6, 2);
            }

        if(Debuggers.isEnabled("pathfindingRender")) {
            for (Map.Entry<String, Integer> entry : Debuggers.pathfindDebuggerTimers.entrySet()) {
                if (entry.getValue() <= 0) {
                    Debuggers.pathfindDebuggerTimers.remove(entry.getKey());
                    Debuggers.pathfindDebuggers.remove(entry.getKey());
                } else {
                    Debuggers.pathfindDebuggerTimers.replace(entry.getKey(), entry.getValue() - 1);
                }
            }
        }

        runManager.tick();
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

        for (ParticleEffect particle : new ArrayList<>(particles)) {
            if(particle.isComplete()) {
                particles.remove(particle);
                particle.dispose();
            }
            particle.draw(GameConstants.spriteBatch, Gdx.graphics.getDeltaTime());
        }
        GameConstants.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void dispose() {
        for (AbstractObject allObject : this.getAllObjects()) {
            allObject.dispose();
        }
        this.rayHandler.dispose();
        this.box2dWorld.dispose();

        GameConstants.player = null;

        if(GameConstants.level == this) GameConstants.level = null;
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
        object.init(this, false);
    }

    public void addObjectFromLoad(AbstractObject object) {
        //noinspection usagelimited
        object.init(this, true);
    }

    public void completedLoad() {
        for (AbstractObject o : this.getAllObjects()) {
            o.markFromWorldLoad();
        }
        for (Vector2 treasurePosition : this.treasurePositions) {
            this.addObject(new PositionMarker(treasurePosition, Color.YELLOW, treasurePositions::remove, treasurePositions::add,"treasure"));
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
        LevelLight<?> toRemove = null;
        for (LevelLight<?> levelLight : this.lights) {
            if(levelLight.light().equals(light)) {
                toRemove = levelLight;
            }
        }
        if(toRemove != null) removeLight(toRemove);
    }

    public void removeLight(LevelLight<?> light) {
        this.lights.remove(light);
        light.light().remove();
    }

    public ArrayList<Vector2> getTreasurePositions() {
        return treasurePositions;
    }
}
