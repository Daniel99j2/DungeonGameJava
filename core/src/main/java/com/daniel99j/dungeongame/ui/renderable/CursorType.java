package com.daniel99j.dungeongame.ui.renderable;

import org.lwjgl.glfw.GLFW;

public enum CursorType {
    NORMAL(GLFW.GLFW_ARROW_CURSOR),
    HAND_POINT(GLFW.GLFW_POINTING_HAND_CURSOR);

    private long id;
    private final int style;

    CursorType(int i) {
        this.style = i;
    }

    public long getId() {
        if(id == 0) id = GLFW.glfwCreateStandardCursor(style);
        return id;
    }
}
