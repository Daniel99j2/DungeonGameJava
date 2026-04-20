package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.djutil.Either;
import com.daniel99j.djutil.UsageLimited;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.level.Level;
import com.daniel99j.dungeongame.level.SaveConfig;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractObject implements Disposable {
    private Level level;
    private Either<PositionHolder, Body> physics;
    private boolean fromWorldLoad = false;
    private UUID uuid;
    private boolean removed = false;
    private SaveConfig saveConfig;

    public AbstractObject() {
    }

    @UsageLimited
    public final void init(Level level, boolean fromLoad) {
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
            this.physics.getRight().setUserData(this);
        } else {
            this.physics = Either.left(new PositionHolder());
        }
        if(!fromLoad) this.uuid = UUID.randomUUID();
        if(!fromLoad) onAdd(false);
    }

    public void onAdd(boolean fromLoad) {

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
        object.addProperty("type", this.getType().id());

        JsonObject custom = new JsonObject();
        writeAdditional(custom);
        object.add("custom_data", custom);
        return object;
    }

    public abstract void writeAdditional(JsonObject object);

    public static void readBasic(AbstractObject this1, JsonObject data) {
        this1.setPos(new Vector2(data.get("x").getAsFloat(), data.get("y").getAsFloat()));
        this1.setUuid(UUID.fromString(data.get("uuid").getAsString()));
    }

    public abstract ObjectType<?> getType();

    public abstract float getLayer();

    public SaveConfig getSaveConfig() {
        return saveConfig;
    }

    public void setSaveConfig(SaveConfig saveConfig) {
        this.saveConfig = saveConfig;
    }

    protected void moveTowardTarget(Vector2 targetPosition, float speed) {
        Vector2 currentPosition = this.getPhysics().getPosition();
        Vector2 direction = targetPosition.cpy().sub(currentPosition);

        direction.nor();

        this.getPhysics().setLinearVelocity(direction.x * speed, direction.y * speed);
    }

    public Vector4 getHitbox(Fixture fixture) {
        if (fixture.getType() == Shape.Type.Polygon && ((PolygonShape) fixture.getShape()).getVertexCount() == 4) {
            Transform transform = this.getPhysics().getTransform();

            PolygonShape shape = (PolygonShape) fixture.getShape();
            ArrayList<Vector2> corners = new ArrayList<>();

            // Get world-space corners
            for (int j = 0; j < shape.getVertexCount(); j++) {
                Vector2 localVertex = new Vector2();
                shape.getVertex(j, localVertex);

                Vector2 worldVertex = new Vector2(localVertex);
                transform.mul(worldVertex); // correct usage

                corners.add(worldVertex);
            }

            // Compute bounding box
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;

            for (Vector2 v : corners) {
                if (v.x < minX) minX = v.x;
                if (v.y < minY) minY = v.y;
                if (v.x > maxX) maxX = v.x;
                if (v.y > maxY) maxY = v.y;
            }

            float x = minX;
            float y = minY;
            float w = maxX - minX;
            float h = maxY - minY;

            return new Vector4(x, y, w, h);
        }
        throw new IllegalStateException("Invalid physics setting to get hitbox");
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public int uniqueHash() {
        return Objects.hash(level, physics, fromWorldLoad, uuid, removed, saveConfig);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractObject object = (AbstractObject) o;
        return uuid.equals(object.uuid);
    }
}
