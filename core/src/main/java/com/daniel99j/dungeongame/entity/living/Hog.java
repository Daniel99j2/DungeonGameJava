package com.daniel99j.dungeongame.entity.living;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.ObjectType;
import com.daniel99j.dungeongame.entity.ObjectTypes;
import com.daniel99j.dungeongame.entity.PhysicsSettings;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class Hog extends PathfindingObject {
    private int attackCooldown = 0;

    @Override
    public @Nullable Vector2 getTarget() {
        return GameConstants.player.getPos();
    }

    @Override
    public void writeAdditional(JsonObject object) {

    }

    @Override
    public void tick() {
        super.tick();
        attackCooldown--;
        if (this.getPos().dst(GameConstants.player.getPos()) < 2 && attackCooldown <= 0) {
            GameConstants.player.damage(20);
            attackCooldown = GameConstants.TICKS_PER_SECOND*1;
        }
    }

    @Override
    public void render() {
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion("npc/testdummy"), this.getPos().x, this.getPos().y, 2, 2);
    }

    @Override
    protected PhysicsSettings createPhysics() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1, new Vector2(0.5f, 1), 0);
        return new PhysicsSettings(BodyDef.BodyType.DynamicBody, shape, 1.0f, 1.0f);
    }

    public static Hog read(JsonObject object) {
        return new Hog();
    }

    @Override
    public ObjectType<?> getType() {
        return ObjectTypes.HOG;
    }

    @Override
    public float getLayer() {
        return RenderLayer.NPC;
    }

    @Override
    public float getSpeed() {
        return 1.5f;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
