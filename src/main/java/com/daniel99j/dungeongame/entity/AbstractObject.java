package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.daniel99j.dungeongame.world.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractObject implements Disposable {
    private Level level;
    private Body physics;

    public AbstractObject() {
    }

    public void init(Level level) {
        this.level = level;
        PhysicsSettings settings = createPhysics();
        this.physics = this.level.getBox2dWorld().createBody(new BodyDef());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = settings.shape();
        this.physics.createFixture(fixtureDef);
        settings.shape().dispose();
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
        return this.physics.getPosition();
    }

    public void setPos(Vector2 pos) {
        this.physics.setTransform(pos.x, pos.y, 0);
    }

    public Body getPhysics() {
        return physics;
    }
}
