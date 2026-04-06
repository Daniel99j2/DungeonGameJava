package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.daniel99j.dungeongame.world.Level;
import com.google.gson.JsonObject;

public class SpriteObject extends StaticObject {
    private final String sprite;
    private final PolygonShape hitbox;
    private final Vector2 size;
    private final float scale;

    public SpriteObject(String sprite) {
        this(sprite, 1.0f);
    }

    public SpriteObject(String sprite, float scale) {
        super();
        this.sprite = sprite;
        this.hitbox = new PolygonShape();
        this.size = new Vector2((GameConstants.atlas.findRegion(sprite).packedWidth / 16.0f)*scale, (GameConstants.atlas.findRegion(sprite).packedHeight / 16.0f)*scale);

        this.hitbox.setAsBox(this.size.x/2, this.size.y/2, new Vector2(0.5f*scale, 0.5f*scale), 0);
        this.scale = scale;
    }

    @Override
    public void init(Level level) {
        super.init(level);
        Filter f = new Filter();
        f.categoryBits = (short) (CollisionCategories.LIGHT_BLOCKING | CollisionCategories.WALL);
        this.getPhysics().getFixtureList().get(0).setFilterData(f);
    }

    @Override
    protected PhysicsSettings createPhysics() {
        return new PhysicsSettings(BodyDef.BodyType.StaticBody, this.hitbox, 1.0f, 0.0f);
    }

    @Override
    public void render() {
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion(sprite), this.getPos().x, this.getPos().y, this.size.x, this.size.y);
    }

    @Override
    public void writeAdditional(JsonObject object) {
        object.addProperty("sprite", sprite);
        object.addProperty("scale", scale);
    }

    @Override
    public String getType() {
        return "sprite";
    }

    @Override
    public float getLayer() {
        return RenderLayer.TILESET_OVERLAYS;
    }

    @Override
    public String toString() {
        return "SpriteObject{" +
            "sprite='" + sprite + '\'' +
            ", scale=" + scale +
            '}';
    }
}
