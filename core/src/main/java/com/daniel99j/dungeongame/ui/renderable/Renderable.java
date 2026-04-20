package com.daniel99j.dungeongame.ui.renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;

public class Renderable {
    public int x;
    public int y;
    public boolean usesMouse = false;
    public boolean isLeftDown = false;
    public boolean isMiddleDown = false;
    public boolean isRightDown = false;

    public boolean capturingMouse() {
        return false;
    }

    public boolean isInRange(int x, int y) {
        return false;
    }

    public void render(RenderState state) {
        if(usesMouse) {
            if(isInRange(state.mouseX(), state.mouseY())) {
                if (!isLeftDown && state.leftJust()) {
                    onDown(state.mouseX() - this.x, state.mouseY() - this.y, ClickType.LEFT);
                    isLeftDown = true;
                }
                if (!isMiddleDown && state.middleJust()) {
                    onDown(state.mouseX() - this.x, state.mouseY() - this.y, ClickType.MIDDLE);
                    isMiddleDown = true;
                }
                if (!isRightDown && state.rightJust()) {
                    onDown(state.mouseX() - this.x, state.mouseY() - this.y, ClickType.RIGHT);
                    isRightDown = true;
                }
            }
            if (isLeftDown && !state.left()) {
                onUp(state.mouseX() - this.x, state.mouseY() - this.y, ClickType.LEFT);
                isLeftDown = false;
            }
            if (isMiddleDown && !state.middle()) {
                onUp(state.mouseX() - this.x, state.mouseY() - this.y, ClickType.MIDDLE);
                isMiddleDown = false;
            }
            if (isRightDown && !state.right()) {
                onUp(state.mouseX() - this.x, state.mouseY() - this.y, ClickType.RIGHT);
                isRightDown = false;
            }
        }
    }

    public void onDown(int relativeX, int relativeY, ClickType type) {

    }

    public void onUp(int relativeX, int relativeY, ClickType type) {

    }
}
