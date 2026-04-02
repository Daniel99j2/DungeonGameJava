package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.dungeongame.world.Level;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractObject implements Disposable {
    private Level level;
    private Body physics;
    private boolean fromWorldLoad = false;
    private UUID uuid;

    public AbstractObject() {
    }

    public void init(Level level) {
        this.level = level;
        PhysicsSettings settings = createPhysics();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = settings.bodyType();
        this.physics = this.level.getBox2dWorld().createBody(bodyDef);
        this.physics.setLinearDamping(settings.drag()*10);
        this.physics.setFixedRotation(true);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = settings.shape();
        fixtureDef.density = settings.density();
        this.physics.createFixture(fixtureDef);
        settings.shape().dispose();
        this.uuid = UUID.randomUUID();
    }

    protected abstract PhysicsSettings createPhysics();

    @Override
    public void dispose() {

    }

    public @NotNull Level getLevel() {
        return level;
    }

    public abstract void render();

    public Vector2 getPos() {
        return this.physics.getPosition().cpy();
    }

    public void setPos(Vector2 pos) {
        this.physics.setTransform(pos.x, pos.y, 0);
    }

    public void setX(float x) {
        this.setPos(new Vector2(x, this.getPos().y));
    }

    public void setY(float y) {
        this.setPos(new Vector2(this.getPos().x, y));
    }

    public Body getPhysics() {
        return physics;
    }

    public void markFromWorldLoad() {
        this.fromWorldLoad = true;
    }

    public boolean isFromWorldLoad() {
        return this.fromWorldLoad;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public JsonObject write() {
        JsonObject object = new JsonObject();
        object.addProperty("x", this.getPos().x);
        object.addProperty("y", this.getPos().y);
        object.addProperty("uuid", this.getUUID().toString());
        object.addProperty("type", this.getType());

        JsonObject custom = new JsonObject();
        writeAdditional(custom);
        object.add("custom_data", custom);
        return object;
    }

    public abstract void writeAdditional(JsonObject object);

    public abstract String getType();
}
