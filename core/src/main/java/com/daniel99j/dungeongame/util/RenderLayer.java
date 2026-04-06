package com.daniel99j.dungeongame.util;

public class RenderLayer {
    private static int current = 0;
    public static final float BACKGROUND = get();
    public static final float TILESETS = get();
    public static final float TILESET_OVERLAYS = get();
    public static final float COLLECTABLES = get();
    public static final float DECORATIONS = get();
    public static final float NPC = get();
    public static final float PLAYER = get();
    public static final float FOLIAGE = get();
    public static final float UI = current+1000;

    private static float get() {
        return current++;
    }
}
