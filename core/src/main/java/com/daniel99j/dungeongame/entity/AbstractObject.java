package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.djutil.Either;
import com.daniel99j.dungeongame.world.Level;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractObject implements Disposable {
    private Level level;
    private Either<PositionHolder, Body> physics;
    private boolean fromWorldLoad = false;
    private UUID uuid;
    private boolean removed = false;

    public AbstractObject() {
    }

    public void init(Level level) {
        this.level = level;
        PhysicsSettings settings = createPhysics();
        if(settings != null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = settings.bodyType();
            this.physics = Either.right(this.level.getBox2dWorld().createBody(bodyDef));
            this.physics.getRight().setLinearDamping(settings.drag() * 10);
            this.physics.getRight().setFixedRotation(true);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = settings.shape();
            fixtureDef.density = settings.density();
            this.physics.getRight().createFixture(fixtureDef);
            settings.shape().dispose();
        } else {
            this.physics = Either.left(new PositionHolder());
        }
        this.uuid = UUID.randomUUID();
    }

    protected abstract PhysicsSettings createPhysics();

    @Override
    public void dispose() {
        if(this.physics.isRight()) this.getLevel().getBox2dWorld().destroyBody(this.physics.getRight());
        this.physics = null;
        this.level = null;
        this.removed = true;
    }

    public @NotNull Level getLevel() {
        return level;
    }

    public final void renderInternal() {
        if(removed) return;
        render();
    };

    public abstract void render();

    public Vector2 getPos() {
        if(this.removed) return new Vector2();
        if(this.physics.isRight()) return this.physics.getRight().getPosition().cpy();
        else if(this.physics.isLeft()) return this.physics.getLeft().pos.cpy();
        throw new IllegalStateException();
    }

    public void setPos(Vector2 pos) {
        if(this.physics.isRight()) this.physics.getRight().setTransform(pos.x, pos.y, 0);
        else if(this.physics.isLeft()) this.physics.getLeft().pos = pos.cpy();
    }

    public void setX(float x) {
        this.setPos(new Vector2(x, this.getPos().y));
    }

    public void setY(float y) {
        this.setPos(new Vector2(this.getPos().x, y));
    }

    public Body getPhysics() {
        return physics.getRight();
    }

    public boolean hasPhysics() {
        return physics.isRight();
    }

    public void markRemoved() {
        this.removed = true;
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
        if(this.removed) return;
        this.uuid = uuid;
    }

    public JsonObject write() {
        if(this.removed) return new JsonObject();
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

    public abstract float getLayer();
}
