package com.daniel99j.dungeongame.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.level.SaveConfig;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.RenderLayer;
import com.daniel99j.dungeongame.util.RenderUtil;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

public class PositionMarker extends StaticObject {
    private final Vector2 markerPos;
    private final String markerType;
    private final Consumer<Vector2> onDelete;
    private final Consumer<Vector2> onCreate;
    private final Color colour;

    public PositionMarker(Vector2 pos, Color colour, Consumer<Vector2> onDelete, Consumer<Vector2> onCreate, String markerType) {
        this.markerType = markerType;
        this.setSaveConfig(SaveConfig.NEVER);
        this.markerPos = pos;
        this.onDelete = onDelete;
        this.onCreate = onCreate;
        this.colour = colour;
    }

    public PositionMarker(PositionMarker m) {
        this.markerPos = m.markerPos.cpy();
        this.markerType = m.markerType;
        this.onDelete = m.onDelete;
        this.onCreate = m.onCreate;
        this.setSaveConfig(m.getSaveConfig());
        this.onCreate.accept(this.getPos());
        this.colour = m.colour.cpy();
    }

    public static PositionMarker read(JsonObject object) {
        throw new IllegalStateException();
    }

    @Override
    public Vector2 getPos() {
        return markerPos.cpy();
    }

    @Override
    public void writeAdditional(JsonObject object) {

    }

    @Override
    public ObjectType<?> getType() {
        return ObjectTypes.POS_MARKER;
    }

    @Override
    public float getLayer() {
        return RenderLayer.PLAYER;
    }

    @Override
    public void setPos(Vector2 pos) {
        this.markerPos.set(pos);
    }

    @Override
    protected PhysicsSettings createPhysics() {
        return null;
    }

    @Override
    public void render() {
        if(Debuggers.isEnabled("markers")) {
            GameConstants.shapeRenderer.end();
            GameConstants.spriteBatch.end();
            RenderUtil.enableBlending();
            GameConstants.shapeRenderer.setProjectionMatrix(GameConstants.camera.combined);
            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            GameConstants.shapeRenderer.setColor(this.colour);
            GameConstants.shapeRenderer.circle(markerPos.x, markerPos.y, 0.2f, 20);
            GameConstants.shapeRenderer.end();
            GameConstants.spriteBatch.begin();
            RenderUtil.renderText("e", (int) (GameConstants.camera.position.x+this.markerPos.x), (int) (GameConstants.camera.position.y+this.markerPos.y), 1, 100, 0, true);
        }
    }

    @Override
    public String toString() {
        return this.markerType + " marker";
    }

    public void delete() {
        this.onDelete.accept(markerPos);
    }
}
