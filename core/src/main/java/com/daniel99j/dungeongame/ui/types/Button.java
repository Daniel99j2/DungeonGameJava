package com.daniel99j.dungeongame.ui.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Align;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.ui.Alignment;
import com.daniel99j.dungeongame.ui.NinePatchLoader;
import com.daniel99j.dungeongame.ui.renderable.ClickType;
import com.daniel99j.dungeongame.ui.renderable.RenderState;
import com.daniel99j.dungeongame.ui.renderable.Renderable;
import com.daniel99j.dungeongame.util.RenderUtil;

public class Button extends Renderable {
    private int width, height;
    private NinePatch ninePatch;
    private String text;
    private float scale;

    public Button(Alignment alignment, int width, int height, float scale, String texture, String text) {
        this.alignment = alignment;
        this.text = text;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.ninePatch = NinePatchLoader.getNinePatch(texture.replace(".png", ""));
        this.usesMouse = true;
    }

    @Override
    public void render(RenderState state) {
        super.render(state);
        GameConstants.spriteBatch.setColor(Color.WHITE);
        this.ninePatch.draw(GameConstants.spriteBatch, this.getX(), GameConstants.height-this.getY()-(this.height*scale), 0, 0, this.width, this.height ,scale ,scale ,0);
        if(!this.text.isBlank()) RenderUtil.renderText(this.text, (int) (this.getX()), GameConstants.height-(int) (this.getY()+this.height*scale*0.5f), 1f, (int) (this.width*scale), Align.center, false);
    }

    @Override
    public boolean isInRange(int x, int y) {
        return x > this.getX() && x < this.getX()+(width*scale) && y > this.getY() && y < this.getY()+(height*scale);
    }

    @Override
    public void onDown(int relativeX, int relativeY, ClickType type) {
        if(type == ClickType.LEFT) onClick();
    }

    public void onClick() {

    }
}
