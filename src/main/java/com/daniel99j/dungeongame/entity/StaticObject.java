package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;

public class StaticObject extends AbstractObject {
    private final String sprite;

    public StaticObject(String sprite) {
        super();
        this.sprite = sprite;
    }

    @Override
    protected PhysicsSettings createPhysics() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 0.5f);
        return new PhysicsSettings(BodyDef.BodyType.StaticBody, shape, 1.0f);
    }

    @Override
    public void render() {
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion(sprite), 0, 0, 1, 1);
    }
}
