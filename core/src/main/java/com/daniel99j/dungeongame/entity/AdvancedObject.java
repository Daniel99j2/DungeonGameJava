package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.world.Level;
import com.google.gson.JsonObject;

public class AdvancedObject extends AbstractObject {
    private Vector2 velocity;

    public AdvancedObject() {
        super();
    }

    @Override
    public void render() {
        Vector2 pos = this.getPos();
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion("player"), pos.x, pos.y, 1, 1);
    }

    @Override
    public void init(Level level) {
        super.init(level);
        level.getAdvancedObjects().add(this);
    }

    public void tick() {

    }

    public Vector2 getVelocity() {
        return this.getPhysics().getLinearVelocity().cpy();
    }

    public void setVelocity(Vector2 velocity) {
        this.getPhysics().setLinearVelocity(velocity);
    }

    @Override
    protected PhysicsSettings createPhysics() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 0.5f);
        return new PhysicsSettings(BodyDef.BodyType.DynamicBody, shape, 1.0f, 1.0f);
    }

    @Override
    public void writeAdditional(JsonObject object) {
    }

    @Override
    public String getType() {
        return "undefined";
    }
}
