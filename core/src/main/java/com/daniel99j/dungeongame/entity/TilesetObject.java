package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.google.gson.JsonObject;

public class TilesetObject extends StaticObject {
    private int width = 1;
    private int height = 1;
    private final String sprite;
    private final Vector2 size;

    public TilesetObject(String sprite, int width, int height) {
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        //slightly extra so that
        this.size = new Vector2((GameConstants.atlas.findRegion(sprite).packedWidth / 16.0f), (GameConstants.atlas.findRegion(sprite).packedHeight / 16.0f));
    }

    @Override
    protected PhysicsSettings createPhysics() {
        return null;
    }

    @Override
    public void render() {
        for (float x = 0; x < this.width*this.size.x; x+=this.size.x) {
            for (float y = 0; y < this.height*this.size.y; y+=this.size.y) {
                GameConstants.spriteBatch.enableBlending();
                //slightly more so that it doesnt have seams
                GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion(sprite), this.getPos().x+x, this.getPos().y+y, this.size.x+0.0001f, this.size.y+0.0001f);
            }
        }
    }

    @Override
    public void writeAdditional(JsonObject object) {
        object.addProperty("width", width);
        object.addProperty("height", height);
        object.addProperty("sprite", sprite);
    }

    public static TilesetObject read(JsonObject object) {
        return new TilesetObject(object.get("sprite").getAsString(), object.get("width").getAsInt(), object.get("height").getAsInt());
    }

    @Override
    public ObjectType<TilesetObject> getType() {
        return ObjectTypes.TILESET;
    }

    @Override
    public float getLayer() {
        return RenderLayer.TILESETS;
    }

    @Override
    public String toString() {
        return "Tileset";
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
