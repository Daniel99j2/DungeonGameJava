package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.level.Level;

public abstract class AdvancedObject extends AbstractObject {
    @Override
    public void render() {
        Vector2 pos = this.getPos();
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion("player"), pos.x, pos.y, 1, 1);
    }

    @Override
    public void onAdd(boolean fromLoad) {
        super.onAdd(fromLoad);
        this.getLevel().getAdvancedObjects().add(this);
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
}
