package com.daniel99j.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.daniel99j.dungeongame.entity.Player;
import com.daniel99j.dungeongame.world.Level;
import org.jetbrains.annotations.Nullable;

public class GameConstants {
    public static @Nullable Level level;
    public static final float SECONDS_PER_TICK = 1.0f/20;
    public static final SpriteBatch spriteBatch = new SpriteBatch();
    public static final FitViewport viewport = new FitViewport(8, 5);
    public static final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlases/main.atlas"));
    public static @Nullable Player player;

    public static Level getLevelOrThrow() {
        if(level != null) return level;
        throw new IllegalStateException("World is null");
    }

    protected static void init() {

    }
}
