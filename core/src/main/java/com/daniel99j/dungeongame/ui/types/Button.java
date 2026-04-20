package com.daniel99j.dungeongame.ui.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.ui.NinePatchLoader;
import com.daniel99j.dungeongame.ui.renderable.ClickType;
import com.daniel99j.dungeongame.ui.renderable.RenderState;
import com.daniel99j.dungeongame.ui.renderable.Renderable;

public class Button extends Renderable {
    private int width, height;
    private NinePatch ninePatch;
    private String text;

    public Button(int x, int y, int width, int height, String texture, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.width = width;
        this.height = height;
        this.ninePatch = NinePatchLoader.getNinePatch(texture.replace(".png", ""));
    }

    @Override
    public void render(RenderState state) {
        super.render(state);
        GameConstants.spriteBatch.setColor(Color.WHITE);
        this.ninePatch.draw(GameConstants.spriteBatch, this.x, this.y, this.width, this.height);
    }

    @Override
    public boolean isInRange(int x, int y) {
        return x > this.x && x < this.x+width && y > this.y && y < this.y+height;
    }

    @Override
    public void onDown(int relativeX, int relativeY, ClickType type) {
        if(type == ClickType.LEFT) onClick();
    }

    public void onClick() {

    }
}
